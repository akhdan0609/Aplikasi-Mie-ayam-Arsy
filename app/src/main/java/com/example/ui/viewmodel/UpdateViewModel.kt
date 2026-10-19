package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.remote.UpdateChecker
import com.example.data.remote.UpdateInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class UpdateUiState(
  val isChecking: Boolean = false,
  val update: UpdateInfo? = null,
  val isDownloading: Boolean = false,
  val downloadProgress: Int = 0,
  val error: String? = null
) {
  val dialogVisible: Boolean get() = update != null
}

class UpdateViewModel(application: Application) : AndroidViewModel(application) {

  private val _state = MutableStateFlow(UpdateUiState())
  val state: StateFlow<UpdateUiState> = _state.asStateFlow()

  private var hasCheckedThisSession = false

  /**
   * Cek update sekali per sesi. Aman dipanggil berkali-kali (mis. dari LaunchedEffect).
   */
  fun checkForUpdate(force: Boolean = false) {
    if ((hasCheckedThisSession && !force) || _state.value.isChecking) return
    hasCheckedThisSession = true
    _state.update { it.copy(isChecking = true, error = null) }

    viewModelScope.launch {
      val info = UpdateChecker.checkForUpdate(BuildConfig.VERSION_NAME)
      _state.update { it.copy(isChecking = false, update = info) }
    }
  }

  fun dismissDialog() {
    if (_state.value.isDownloading) return
    _state.update { it.copy(update = null, error = null, downloadProgress = 0) }
  }

  fun startUpdate() {
    val info = _state.value.update ?: return
    if (_state.value.isDownloading) return

    viewModelScope.launch {
      val context = getApplication<Application>()
      val apkFile = File(context.cacheDir, "mie-ayam-arsy-update.apk")
      if (apkFile.exists()) apkFile.delete()

      _state.update { it.copy(isDownloading = true, downloadProgress = 0, error = null) }

      val success = UpdateChecker.downloadApk(info.apkUrl, apkFile) { progress ->
        _state.update { it.copy(downloadProgress = progress) }
      }

      if (success) {
        _state.update { it.copy(isDownloading = false, downloadProgress = 100) }
        installApk(context, apkFile)
      } else {
        _state.update {
          it.copy(
            isDownloading = false,
            error = "Gagal mengunduh update. Periksa koneksi internet Anda."
          )
        }
      }
    }
  }

  private fun installApk(context: Context, apkFile: File) {
    try {
      val authority = "${context.packageName}.fileprovider"
      val uri = FileProvider.getUriForFile(context, authority, apkFile)
      val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/vnd.android.package-archive")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      _state.update {
        it.copy(error = "Tidak dapat membuka pemasang APK: ${e.message}")
      }
    }
  }
}
