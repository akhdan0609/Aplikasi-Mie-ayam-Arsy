package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.remote.UpdateInfo

@Composable
fun UpdateDialog(
  currentVersion: String,
  info: UpdateInfo,
  isDownloading: Boolean,
  downloadProgress: Int,
  error: String?,
  onDismiss: () -> Unit,
  onUpdate: () -> Unit
) {
  AlertDialog(
    onDismissRequest = { if (!isDownloading) onDismiss() },
    shape = RoundedCornerShape(20.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Text(
        text = "Versi Baru Tersedia",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Versi ${info.versionName} sudah tersedia. Versi Anda sekarang: $currentVersion",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (info.changelog.isNotBlank()) {
          Text(
            text = "Apa yang baru:",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = info.changelog,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(max = 160.dp)
              .verticalScroll(rememberScrollState())
          )
        }

        if (isDownloading) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            LinearProgressIndicator(
              progress = { downloadProgress / 100f },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("update_progress")
            )
            Text(
              text = "Mengunduh... $downloadProgress%",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (error != null) {
          Text(
            text = error,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onUpdate,
        enabled = !isDownloading,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("btn_update_now")
      ) {
        Text(if (isDownloading) "Mengunduh..." else "Update Sekarang")
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        enabled = !isDownloading
      ) {
        Text("Nanti")
      }
    }
  )
}
