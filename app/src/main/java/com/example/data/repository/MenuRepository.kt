package com.example.data.repository

import android.net.Uri
import com.example.data.local.MenuItemDao
import com.example.data.local.ShopConfigDao
import com.example.data.model.MenuItemEntity
import com.example.data.model.ShopConfigEntity
import com.example.data.remote.awaitResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MenuRepository(
  private val firestore: FirebaseFirestore,
  private val storage: FirebaseStorage,
  private val menuItemDao: MenuItemDao,
  private val shopConfigDao: ShopConfigDao
) {
  private val menuCollection = firestore.collection("menu")
  private val configDocument = firestore.collection("config").document("shop")

  private val _allMenuItems = MutableStateFlow<List<MenuItemEntity>>(emptyList())
  val allMenuItems: StateFlow<List<MenuItemEntity>> = _allMenuItems.asStateFlow()

  private val _shopConfig = MutableStateFlow(ShopConfigEntity())
  val shopConfig: StateFlow<ShopConfigEntity> = _shopConfig.asStateFlow()

  /** Menarik data terbaru dari cloud. Melempar exception jika gagal (mis. tanpa internet). */
  suspend fun refresh() {
    val menuSnapshot = menuCollection.get().awaitResult()
    _allMenuItems.value = menuSnapshot.documents
      .map { it.toMenuItem() }
      .sortedBy { it.id }

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
    val oldItem = _allMenuItems.value.firstOrNull { it.id == item.id }
    if (oldItem != null && oldItem.imageUrl.isNotBlank() && oldItem.imageUrl != item.imageUrl) {
      runCatching { deleteMenuImage(oldItem.imageUrl) }
    }
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
    val item = _allMenuItems.value.firstOrNull { it.id == id }
    if (item != null && item.imageUrl.isNotBlank()) {
      runCatching { deleteMenuImage(item.imageUrl) }
    }
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
  suspend fun uploadMenuImage(imageUri: Uri): String {
    val ref = storage.reference.child("menu_images/${UUID.randomUUID()}.jpg")
    ref.putFile(imageUri).awaitResult()
    return ref.downloadUrl.awaitResult().toString()
  }

  suspend fun deleteMenuImage(imageUrl: String) {
    if (imageUrl.isBlank()) return
    try {
      val ref = storage.getReferenceFromUrl(imageUrl)
      ref.delete().awaitResult()
    } catch (_: Exception) {
    }
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
  "imageUrl" to imageUrl
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
  imageUrl = getString("imageUrl") ?: ""
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
