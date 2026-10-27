package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MenuItemEntity
import com.example.data.model.PromoSpesial
import com.example.ui.design.rupiah

@Composable
fun PromoEditDialog(
  promo: PromoSpesial?,
  menuItems: List<MenuItemEntity>,
  saveError: String?,
  onDismiss: () -> Unit,
  onSave: (id: Int, judul: String, deskripsi: String, hargaPromo: Long, hargaNormal: Long, menuId: Int, aktif: Boolean) -> Unit
) {
  var judul by remember { mutableStateOf(promo?.judul ?: "") }
  var deskripsi by remember { mutableStateOf(promo?.deskripsi ?: "") }
  var hargaNormal by remember { mutableStateOf(promo?.hargaNormal?.takeIf { it > 0 }?.toString() ?: "") }
  var hargaPromo by remember { mutableStateOf(promo?.hargaPromo?.takeIf { it > 0 }?.toString() ?: "") }
  var menuId by remember { mutableStateOf(promo?.menuId ?: 0) }
  var aktif by remember { mutableStateOf(promo?.aktif ?: true) }
  var menuExpanded by remember { mutableStateOf(false) }
  var validationError by remember { mutableStateOf<String?>(null) }
  val selectedMenu = menuItems.firstOrNull { it.id == menuId }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(if (promo == null) "Tambah Promo" else "Ubah Promo", fontWeight = FontWeight.Bold) },
    text = {
      Column(
        Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box {
          OutlinedTextField(
            value = selectedMenu?.nama ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Menu Utama (foto & tujuan Klaim)") },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
            modifier = Modifier.fillMaxWidth()
          )
          Box(Modifier.matchParentSize().clickable { menuExpanded = true })
          DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            modifier = Modifier.heightIn(max = 280.dp)
          ) {
            menuItems.forEach { item ->
              DropdownMenuItem(
                text = { Text("${item.nama} — ${rupiah(item.harga)}", fontSize = 13.sp) },
                onClick = { menuId = item.id; menuExpanded = false }
              )
            }
          }
        }
        OutlinedTextField(
          value = judul,
          onValueChange = { judul = it },
          label = { Text("Judul Promo (mis. Hemat Berdua Diskon 30%!)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = deskripsi,
          onValueChange = { deskripsi = it },
          label = { Text("Deskripsi Singkat") },
          modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = hargaNormal,
            onValueChange = { hargaNormal = it.filter(Char::isDigit) },
            label = { Text("Harga Normal") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = hargaPromo,
            onValueChange = { hargaPromo = it.filter(Char::isDigit) },
            label = { Text("Harga Promo") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
          )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Switch(
            checked = aktif,
            onCheckedChange = { aktif = it },
            colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
          )
          Spacer(Modifier.width(8.dp))
          Text("Tampilkan di Beranda", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        if (validationError != null) {
          Text(validationError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
        if (saveError != null) {
          Text(saveError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
      }
    },
    confirmButton = {
      Button(onClick = {
        val promoPrice = hargaPromo.toLongOrNull() ?: 0L
        val normalPrice = hargaNormal.toLongOrNull() ?: 0L
        when {
          menuId == 0 -> validationError = "Pilih menu utama terlebih dahulu."
          judul.isBlank() -> validationError = "Judul promo wajib diisi."
          promoPrice <= 0 -> validationError = "Harga promo harus lebih dari 0."
          else -> onSave(promo?.id ?: 0, judul.trim(), deskripsi.trim(), promoPrice, normalPrice, menuId, aktif)
        }
      }) { Text("Simpan") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
  )
}
