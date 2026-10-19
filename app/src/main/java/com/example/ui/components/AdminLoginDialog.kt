package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AdminLoginDialog(
  isLoggingIn: Boolean,
  error: String?,
  onDismiss: () -> Unit,
  onSubmit: (String, String) -> Unit
) {
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = { if (!isLoggingIn) onDismiss() },
    shape = RoundedCornerShape(20.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Text(
        text = "Masuk Mode Pengelola",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = "Masuk dengan akun admin untuk mengelola menu, harga, dan pengaturan warung:",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email Admin") },
          singleLine = true,
          enabled = !isLoggingIn,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
          modifier = Modifier.fillMaxWidth().testTag("input_admin_email"),
          shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
          value = password,
          onValueChange = { password = it },
          label = { Text("Password") },
          singleLine = true,
          enabled = !isLoggingIn,
          isError = error != null,
          supportingText = {
            if (error != null) {
              Text(text = error, color = MaterialTheme.colorScheme.error)
            }
          },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
          modifier = Modifier.fillMaxWidth().testTag("input_admin_password"),
          shape = RoundedCornerShape(12.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onSubmit(email, password) },
        enabled = !isLoggingIn,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("btn_submit_login")
      ) {
        Text(if (isLoggingIn) "Masuk..." else "Masuk")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss, enabled = !isLoggingIn) {
        Text("Batal")
      }
    }
  )
}
