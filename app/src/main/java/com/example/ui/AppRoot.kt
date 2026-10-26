package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.BuildConfig
import com.example.data.model.MenuItemEntity
import com.example.ui.components.AdminEditDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.UpdateDialog
import com.example.ui.components.VariantSelectionDialog
import com.example.ui.design.AdminTab
import com.example.ui.design.CustomerTab
import com.example.ui.design.OrderRecord
import com.example.ui.design.rupiah
import com.example.ui.screens.AdminMenuStockScreen
import com.example.ui.screens.AdminOrdersScreen
import com.example.ui.screens.AdminReportsScreen
import com.example.ui.screens.AdminSettingsScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CustomerHomeScreen
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.MenuBrowseScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.ArsyCardBorder
import com.example.ui.theme.ArsyCream
import com.example.ui.theme.ArsyGoldSoft
import com.example.ui.theme.ArsyPeach
import com.example.ui.theme.ArsyTextMuted
import com.example.ui.viewmodel.MenuViewModel
import com.example.ui.viewmodel.UpdateViewModel

private val ORDER_FLOW = listOf("Baru Masuk", "Dimasak", "Siap Saji", "Selesai")

@Composable
fun ArsyApp(viewModel: MenuViewModel, updateViewModel: UpdateViewModel) {
  val context = LocalContext.current
  val isAdmin by viewModel.isAdminMode.collectAsStateWithLifecycle()

  var custTab by remember { mutableStateOf(CustomerTab.Home) }
  var adminTab by remember { mutableStateOf(AdminTab.Orders) }
  var detailItem by remember { mutableStateOf<MenuItemEntity?>(null) }
  var showCart by remember { mutableStateOf(false) }

  val orders = remember { mutableStateListOf<OrderRecord>() }
  fun advance(code: String) {
    val i = orders.indexOfFirst { it.code == code }
    if (i >= 0) {
      val cur = orders[i].status
      val next = ORDER_FLOW.getOrNull(ORDER_FLOW.indexOf(cur).coerceAtLeast(0) + 1) ?: cur
      orders[i] = orders[i].copy(status = next)
    }
  }

  // Shared dialog + snackbar state
  val variantDialogItem by viewModel.variantDialogItem.collectAsStateWithLifecycle()
  val variantOptions by viewModel.variantOptions.collectAsStateWithLifecycle()
  val variantDialogTitle by viewModel.variantDialogTitle.collectAsStateWithLifecycle()
  val showLoginDialog by viewModel.showLoginDialog.collectAsStateWithLifecycle()
  val isLoggingIn by viewModel.isLoggingIn.collectAsStateWithLifecycle()
  val loginError by viewModel.loginError.collectAsStateWithLifecycle()
  val showAddEditDialog by viewModel.showAddEditDialog.collectAsStateWithLifecycle()
  val menuItemToEdit by viewModel.menuItemToEdit.collectAsStateWithLifecycle()
  val saveError by viewModel.saveError.collectAsStateWithLifecycle()
  val isUploadingImage by viewModel.isUploadingImage.collectAsStateWithLifecycle()
  val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
  val cartCount by viewModel.totalCartCount.collectAsStateWithLifecycle()
  val updateState by updateViewModel.state.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }
  LaunchedEffect(uiMessage) {
    uiMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearUiMessage() }
  }
  LaunchedEffect(Unit) { updateViewModel.checkForUpdate() }

  var itemToDelete by remember { mutableStateOf<MenuItemEntity?>(null) }

  Scaffold(
    containerColor = ArsyCream,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      ArsyTopBar(
        isAdmin = isAdmin,
        cartCount = cartCount,
        onCartClick = { showCart = true },
        onProfileClick = { custTab = CustomerTab.Profile },
        onAdminLogin = { viewModel.openAdminLoginDialog() },
        onExitAdmin = { viewModel.exitAdminMode() }
      )
    },
    bottomBar = {
      if (isAdmin) ArsyAdminBottomBar(selected = adminTab, onSelect = { adminTab = it })
      else ArsyCustomerBottomBar(selected = custTab, onSelect = { custTab = it; detailItem = null; showCart = false })
    }
  ) { inner ->
    Box(Modifier.fillMaxSize().padding(inner)) {
      when {
        showCart -> CartScreen(vm = viewModel, onBack = { showCart = false }, onOrderPlaced = { orders.add(0, it) })
        detailItem != null -> DetailScreen(vm = viewModel, item = detailItem!!, onBack = { detailItem = null }, onOpenCart = { detailItem = null; showCart = true })
        isAdmin -> when (adminTab) {
          AdminTab.Orders -> AdminOrdersScreen(viewModel, orders.toList(), ::advance)
          AdminTab.MenuStock -> AdminMenuStockScreen(viewModel)
          AdminTab.Reports -> AdminReportsScreen(viewModel, orders.toList())
          AdminTab.Settings -> AdminSettingsScreen(viewModel, onExitToCustomer = { viewModel.exitAdminMode() })
        }
        else -> when (custTab) {
          CustomerTab.Home -> CustomerHomeScreen(viewModel, onOpenDetail = { detailItem = it }, onGoMenu = { custTab = CustomerTab.Menu }, onOpenCart = { showCart = true })
          CustomerTab.Menu -> MenuBrowseScreen(viewModel, onOpenDetail = { detailItem = it })
          CustomerTab.Orders -> OrdersScreen(orders.toList(), onBackToMenu = { custTab = CustomerTab.Menu })
          CustomerTab.Profile -> ProfileScreen(
            viewModel,
            orders.toList(),
            onOpenAdminLogin = { viewModel.openAdminLoginDialog() },
            onGoMenu = { custTab = CustomerTab.Menu },
            onOpenOrders = { custTab = CustomerTab.Orders }
          )
        }
      }
    }
  }

  // Admin floating add-menu button
  if (isAdmin && adminTab == AdminTab.MenuStock) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
      Surface(
        modifier = Modifier.padding(end = 20.dp, bottom = 100.dp).size(56.dp).clip(CircleShape).clickable { viewModel.openAddMenuDialog() },
        color = MaterialTheme.colorScheme.primary,
        shape = CircleShape
      ) {
        Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Add, null, tint = Color.White) }
      }
    }
  }

  // ---- Shared dialogs ----
  if (variantDialogItem != null) {
    VariantSelectionDialog(
      item = variantDialogItem!!,
      title = variantDialogTitle,
      options = variantOptions,
      onOptionSelected = { viewModel.selectVariant(it) },
      onDismiss = { viewModel.closeVariantDialog() }
    )
  }
  if (showLoginDialog) {
    AdminLoginDialog(
      isLoggingIn = isLoggingIn,
      error = loginError,
      onDismiss = { viewModel.closeAdminLoginDialog() },
      onSubmit = { email, password -> viewModel.loginAdmin(email, password) }
    )
  }
  if (showAddEditDialog) {
    AdminEditDialog(
      menuItem = menuItemToEdit,
      saveError = saveError,
      isUploadingImage = isUploadingImage,
      onDismiss = { viewModel.closeAddEditDialog() },
      onUploadImage = { uri, onSuccess, onError -> viewModel.uploadMenuImage(uri, onSuccess, onError) },
      onSave = { id, nama, kategori, harga, status, deskripsi, hasSpiceLevel, hasDrinkTemp, imageData, mieRelation, isBestSeller ->
        viewModel.saveMenuItem(id, nama, kategori, harga, status, deskripsi, hasSpiceLevel, hasDrinkTemp, imageData, mieRelation, isBestSeller)
      }
    )
  }
  updateState.update?.let { info ->
    UpdateDialog(
      currentVersion = BuildConfig.VERSION_NAME,
      info = info,
      isDownloading = updateState.isDownloading,
      downloadProgress = updateState.downloadProgress,
      error = updateState.error,
      onDismiss = { updateViewModel.dismissDialog() },
      onUpdate = { updateViewModel.startUpdate() }
    )
  }
  if (itemToDelete != null) {
    AlertDialog(
      onDismissRequest = { itemToDelete = null },
      title = { Text("Hapus Menu") },
      text = { Text("Hapus '${itemToDelete?.nama}' dari daftar menu?") },
      confirmButton = {
        Button(
          onClick = { itemToDelete?.let { viewModel.deleteMenuItem(it) }; itemToDelete = null },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) { Text("Hapus") }
      },
      dismissButton = { TextButton(onClick = { itemToDelete = null }) { Text("Batal") } }
    )
  }
}

