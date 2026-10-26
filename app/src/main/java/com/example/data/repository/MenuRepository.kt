package com.example.data.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.data.local.MenuItemDao
import com.example.data.local.ShopConfigDao
import com.example.data.model.MenuItemEntity
import com.example.data.model.ShopConfigEntity
import com.example.data.remote.awaitResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Data contoh agar seluruh layar (Beranda, Menu, Detail, Admin) tetap terisi
 * walaupun Firestore kosong atau perangkat sedang offline.
 */
private val DEMO_MENU = listOf(
  MenuItemEntity(1, "Mie Ayam Spesial Arsy", "Mie Ayam", 22000, deskripsi = "Mie kenyal racikan bumbu khas Arsy dengan topping ayam rica gurih melimpah.", hasSpiceLevel = true, isBestSeller = true),
  MenuItemEntity(2, "Mie Ayam Yamin Manis", "Mie Ayam", 20000, deskripsi = "Manis gurih kecap khas Arsy dengan taburan ayam gurih.", hasSpiceLevel = true, isBestSeller = true),
  MenuItemEntity(3, "Mie Ayam Bakso", "Mie Ayam", 25000, deskripsi = "Disajikan lengkap dengan 2 buah bakso sapi urat.", hasSpiceLevel = true),
  MenuItemEntity(4, "Mie Ayam Original", "Mie Ayam", 14000, deskripsi = "Racikan klasik mie ayam ayam cincang kuah kaldu.", hasSpiceLevel = true),
  MenuItemEntity(5, "Pangsit Goreng Renyah", "Topping", 3000, deskripsi = "Kriuk gurih isi cacahan ayam, teman santap mie.", isBestSeller = true),
  MenuItemEntity(6, "Pangsit Basah Lembut", "Topping", 3000, deskripsi = "Disajikan hangat dengan kuah kaldu."),
  MenuItemEntity(7, "Bakso Sapi Halus", "Topping", 6000, deskripsi = "Daging sapi asli kenyal berserat."),
  MenuItemEntity(8, "Bakso Urat Sapi Jumbo", "Topping", 8000, deskripsi = "Bakso urat jumbo penuh daging."),
  MenuItemEntity(9, "Ceker Ayam Empuk", "Topping", 4000, deskripsi = "Presto lembut bumbu kecap meresap."),
  MenuItemEntity(10, "Telur Puyuh Pindang", "Topping", 3000, deskripsi = "Manis gurih cokelat merica."),
  MenuItemEntity(11, "Es Teh Manis Melati", "Minuman", 5000, deskripsi = "Teh melati pilihan dengan es segar.", hasDrinkTemp = true),
  MenuItemEntity(12, "Es Jeruk Peras Segar", "Minuman", 6000, deskripsi = "Jeruk peras asli tanpa pengawet.", hasDrinkTemp = true)
)

