package com.example.data.model

/** Kartu promo spesial yang tampil bergeser (carousel) di Beranda. */
data class PromoSpesial(
  val id: Int = 0,
  val judul: String = "",
  val deskripsi: String = "",
  val hargaPromo: Long = 0,
  val hargaNormal: Long = 0,
  val menuId: Int = 0,
  val aktif: Boolean = true
)