@Composable
private fun ArsyTopBar(
  isAdmin: Boolean,
  cartCount: Int,
  onCartClick: () -> Unit,
  onProfileClick: () -> Unit,
  onAdminLogin: () -> Unit,
  onExitAdmin: () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth().statusBarsPadding(),
    color = ArsyCream,
    tonalElevation = 0.dp
  ) {
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.Restaurant, null, tint = Color.White, modifier = Modifier.size(22.dp))
      }
      Spacer(Modifier.width(10.dp))
      Column(Modifier.weight(1f)) {
        Text("Mie Ayam Arsy", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
        if (isAdmin) Text("Mode Pengelola • POS", fontSize = 11.sp, color = ArsyTextMuted)
      }
      if (!isAdmin) {
        Box(contentAlignment = Alignment.TopEnd) {
          Box(Modifier.size(42.dp).clip(CircleShape).background(Color.White).clickable(onClick = onCartClick), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.ShoppingBag, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
          }
          if (cartCount > 0) {
            Box(Modifier.size(18.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
              Text("$cartCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
        Spacer(Modifier.width(8.dp))
        Box(Modifier.size(42.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary).clickable(onClick = onProfileClick), contentAlignment = Alignment.Center) {
          Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
      } else {
        Box(Modifier.clip(RoundedCornerShape(20.dp)).background(Color.White).padding(horizontal = 12.dp, vertical = 8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF2E7D32)))
            Spacer(Modifier.width(6.dp))
            Text("Buka", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
          }
        }
        Spacer(Modifier.width(8.dp))
        Box(Modifier.size(42.dp).clip(CircleShape).background(Color.White).clickable(onClick = onExitAdmin), contentAlignment = Alignment.Center) {
          Icon(Icons.Filled.Close, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
        }
      }
    }
  }
}

@Composable
private fun ArsyCustomerBottomBar(selected: CustomerTab, onSelect: (CustomerTab) -> Unit) {
  val insets = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
  Surface(color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, ArsyCardBorder)) {
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 10.dp).padding(top = 8.dp).padding(bottom = 8.dp + insets),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      CustomerTab.entries.forEach { tab ->
        val active = tab == selected
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onSelect(tab) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Icon(tab.icon, null, tint = if (active) Color.White else ArsyTextMuted, modifier = Modifier.size(22.dp))
          Spacer(Modifier.height(2.dp))
          Text(tab.label, fontSize = 11.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium, color = if (active) Color.White else ArsyTextMuted)
        }
      }
    }
  }
}

@Composable
private fun ArsyAdminBottomBar(selected: AdminTab, onSelect: (AdminTab) -> Unit) {
  val insets = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
  Surface(color = Color.White, border = androidx.compose.foundation.BorderStroke(1.dp, ArsyCardBorder)) {
    Row(
      Modifier.fillMaxWidth().padding(horizontal = 10.dp).padding(top = 8.dp).padding(bottom = 8.dp + insets),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      AdminTab.entries.forEach { tab ->
        val active = tab == selected
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) ArsyPeach else Color.Transparent)
            .clickable { onSelect(tab) }
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Icon(tab.icon, null, tint = if (active) MaterialTheme.colorScheme.primary else ArsyTextMuted, modifier = Modifier.size(22.dp))
          Spacer(Modifier.height(2.dp))
          Text(tab.label, fontSize = 10.5.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium, color = if (active) MaterialTheme.colorScheme.primary else ArsyTextMuted, maxLines = 1)
        }
      }
    }
  }
}
