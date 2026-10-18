package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_config")
data class ShopConfigEntity(
  @PrimaryKey
  val id: Int = 1,
  val namaWarung: String = "Mie Ayam Arsy",
  val nomorWa: String = "628123456789", // WhatsApp destination number
  val infoBank: String = "BCA: 1234567890 a.n Arsy\nBRI: 0987654321 a.n Arsy\nQRIS: Tersedia di Kasir",
  val alamat: String = "Jl. Raya Warung Mie Ayam Arsy No. 88",
  val jamBuka: String = "Buka Setiap Hari: 10:00 - 21:00 WIB",
  val adminPin: String = "1234"
)
