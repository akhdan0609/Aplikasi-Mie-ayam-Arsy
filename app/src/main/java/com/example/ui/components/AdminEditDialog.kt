package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.MenuItemEntity

@Composable
fun AdminEditDialog(
  menuItem: MenuItemEntity?,
  onDismiss: () -> Unit,
  onSave: (
    id: Int,
    nama: String,
    kategori: String,
    harga: Long,
    status: String,
    deskripsi: String,
    hasSpiceLevel: Boolean,
    hasDrinkTemp: Boolean
  ) -> Unit
) {
  val isEdit = menuItem != null

  var nama by remember { mutableStateOf(menuItem?.nama ?: "") }
  var kategori by remember { mutableStateOf(menuItem?.kategori ?: "Mie Ayam") }
  var hargaText by remember { mutableStateOf(menuItem?.harga?.toString() ?: "") }
  var isAvailable by remember { mutableStateOf(menuItem?.isAvailable ?: true) }
  var deskripsi by remember { mutableStateOf(menuItem?.deskripsi ?: "") }
  var hasSpiceLevel by remember { mutableStateOf(menuItem?.hasSpiceLevel ?: false) }
  var hasDrinkTemp by remember { mutableStateOf(menuItem?.hasDrinkTemp ?: false) }

  var errorMessage by remember { mutableStateOf("") }

  val categories = listOf("Mie Ayam", "Topping", "Minuman")

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Text(
        text = if (isEdit) "Ubah Menu" else "Tambah Menu Baru",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        if (errorMessage.isNotBlank()) {
          Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
          )
        }

        OutlinedTextField(
          value = nama,
          onValueChange = { nama = it },
          label = { Text("Nama Menu *") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_input_menu_nama"),
          shape = RoundedCornerShape(12.dp)
        )

        // Category Chips
        Text(
          text = "Kategori *",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { cat ->
            FilterChip(
              selected = kategori == cat,
              onClick = {
                kategori = cat
                if (cat == "Minuman") {
                  hasDrinkTemp = true
                  hasSpiceLevel = false
                }
              },
              label = { Text(cat) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              )
            )
          }
        }

        OutlinedTextField(
          value = hargaText,
          onValueChange = { hargaText = it.filter { char -> char.isDigit() } },
          label = { Text("Harga (Rp) *") },
          placeholder = { Text("Contoh: 15000") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_input_menu_harga"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = deskripsi,
          onValueChange = { deskripsi = it },
          label = { Text("Deskripsi Singkat (Opsional)") },
          placeholder = { Text("Keterangan bahan / kelezatan...") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_input_menu_desc"),
          shape = RoundedCornerShape(12.dp)
        )

        // Availability Switch
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Status Ketersediaan",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
              text = if (isAvailable) "Tersedia untuk dipesan" else "Stok Habis sementara",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Switch(
            checked = isAvailable,
            onCheckedChange = { isAvailable = it },
            colors = SwitchDefaults.colors(
              checkedThumbColor = MaterialTheme.colorScheme.primary,
              checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
          )
        }

        // Smart Variant Options
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { hasSpiceLevel = !hasSpiceLevel },
          verticalAlignment = Alignment.CenterVertically
        ) {
          Checkbox(
            checked = hasSpiceLevel,
            onCheckedChange = { hasSpiceLevel = it },
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Aktifkan Level Pedas (Level 1, 2, 3)",
            style = MaterialTheme.typography.bodySmall
          )
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { hasDrinkTemp = !hasDrinkTemp },
          verticalAlignment = Alignment.CenterVertically
        ) {
          Checkbox(
            checked = hasDrinkTemp,
            onCheckedChange = { hasDrinkTemp = it },
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Aktifkan Opsi Suhu (ICE / HOT)",
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (nama.isBlank()) {
            errorMessage = "Nama menu tidak boleh kosong!"
            return@Button
          }
          val parsedHarga = hargaText.toLongOrNull()
          if (parsedHarga == null || parsedHarga <= 0) {
            errorMessage = "Masukkan nominal harga yang valid!"
            return@Button
          }

          onSave(
            menuItem?.id ?: 0,
            nama.trim(),
            kategori,
            parsedHarga,
            if (isAvailable) "Tersedia" else "Habis",
            deskripsi.trim(),
            hasSpiceLevel,
            hasDrinkTemp
          )
        },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("admin_btn_save_menu")
      ) {
        Text("Simpan")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
