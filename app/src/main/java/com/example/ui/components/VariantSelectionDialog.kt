package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MenuItemEntity

@Composable
fun VariantSelectionDialog(
  item: MenuItemEntity,
  title: String,
  options: List<String>,
  onOptionSelected: (String) -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Column {
        Text(
          text = item.nama,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
      ) {
        options.forEach { option ->
          val isSpicy = option.contains("Level", ignoreCase = true)
          val isHot = option.contains("HOT", ignoreCase = true)
          val isIce = option.contains("ICE", ignoreCase = true)

          Button(
            onClick = { onOptionSelected(option) },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("variant_btn_${option.replace(" ", "_")}"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = when {
                isSpicy && option.contains("3") -> MaterialTheme.colorScheme.tertiary
                isIce -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.primary
              },
              contentColor = when {
                isIce -> MaterialTheme.colorScheme.onPrimaryContainer
                else -> MaterialTheme.colorScheme.onPrimary
              }
            )
          ) {
            Text(
              text = option,
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
              fontSize = 15.sp
            )
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("btn_close_variant")
      ) {
        Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }
  )
}
