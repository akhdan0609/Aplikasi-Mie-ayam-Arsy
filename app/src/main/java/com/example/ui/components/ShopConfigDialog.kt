package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.ShopConfigEntity

@Composable
fun ShopConfigDialog(
  config: ShopConfigEntity,
  onDismiss: () -> Unit,
  onSave: (
    namaWarung: String,
    nomorWa: String,
    infoBank: String,
    alamat: String,
    jamBuka: String,
    newPin: String
  ) -> Unit
) {
  var namaWarung by remember { mutableStateOf(config.namaWarung) }
  var nomorWa by remember { mutableStateOf(config.nomorWa) }
  var infoBank by remember { mutableStateOf(config.infoBank) }
  var alamat by remember { mutableStateOf(config.alamat) }
  var jamBuka by remember { mutableStateOf(config.jamBuka) }
  var newPin by remember { mutableStateOf(config.adminPin) }

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(20.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Text(
        text = "Pengaturan Warung & WhatsApp",
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
        OutlinedTextField(
          value = namaWarung,
          onValueChange = { namaWarung = it },
          label = { Text("Nama Warung") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("config_input_nama"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = nomorWa,
          onValueChange = { nomorWa = it },
          label = { Text("Nomor WhatsApp Tujuan Pesanan") },
          placeholder = { Text("Contoh: 628123456789") },
          supportingText = { Text("Gunakan format awalan 62 tanpa spasi atau tanda +") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("config_input_wa"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = infoBank,
          onValueChange = { infoBank = it },
          label = { Text("Informasi Rekening & Transfer") },
          placeholder = { Text("BCA: 12345678 a.n Arsy\nBRI: ...") },
          minLines = 3,
          modifier = Modifier.fillMaxWidth().testTag("config_input_bank"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = alamat,
          onValueChange = { alamat = it },
          label = { Text("Alamat Warung") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("config_input_alamat"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = jamBuka,
          onValueChange = { jamBuka = it },
          label = { Text("Jam Operasional") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("config_input_jam"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = newPin,
          onValueChange = { newPin = it },
          label = { Text("Ganti PIN Admin (4 Angka)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("config_input_pin"),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(
            namaWarung.trim(),
            nomorWa.trim(),
            infoBank.trim(),
            alamat.trim(),
            jamBuka.trim(),
            newPin.trim()
          )
        },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("config_btn_save")
      ) {
        Text("Simpan Pengaturan")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
