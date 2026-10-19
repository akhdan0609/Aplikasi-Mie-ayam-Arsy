package com.example.data.remote

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

object UpdateChecker {

  private const val GITHUB_REPO = "akhdan0609/Aplikasi-Mie-ayam-Arsy"
  private const val LATEST_RELEASE_URL =
    "https://api.github.com/repos/$GITHUB_REPO/releases/latest"
  private const val USER_AGENT = "MieAyamArsy-Android"

  private val client: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

  private val moshi: Moshi = Moshi.Builder().build()

  /**
   * Mengambil rilis terbaru dan mengembalikan [UpdateInfo] jika versinya lebih baru
   * dari [currentVersionName]. Mengembalikan null jika tidak ada update / gagal (offline).
   */
  suspend fun checkForUpdate(currentVersionName: String): UpdateInfo? =
    withContext(Dispatchers.IO) {
      val release = fetchLatestRelease() ?: return@withContext null
      if (release.draft || release.prerelease) return@withContext null

      val tag = release.tagName ?: return@withContext null
      val latestVersion = tag.removePrefix("v").trim()
      if (!isNewerVersion(latestVersion, currentVersionName)) return@withContext null

      val apkAsset = release.assets
        ?.firstOrNull { it.name?.endsWith(".apk", ignoreCase = true) == true }
        ?: return@withContext null
      val apkUrl = apkAsset.browserDownloadUrl ?: return@withContext null

      UpdateInfo(
        versionName = latestVersion,
        changelog = release.body?.trim().orEmpty(),
        apkUrl = apkUrl,
        releasePageUrl = release.htmlUrl.orEmpty()
      )
    }

  private fun fetchLatestRelease(): GithubRelease? {
    return try {
      val request = Request.Builder()
        .url(LATEST_RELEASE_URL)
        .header("Accept", "application/vnd.github+json")
        .header("User-Agent", USER_AGENT)
        .build()

      client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) return null
        val body = response.body?.string() ?: return null
        moshi.adapter(GithubRelease::class.java).fromJson(body)
      }
    } catch (e: Exception) {
      null
    }
  }

  /**
   * Mengunduh APK dari [url] ke [destination], memanggil [onProgress] (0..100) secara berkala.
   */
  suspend fun downloadApk(
    url: String,
    destination: File,
    onProgress: (Int) -> Unit
  ): Boolean = withContext(Dispatchers.IO) {
    try {
      val request = Request.Builder()
        .url(url)
        .header("User-Agent", USER_AGENT)
        .build()

      client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unduhan gagal (HTTP ${response.code})")
        val body = response.body ?: throw IOException("Isi unduhan kosong")
        val total = body.contentLength()

        body.byteStream().use { input ->
          FileOutputStream(destination).use { output ->
            val buffer = ByteArray(8 * 1024)
            var downloaded = 0L
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
              output.write(buffer, 0, read)
              downloaded += read
              if (total > 0) {
                onProgress(((downloaded * 100) / total).toInt().coerceIn(0, 100))
              }
            }
            output.flush()
          }
        }
      }
      onProgress(100)
      true
    } catch (e: Exception) {
      destination.delete()
      false
    }
  }

  /**
   * Membandingkan versi numerik. Contoh: 1.1.0 > 1.0, 1.0 == 1.0.0.
   */
  fun isNewerVersion(latest: String, current: String): Boolean {
    val a = parseVersion(latest)
    val b = parseVersion(current)
    val size = maxOf(a.size, b.size)
    for (i in 0 until size) {
      val x = a.getOrElse(i) { 0 }
      val y = b.getOrElse(i) { 0 }
      if (x != y) return x > y
    }
    return false
  }

  private fun parseVersion(version: String): List<Int> =
    version.split(".", "-", "+", " ")
      .mapNotNull { it.trim().toIntOrNull() }
      .ifEmpty { listOf(0) }
}