class MenuRepository(
  private val firestore: FirebaseFirestore,
  private val contentResolver: ContentResolver,
  private val menuItemDao: MenuItemDao,
  private val shopConfigDao: ShopConfigDao
) {
  private val menuCollection = firestore.collection("menu")
  private val configDocument = firestore.collection("config").document("shop")

  private val _allMenuItems = MutableStateFlow(DEMO_MENU)
  val allMenuItems: StateFlow<List<MenuItemEntity>> = _allMenuItems.asStateFlow()

  private val _shopConfig = MutableStateFlow(ShopConfigEntity())
  val shopConfig: StateFlow<ShopConfigEntity> = _shopConfig.asStateFlow()

  /** Menarik data terbaru dari cloud. Melempar exception jika gagal (mis. tanpa internet). */
  suspend fun refresh() {
    val menuSnapshot = menuCollection.get().awaitResult()
    val cloudItems = menuSnapshot.documents
      .map { it.toMenuItem() }
      .sortedBy { it.id }
    // Data cloud menimpa data contoh per-id, sisanya diisi DEMO_MENU
    // agar daftar menu tidak tampak kosong walau cloud baru berisi sedikit item.
    if (cloudItems.isNotEmpty()) {
      val cloudIds = cloudItems.map { it.id }.toSet()
      val cloudNames = cloudItems.map { it.nama.trim().lowercase() }.toSet()
      _allMenuItems.value = (cloudItems + DEMO_MENU.filter { demo ->
        demo.id !in cloudIds && demo.nama.trim().lowercase() !in cloudNames
      }).sortedBy { it.id }
    }

    val configSnapshot = configDocument.get().awaitResult()
    if (configSnapshot.exists()) {
      _shopConfig.value = configSnapshot.toShopConfig()
    }
  }

  suspend fun insertMenuItem(item: MenuItemEntity): Long {
    val newId = ((_allMenuItems.value.maxOfOrNull { it.id } ?: 0) + 1).toLong()
    val toSave = item.copy(id = newId.toInt())
    menuCollection.document(newId.toString()).set(toSave.toMap()).awaitResult()
    _allMenuItems.value = (_allMenuItems.value + toSave).sortedBy { it.id }
    return newId
  }

  suspend fun updateMenuItem(item: MenuItemEntity) {
    menuCollection.document(item.id.toString()).set(item.toMap()).awaitResult()
    _allMenuItems.value = _allMenuItems.value.map { if (it.id == item.id) item else it }
  }

  suspend fun toggleMenuItemStatus(id: Int, currentStatus: String) {
    val newStatus = if (currentStatus.equals("Tersedia", ignoreCase = true)) "Habis" else "Tersedia"
    menuCollection.document(id.toString()).update("status", newStatus).awaitResult()
    _allMenuItems.value = _allMenuItems.value.map {
      if (it.id == id) it.copy(status = newStatus) else it
    }
  }

  suspend fun deleteMenuItemById(id: Int) {
    menuCollection.document(id.toString()).delete().awaitResult()
    _allMenuItems.value = _allMenuItems.value.filterNot { it.id == id }
  }

  suspend fun updateShopConfig(config: ShopConfigEntity) {
    configDocument.set(config.toConfigMap()).awaitResult()
    _shopConfig.value = config
  }

  /**
   * Mengunggah seluruh menu & pengaturan warung dari database lokal (Room) ke cloud.
   * @return jumlah menu yang diunggah.
   */
  suspend fun migrateLocalDataToCloud(): Int {
    val localItems = menuItemDao.getAllMenuItemsDirect()
    localItems.forEach { item ->
      menuCollection.document(item.id.toString()).set(item.toMap()).awaitResult()
    }

    val localConfig = shopConfigDao.getShopConfigDirect()
    if (localConfig != null) {
      configDocument.set(localConfig.toConfigMap()).awaitResult()
    }

    refresh()
    return localItems.size
  }

  suspend fun createMenuImage(imageUri: Uri): String = withContext(Dispatchers.IO) {
    val bytes = compressImage(imageUri)
    Base64.encodeToString(bytes, Base64.NO_WRAP)
  }

  private fun compressImage(uri: Uri): ByteArray {
    val original = decodeSampled(uri, MAX_IMAGE_SIDE)
    var maxSide = MAX_IMAGE_SIDE
    var quality = 70
    while (true) {
      val scaled = scaleDown(original, maxSide)
      val output = ByteArrayOutputStream()
      scaled.compress(Bitmap.CompressFormat.JPEG, quality, output)
      val bytes = output.toByteArray()
      if (bytes.size <= MAX_IMAGE_BYTES || (maxSide <= 240 && quality <= 30)) return bytes
      if (quality > 40) {
        quality -= 10
      } else {
        maxSide = (maxSide * 3) / 4
        quality = 60
      }
    }
  }

  private fun decodeSampled(uri: Uri, maxSide: Int): Bitmap {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    var sampleSize = 1
    while (maxOf(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= maxSide) {
      sampleSize *= 2
    }
    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    return contentResolver.openInputStream(uri)?.use {
      BitmapFactory.decodeStream(it, null, options)
    } ?: throw IllegalStateException("Gambar tidak dapat dibaca dari galeri")
  }

  private fun scaleDown(bitmap: Bitmap, maxSide: Int): Bitmap {
    val largest = maxOf(bitmap.width, bitmap.height)
    if (largest <= maxSide) return bitmap
    val ratio = maxSide.toFloat() / largest
    return Bitmap.createScaledBitmap(
      bitmap,
      (bitmap.width * ratio).toInt().coerceAtLeast(1),
      (bitmap.height * ratio).toInt().coerceAtLeast(1),
      true
    )
  }

  private companion object {
    const val MAX_IMAGE_SIDE = 720
    const val MAX_IMAGE_BYTES = 90_000
  }
}

private fun MenuItemEntity.toMap(): Map<String, Any?> = mapOf(
  "id" to id,
  "nama" to nama,
  "kategori" to kategori,
  "harga" to harga,
  "status" to status,
  "deskripsi" to deskripsi,
  "hasSpiceLevel" to hasSpiceLevel,
  "hasDrinkTemp" to hasDrinkTemp,
  "isBestSeller" to isBestSeller,
  "imageUrl" to imageUrl,
  "imageData" to imageData,
  "mieRelation" to mieRelation
)

private fun DocumentSnapshot.toMenuItem(): MenuItemEntity = MenuItemEntity(
  id = getLong("id")?.toInt() ?: id.toIntOrNull() ?: 0,
  nama = getString("nama") ?: "",
  kategori = getString("kategori") ?: "",
  harga = getLong("harga") ?: 0L,
  status = getString("status") ?: "Tersedia",
  deskripsi = getString("deskripsi") ?: "",
  hasSpiceLevel = getBoolean("hasSpiceLevel") ?: false,
  hasDrinkTemp = getBoolean("hasDrinkTemp") ?: false,
  isBestSeller = getBoolean("isBestSeller") ?: false,
  imageUrl = getString("imageUrl") ?: "",
  imageData = getString("imageData") ?: "",
  mieRelation = getString("mieRelation") ?: "NONE"
)

private fun ShopConfigEntity.toConfigMap(): Map<String, Any?> = mapOf(
  "namaWarung" to namaWarung,
  "nomorWa" to nomorWa,
  "infoBank" to infoBank,
  "alamat" to alamat,
  "jamBuka" to jamBuka
)

private fun DocumentSnapshot.toShopConfig(): ShopConfigEntity = ShopConfigEntity(
  id = 1,
  namaWarung = getString("namaWarung") ?: "Mie Ayam Arsy",
  nomorWa = getString("nomorWa") ?: "",
  infoBank = getString("infoBank") ?: "",
  alamat = getString("alamat") ?: "",
  jamBuka = getString("jamBuka") ?: ""
)
