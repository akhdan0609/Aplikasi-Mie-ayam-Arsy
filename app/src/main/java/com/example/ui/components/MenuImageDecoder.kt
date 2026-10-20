package com.example.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

fun decodeMenuImage(data: String): ImageBitmap? {
  if (data.isBlank()) return null
  return runCatching {
    val bytes = Base64.decode(data, Base64.DEFAULT)
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
  }.getOrNull()
}
