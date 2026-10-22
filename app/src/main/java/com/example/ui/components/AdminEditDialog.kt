package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MenuItemEntity
import com.example.ui.viewmodel.MenuViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminEditDialog(
  menuItem: MenuItemEntity?,
  saveError: String?,
  isUploadingImage: Boolean,
  onDismiss: () -> Unit,
  onUploadImage: (Uri, (String) -> Unit, (String) -> Unit) -> Unit,
  onSave: (
    id: Int,
    nama: String,
    kategori: String,
    harga: Long,
    status: String,
    deskripsi: String,
    hasSpiceLevel: Boolean,
    hasDrinkTemp: Boolean,
    imageData: String,
    mieRelation: String
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
  var imageData by remember { mutableStateOf(menuItem?.imageData ?: "") }
  var mieRelation by remember { mutableStateOf(menuItem?.mieRelation ?: MenuViewModel.MIE_RELATION_NONE) }
  var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
  var localError by remember { mutableStateOf("") }

  val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
    if (uri != null) {
      selectedImageUri = uri
      localError = ""
    }
  }

  val categories = listOf("Mie Ayam", "Topping", "Minuman")

  val mieRelationOptions = listOf(
    "Tidak Terkait" to MenuViewModel.MIE_RELATION_NONE,
    "Menu Utama Mie Ayam" to MenuViewModel.MIE_RELATION_MAIN,
    "Menu Pendukung" to MenuViewModel.MIE_RELATION_SUPPORT
  )

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
        if (localError.isNotBlank() || saveError != null) {
          Text(
            text = saveError ?: localError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
          )
        }

        Text(
          text = "Gambar Menu",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
          ) {
            val displayUri = selectedImageUri
            val decodedImage = remember(imageData) { decodeMenuImage(imageData) }
            if (displayUri != null) {
              AsyncImage(
                model = displayUri,
                contentDescription = "Preview gambar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
              )
            } else if (decodedImage != null) {
              androidx.compose.foundation.Image(
                bitmap = decodedImage,
                contentDescription = "Gambar menu",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
              )
            } else {
              Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedButton(
              onClick = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
              },
              enabled = !isUploadingImage
            ) {
              Text("Pilih Gambar dari Galeri")
            }
            if (selectedImageUri != null || imageData.isNotBlank()) {
              TextButton(
                onClick = {
                  selectedImageUri = null
                  imageData = ""
                },
                enabled = !isUploadingImage
              ) {
                Text("Hapus Gambar")
              }
            }
          }
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

        // Tipe Menu Mie (kaitan dengan mie ayam)
        Text(
          text = "Tipe Menu Mie",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
        )
        FlowRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          mieRelationOptions.forEach { option ->
            FilterChip(
              selected = mieRelation == option.second,
              onClick = { mieRelation = option.second },
              label = { Text(option.first, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
              ),
              modifier = Modifier.testTag("chip_mie_relation_${option.second}")
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
              text = if (isAvailable) "Tersedia" else "Habis",
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
              localError = "Nama menu tidak boleh kosong!"
              return@Button
            }
            val parsedHarga = hargaText.toLongOrNull()
            if (parsedHarga == null || parsedHarga <= 0) {
              localError = "Masukkan nominal harga yang valid!"
              return@Button
            }
            localError = ""

            val statusStr = if (isAvailable) "Tersedia" else "Habis"
            if (selectedImageUri != null) {
              onUploadImage(
                selectedImageUri!!,
                { uploadedUrl ->
                  onSave(
                    menuItem?.id ?: 0,
                    nama.trim(),
                    kategori,
                    parsedHarga,
                    statusStr,
                    deskripsi.trim(),
                    hasSpiceLevel,
                    hasDrinkTemp,
                    uploadedUrl,
                    mieRelation
                  )
                },
                { err ->
                  localError = err
                }
              )
            } else {
              onSave(
                menuItem?.id ?: 0,
                nama.trim(),
                kategori,
                parsedHarga,
                statusStr,
                deskripsi.trim(),
                hasSpiceLevel,
                hasDrinkTemp,
                imageData,
                mieRelation
              )
            }
          },
          shape = RoundedCornerShape(10.dp),
          enabled = !isUploadingImage,
          modifier = Modifier.testTag("admin_btn_save_menu")
        ) {
          if (isUploadingImage) {
            CircularProgressIndicator(
              modifier = Modifier.size(18.dp),
              strokeWidth = 2.dp,
              color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
          }
          Text(if (isUploadingImage) "Mengunggah..." else "Simpan")
        }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
