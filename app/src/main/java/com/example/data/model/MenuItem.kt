package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_items")
data class MenuItemEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val nama: String,
  val kategori: String, // "Mie Ayam", "Topping", "Minuman"
  val harga: Long,
  val status: String = "Tersedia", // "Tersedia" or "Habis"
  val deskripsi: String = "",
  val hasSpiceLevel: Boolean = false, // Levels 1, 2, 3
  val hasDrinkTemp: Boolean = false, // ICE (Dingin), HOT (Panas)
  val isBestSeller: Boolean = false,
  val imageUrl: String = "",
  val imageData: String = "",
  val mieRelation: String = "NONE"
) {
  val isAvailable: Boolean
    get() = status.equals("Tersedia", ignoreCase = true)
}
