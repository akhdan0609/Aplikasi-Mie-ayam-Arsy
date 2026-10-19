package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.MenuItemEntity
import com.example.data.model.ShopConfigEntity
import com.example.data.remote.awaitResult
import com.example.data.repository.MenuRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale

data class CartItem(
  val item: MenuItemEntity,
  val variant: String,
  val quantity: Int
) {
  val key: String = "${item.id}_$variant"
  val subtotal: Long = item.harga * quantity
}

class MenuViewModel(
  private val repository: MenuRepository,
  private val auth: FirebaseAuth
) : ViewModel() {

  val shopConfig: StateFlow<ShopConfigEntity> = repository.shopConfig

  private val _allMenuItems = repository.allMenuItems
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  private val _selectedCategory = MutableStateFlow("Semua")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  // Filtered menu items based on Category & Search
  val filteredMenuItems: StateFlow<List<MenuItemEntity>> = combine(
    _allMenuItems,
    _selectedCategory,
    _searchQuery
  ) { items, category, query ->
    items.filter { item ->
      val matchesCategory = (category == "Semua") || item.kategori.equals(category, ignoreCase = true)
      val matchesQuery = query.isBlank() ||
        item.nama.contains(query, ignoreCase = true) ||
        item.deskripsi.contains(query, ignoreCase = true)
      matchesCategory && matchesQuery
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Cart state: Map<Key, Quantity> where key is "${menuId}_${variant}"
  private val _cartMap = MutableStateFlow<Map<String, Int>>(emptyMap())
  val cartMap: StateFlow<Map<String, Int>> = _cartMap.asStateFlow()

  // Cart Items with detailed metadata
  val cartItems: StateFlow<List<CartItem>> = combine(
    _allMenuItems,
    _cartMap
  ) { items, cart ->
    val itemMap = items.associateBy { it.id }
    cart.mapNotNull { (key, qty) ->
      if (qty <= 0) return@mapNotNull null
      val parts = key.split("_")
      val id = parts.getOrNull(0)?.toIntOrNull() ?: return@mapNotNull null
      val variant = parts.getOrNull(1) ?: ""
      val menuItem = itemMap[id] ?: return@mapNotNull null
      CartItem(menuItem, variant, qty)
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val totalCartPrice: StateFlow<Long> = cartItems.combine(cartItems) { list, _ ->
    list.sumOf { it.subtotal }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = 0L
  )

  val totalCartCount: StateFlow<Int> = cartItems.combine(cartItems) { list, _ ->
    list.sumOf { it.quantity }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = 0
  )

  // UI state for modals
  private val _variantDialogItem = MutableStateFlow<MenuItemEntity?>(null)
  val variantDialogItem: StateFlow<MenuItemEntity?> = _variantDialogItem.asStateFlow()

  private val _variantOptions = MutableStateFlow<List<String>>(emptyList())
  val variantOptions: StateFlow<List<String>> = _variantOptions.asStateFlow()

  private val _variantDialogTitle = MutableStateFlow("")
  val variantDialogTitle: StateFlow<String> = _variantDialogTitle.asStateFlow()

  // Admin state
  private val _isAdminMode = MutableStateFlow(false)
  val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

  private val _showLoginDialog = MutableStateFlow(false)
  val showLoginDialog: StateFlow<Boolean> = _showLoginDialog.asStateFlow()

  private val _isLoggingIn = MutableStateFlow(false)
  val isLoggingIn: StateFlow<Boolean> = _isLoggingIn.asStateFlow()

  private val _loginError = MutableStateFlow<String?>(null)
  val loginError: StateFlow<String?> = _loginError.asStateFlow()

  private val _showAddEditDialog = MutableStateFlow(false)
  val showAddEditDialog: StateFlow<Boolean> = _showAddEditDialog.asStateFlow()

  private val _menuItemToEdit = MutableStateFlow<MenuItemEntity?>(null)
  val menuItemToEdit: StateFlow<MenuItemEntity?> = _menuItemToEdit.asStateFlow()

  private val _showShopConfigDialog = MutableStateFlow(false)
  val showShopConfigDialog: StateFlow<Boolean> = _showShopConfigDialog.asStateFlow()

  private val _showOrderSummary = MutableStateFlow(false)
  val showOrderSummary: StateFlow<Boolean> = _showOrderSummary.asStateFlow()

  private val _uiMessage = MutableStateFlow<String?>(null)
  val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

  private val _isSyncing = MutableStateFlow(false)
  val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

  private val _paymentMethod = MutableStateFlow("Tunai")
  val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

  private val _customerName = MutableStateFlow("")
  val customerName: StateFlow<String> = _customerName.asStateFlow()

  private val _customerNotes = MutableStateFlow("")
  val customerNotes: StateFlow<String> = _customerNotes.asStateFlow()

  init {
    // Tarik data terbaru dari cloud saat aplikasi dibuka.
    refreshMenu()
  }

  fun refreshMenu() {
    viewModelScope.launch {
      try {
        repository.refresh()
      } catch (e: Exception) {
        // Diamkan; data lama tetap tampil (offline).
      }
    }
  }

  fun setSelectedCategory(category: String) {
    _selectedCategory.value = category
  }

  fun setCategory(category: String) {
    _selectedCategory.value = category
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setCustomerName(name: String) {
    _customerName.value = name
  }

  fun setCustomerNotes(notes: String) {
    _customerNotes.value = notes
  }

  fun setShowOrderSummary(show: Boolean) {
    _showOrderSummary.value = show
  }

  fun setPaymentMethod(method: String) {
    _paymentMethod.value = method
  }

  // Handle + button on menu card
  fun handlePlus(item: MenuItemEntity) {
    if (!item.isAvailable && !_isAdminMode.value) return

    val namaLower = item.nama.lowercase()
    val isJebew = item.hasSpiceLevel || namaLower.contains("jebew") || namaLower.contains("pedas")
    val isDrink = item.hasDrinkTemp || (
      (item.kategori.equals("Minuman", ignoreCase = true) ||
        namaLower.contains("tea") ||
        namaLower.contains("teh") ||
        namaLower.contains("cappucino") ||
        namaLower.contains("milo") ||
        namaLower.contains("jeruk")) &&
        !namaLower.contains("mineral")
      )

    when {
      isJebew -> {
        _variantDialogTitle.value = "Pilih Level Pedas:"
        _variantOptions.value = listOf("Level 1 (Sedang)", "Level 2 (Pedas)", "Level 3 (Super Pedas)")
        _variantDialogItem.value = item
      }
      isDrink -> {
        _variantDialogTitle.value = "Pilih Suhu Minuman:"
        _variantOptions.value = listOf("ICE (Dingin)", "HOT (Panas)")
        _variantDialogItem.value = item
      }
      else -> {
        addItemToCart(item, "")
      }
    }
  }

  fun handleMinus(item: MenuItemEntity) {
    val currentCart = _cartMap.value.toMutableMap()
    val keyToDecrement = currentCart.keys.firstOrNull { it.startsWith("${item.id}_") && (currentCart[it] ?: 0) > 0 }
    if (keyToDecrement != null) {
      val count = (currentCart[keyToDecrement] ?: 0) - 1
      if (count <= 0) {
        currentCart.remove(keyToDecrement)
      } else {
        currentCart[keyToDecrement] = count
      }
      _cartMap.value = currentCart
    }
  }

  fun selectVariant(variant: String) {
    val item = _variantDialogItem.value ?: return
    addItemToCart(item, variant)
    closeVariantDialog()
  }

  fun closeVariantDialog() {
    _variantDialogItem.value = null
    _variantOptions.value = emptyList()
  }

  private fun addItemToCart(item: MenuItemEntity, variant: String) {
    val key = "${item.id}_$variant"
    val current = _cartMap.value.toMutableMap()
    current[key] = (current[key] ?: 0) + 1
    _cartMap.value = current
  }

  fun updateCartItemQuantity(cartItem: CartItem, newQty: Int) {
    val current = _cartMap.value.toMutableMap()
    if (newQty <= 0) {
      current.remove(cartItem.key)
    } else {
      current[cartItem.key] = newQty
    }
    _cartMap.value = current
  }

  fun clearCart() {
    _cartMap.value = emptyMap()
    _customerNotes.value = ""
  }

  fun getItemTotalQuantity(itemId: Int): Int {
    return _cartMap.value.entries
      .filter { it.key.startsWith("${itemId}_") }
      .sumOf { it.value }
  }

  fun getItemVariantDetails(itemId: Int): List<String> {
    return _cartMap.value.entries
      .filter { it.key.startsWith("${itemId}_") && it.value > 0 }
      .mapNotNull { entry ->
        val variant = entry.key.split("_").getOrNull(1) ?: ""
        if (variant.isNotBlank()) "$variant: ${entry.value}" else null
      }
  }

  // Checkout and WhatsApp Order formatting
  fun sendOrderViaWhatsApp(context: Context) {
    val items = cartItems.value
    if (items.isEmpty()) {
      Toast.makeText(context, "Pilih minimal 1 menu terlebih dahulu!", Toast.LENGTH_SHORT).show()
      return
    }

    val config = shopConfig.value
    val targetWa = config.nomorWa.replace("+", "").replace("-", "").replace(" ", "").trim()

    val formattedTotal = formatRupiah(totalCartPrice.value)
    val sb = StringBuilder()
    sb.append("Halo *${config.namaWarung}*, saya ingin memesan:\n\n")

    items.forEach { cartItem ->
      val variantStr = if (cartItem.variant.isNotBlank()) " (${cartItem.variant})" else ""
      val subtotalFormatted = formatRupiah(cartItem.subtotal)
      sb.append("- ${cartItem.item.nama}$variantStr x${cartItem.quantity} = $subtotalFormatted\n")
    }

    sb.append("\n*Total Bayar:* $formattedTotal\n")
    sb.append("*Metode Pembayaran:* ${_paymentMethod.value}\n")

    if (_customerName.value.isNotBlank()) {
      sb.append("*Nama Pemesan:* ${_customerName.value.trim()}\n")
    }
    if (_customerNotes.value.isNotBlank()) {
      sb.append("*Catatan:* ${_customerNotes.value.trim()}\n")
    }

    if (_paymentMethod.value.equals("Transfer", ignoreCase = true)) {
      sb.append("\n*(Saya akan melampirkan bukti transfer setelah pesan ini dikirim)*\n")
    }

    val message = sb.toString()

    try {
      val encoded = URLEncoder.encode(message, "UTF-8")
      val waUri = Uri.parse("https://wa.me/$targetWa?text=$encoded")
      val intent = Intent(Intent.ACTION_VIEW, waUri)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
      _showOrderSummary.value = false
    } catch (e: Exception) {
      Toast.makeText(context, "Tidak dapat membuka WhatsApp: ${e.message}", Toast.LENGTH_LONG).show()
    }
  }

  // Admin login
  fun openAdminLoginDialog() {
    _loginError.value = null
    _showLoginDialog.value = true
  }

  fun closeAdminLoginDialog() {
    _showLoginDialog.value = false
    _loginError.value = null
  }

  fun loginAdmin(email: String, password: String) {
    if (email.isBlank() || password.isBlank()) {
      _loginError.value = "Email dan password wajib diisi."
      return
    }
    viewModelScope.launch {
      _isLoggingIn.value = true
      _loginError.value = null
      try {
        auth.signInWithEmailAndPassword(email.trim(), password).awaitResult()
      } catch (e: Exception) {
        _loginError.value = "Login gagal. Periksa email & password."
        _isLoggingIn.value = false
        return@launch
      }
      _isAdminMode.value = true
      _showLoginDialog.value = false
      _isLoggingIn.value = false
      runCatching { repository.refresh() }
    }
  }

  fun exitAdminMode() {
    auth.signOut()
    _isAdminMode.value = false
  }

  fun openAddMenuDialog() {
    _menuItemToEdit.value = null
    _showAddEditDialog.value = true
  }

  fun openEditMenuDialog(item: MenuItemEntity) {
    _menuItemToEdit.value = item
    _showAddEditDialog.value = true
  }

  fun closeAddEditDialog() {
    _showAddEditDialog.value = false
    _menuItemToEdit.value = null
  }

  fun saveMenuItem(
    id: Int,
    nama: String,
    kategori: String,
    harga: Long,
    status: String,
    deskripsi: String,
    hasSpiceLevel: Boolean,
    hasDrinkTemp: Boolean
  ) {
    viewModelScope.launch {
      val item = MenuItemEntity(
        id = id,
        nama = nama,
        kategori = kategori,
        harga = harga,
        status = status,
        deskripsi = deskripsi,
        hasSpiceLevel = hasSpiceLevel,
        hasDrinkTemp = hasDrinkTemp,
        isBestSeller = _menuItemToEdit.value?.isBestSeller ?: false
      )
      try {
        if (id == 0) repository.insertMenuItem(item) else repository.updateMenuItem(item)
        closeAddEditDialog()
      } catch (e: Exception) {
        _uiMessage.value = "Gagal menyimpan menu: ${e.message}"
      }
    }
  }

  fun toggleItemStatus(item: MenuItemEntity) {
    viewModelScope.launch {
      try {
        repository.toggleMenuItemStatus(item.id, item.status)
      } catch (e: Exception) {
        _uiMessage.value = "Gagal mengubah status: ${e.message}"
      }
    }
  }

  fun deleteMenuItem(item: MenuItemEntity) {
    viewModelScope.launch {
      try {
        repository.deleteMenuItemById(item.id)
      } catch (e: Exception) {
        _uiMessage.value = "Gagal menghapus menu: ${e.message}"
      }
    }
  }

  fun openShopConfigDialog() {
    _showShopConfigDialog.value = true
  }

  fun closeShopConfigDialog() {
    _showShopConfigDialog.value = false
  }

  fun saveShopConfig(
    namaWarung: String,
    nomorWa: String,
    infoBank: String,
    alamat: String,
    jamBuka: String
  ) {
    viewModelScope.launch {
      val updated = shopConfig.value.copy(
        namaWarung = namaWarung,
        nomorWa = nomorWa,
        infoBank = infoBank,
        alamat = alamat,
        jamBuka = jamBuka
      )
      try {
        repository.updateShopConfig(updated)
        closeShopConfigDialog()
      } catch (e: Exception) {
        _uiMessage.value = "Gagal menyimpan pengaturan: ${e.message}"
      }
    }
  }

  fun migrateLocalDataToCloud() {
    viewModelScope.launch {
      _uiMessage.value = "Mengunggah menu ke cloud..."
      try {
        val count = repository.migrateLocalDataToCloud()
        _uiMessage.value = "Berhasil mengunggah $count menu ke cloud."
      } catch (e: Exception) {
        _uiMessage.value = "Gagal mengunggah ke cloud: ${e.message}"
      }
    }
  }

  fun clearUiMessage() {
    _uiMessage.value = null
  }

  companion object {
    fun formatRupiah(amount: Long): String {
      val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
      format.maximumFractionDigits = 0
      return format.format(amount).replace("Rp", "Rp ")
    }
  }
}

class MenuViewModelFactory(
  private val repository: MenuRepository,
  private val auth: FirebaseAuth
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return MenuViewModel(repository, auth) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
