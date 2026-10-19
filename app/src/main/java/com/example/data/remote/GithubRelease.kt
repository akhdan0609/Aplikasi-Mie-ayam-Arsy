package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GithubRelease(
  @Json(name = "tag_name") val tagName: String?,
  @Json(name = "name") val name: String?,
  @Json(name = "body") val body: String?,
  @Json(name = "html_url") val htmlUrl: String?,
  @Json(name = "assets") val assets: List<GithubAsset>? = emptyList(),
  @Json(name = "draft") val draft: Boolean = false,
  @Json(name = "prerelease") val prerelease: Boolean = false
)

@JsonClass(generateAdapter = true)
data class GithubAsset(
  @Json(name = "name") val name: String?,
  @Json(name = "browser_download_url") val browserDownloadUrl: String?
)

data class UpdateInfo(
  val versionName: String,
  val changelog: String,
  val apkUrl: String,
  val releasePageUrl: String
)
