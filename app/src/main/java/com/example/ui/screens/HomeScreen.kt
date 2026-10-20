package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.BuildConfig
import com.example.data.model.MenuItemEntity
import com.example.ui.components.AdminEditDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.MenuCard
import com.example.ui.components.OrderSummarySheet
import com.example.ui.components.ShopConfigDialog
import com.example.ui.components.UpdateDialog
import com.example.ui.components.VariantSelectionDialog
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.MenuViewModel
import com.example.ui.viewmodel.UpdateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: MenuViewModel,
  updateViewModel: UpdateViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val updateState by updateViewModel.state.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    updateViewModel.checkForUpdate()
  }

  val menuItems by viewModel.filteredMenuItems.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val shopConfig by viewModel.shopConfig.collectAsStateWithLifecycle()

  val totalCartCount by viewModel.totalCartCount.collectAsStateWithLifecycle()
  val totalCartPrice by viewModel.totalCartPrice.collectAsStateWithLifecycle()
  val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()

  val variantDialogItem by viewModel.variantDialogItem.collectAsStateWithLifecycle()
  val variantOptions by viewModel.variantOptions.collectAsStateWithLifecycle()
  val variantDialogTitle by viewModel.variantDialogTitle.collectAsStateWithLifecycle()

  val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()
  val showLoginDialog by viewModel.showLoginDialog.collectAsStateWithLifecycle()
  val isLoggingIn by viewModel.isLoggingIn.collectAsStateWithLifecycle()
  val loginError by viewModel.loginError.collectAsStateWithLifecycle()
  val showAddEditDialog by viewModel.showAddEditDialog.collectAsStateWithLifecycle()
  val menuItemToEdit by viewModel.menuItemToEdit.collectAsStateWithLifecycle()
  val showShopConfigDialog by viewModel.showShopConfigDialog.collectAsStateWithLifecycle()
  val showOrderSummary by viewModel.showOrderSummary.collectAsStateWithLifecycle()
  val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
  val saveError by viewModel.saveError.collectAsStateWithLifecycle()
  val isUploadingImage by viewModel.isUploadingImage.collectAsStateWithLifecycle()

  val paymentMethod by viewModel.paymentMethod.collectAsStateWithLifecycle()
  val customerName by viewModel.customerName.collectAsStateWithLifecycle()
  val customerNotes by viewModel.customerNotes.collectAsStateWithLifecycle()

  var itemToDelete by remember { mutableStateOf<MenuItemEntity?>(null) }

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiMessage) {
    val message = uiMessage ?: return@LaunchedEffect
    snackbarHostState.showSnackbar(message)
    viewModel.clearUiMessage()
  }

  val categories = listOf("Semua", "Mie Ayam", "Topping", "Minuman")

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Text(
                text = shopConfig.namaWarung,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
              )
            }
          }
        },
        actions = {
          if (!isAdminMode) {
            IconButton(
              onClick = { viewModel.openAdminLoginDialog() },
              modifier = Modifier.testTag("btn_open_admin_login")
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Mode Pengelola",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                modifier = Modifier.padding(end = 4.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Admin",
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }

              IconButton(
                onClick = { viewModel.openShopConfigDialog() },
                modifier = Modifier.testTag("btn_shop_config")
              ) {
                Icon(
                  imageVector = Icons.Default.Settings,
                  contentDescription = "Pengaturan Toko",
                  tint = MaterialTheme.colorScheme.primary
                )
              }

              IconButton(
                onClick = { viewModel.exitAdminMode() },
                modifier = Modifier.testTag("btn_exit_admin")
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Keluar Admin",
                  tint = MaterialTheme.colorScheme.error
                )
              }
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    floatingActionButton = {
      if (isAdminMode) {
        FloatingActionButton(
          onClick = { viewModel.openAddMenuDialog() },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          shape = CircleShape,
          modifier = Modifier
            .padding(bottom = if (totalCartCount > 0) 72.dp else 0.dp)
            .testTag("fab_add_menu")
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Menu")
        }
      }
    },
    bottomBar = {
      // Floating Order Bar at bottom
      AnimatedVisibility(
        visible = totalCartCount > 0 && !isAdminMode,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
      ) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(10.dp, RoundedCornerShape(20.dp)),
          shape = RoundedCornerShape(20.dp),
          color = MaterialTheme.colorScheme.surface,
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.setShowOrderSummary(true) }
              .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              BadgedBox(
                badge = {
                  Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                  ) {
                    Text("$totalCartCount")
                  }
                }
              ) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }

              Column {
                Text(
                  text = "Total Pesanan",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = MenuViewModel.formatRupiah(totalCartPrice),
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.testTag("floating_bar_total")
                )
              }
            }

            Button(
              onClick = { viewModel.setShowOrderSummary(true) },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              modifier = Modifier.testTag("btn_view_cart")
            ) {
              Text("Lihat Pesanan", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Jam Operasional
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Icon(
          imageVector = Icons.Default.AccessTime,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = shopConfig.jamBuka,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      // Admin Active Notice Banner
      if (isAdminMode) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = { viewModel.migrateLocalDataToCloud() },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("btn_migrate_cloud")
          ) {
            Text("Unggah ke Cloud", fontSize = 12.sp)
          }
        }
      }

      // Search Field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = { Text("Cari menu...") },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Search, contentDescription = "Cari")
        },
        trailingIcon = {
          if (searchQuery.isNotBlank()) {
            IconButton(onClick = { viewModel.setSearchQuery("") }) {
              Icon(imageVector = Icons.Default.Clear, contentDescription = "Hapus")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("input_search_menu")
      )

      // Category Horizontal Filter Chips
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(categories) { category ->
          val isSelected = selectedCategory == category
          FilterChip(
            selected = isSelected,
            onClick = { viewModel.setCategory(category) },
            label = {
              Text(
                text = category,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.testTag("filter_chip_$category")
          )
        }
      }

      // Menu List
      if (menuItems.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "Menu tidak ditemukan",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "Coba gunakan kata kunci pencarian yang lain",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 4.dp,
            bottom = if (totalCartCount > 0 && !isAdminMode) 90.dp else 24.dp
          ),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(menuItems, key = { it.id }) { item ->
            val totalQty = viewModel.getItemTotalQuantity(item.id)
            val variantDetails = viewModel.getItemVariantDetails(item.id)

            MenuCard(
              item = item,
              totalQty = totalQty,
              variantDetails = variantDetails,
              isAdminMode = isAdminMode,
              onPlus = { viewModel.handlePlus(item) },
              onMinus = { viewModel.handleMinus(item) },
              onEdit = { viewModel.openEditMenuDialog(item) },
              onToggleStatus = { viewModel.toggleItemStatus(item) },
              onDelete = { itemToDelete = item }
            )
          }
        }
      }
    }
  }

  // Smart Variant Modal (Pedas / Suhu)
  if (variantDialogItem != null) {
    VariantSelectionDialog(
      item = variantDialogItem!!,
      title = variantDialogTitle,
      options = variantOptions,
      onOptionSelected = { variant ->
        viewModel.selectVariant(variant)
      },
      onDismiss = {
        viewModel.closeVariantDialog()
      }
    )
  }

  // Order Summary Bottom Sheet
  if (showOrderSummary) {
    OrderSummarySheet(
      cartItems = cartItems,
      totalPrice = totalCartPrice,
      totalCount = totalCartCount,
      shopConfig = shopConfig,
      paymentMethod = paymentMethod,
      customerName = customerName,
      customerNotes = customerNotes,
      onUpdateQty = { item, qty -> viewModel.updateCartItemQuantity(item, qty) },
      onClearCart = { viewModel.clearCart() },
      onPaymentMethodChange = { viewModel.setPaymentMethod(it) },
      onCustomerNameChange = { viewModel.setCustomerName(it) },
      onCustomerNotesChange = { viewModel.setCustomerNotes(it) },
      onSendOrderWhatsApp = { viewModel.sendOrderViaWhatsApp(context) },
      onDismiss = { viewModel.setShowOrderSummary(false) }
    )
  }

  // Admin Login Dialog
  if (showLoginDialog) {
    AdminLoginDialog(
      isLoggingIn = isLoggingIn,
      error = loginError,
      onDismiss = { viewModel.closeAdminLoginDialog() },
      onSubmit = { email, password -> viewModel.loginAdmin(email, password) }
    )
  }

  // Add / Edit Menu Dialog
  if (showAddEditDialog) {
    AdminEditDialog(
      menuItem = menuItemToEdit,
      saveError = saveError,
      isUploadingImage = isUploadingImage,
      onDismiss = { viewModel.closeAddEditDialog() },
      onUploadImage = { uri, onSuccess, onError ->
        viewModel.uploadMenuImage(uri, onSuccess, onError)
      },
      onSave = { id, nama, kategori, harga, status, deskripsi, hasSpiceLevel, hasDrinkTemp, imageUrl ->
        viewModel.saveMenuItem(
          id, nama, kategori, harga, status, deskripsi, hasSpiceLevel, hasDrinkTemp, imageUrl
        )
      }
    )
  }

  // Shop Config Dialog
  if (showShopConfigDialog) {
    ShopConfigDialog(
      config = shopConfig,
      onDismiss = { viewModel.closeShopConfigDialog() },
      onSave = { namaWarung, nomorWa, infoBank, alamat, jamBuka ->
        viewModel.saveShopConfig(
          namaWarung, nomorWa, infoBank, alamat, jamBuka
        )
      }
    )
  }

  // Update Available Dialog
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

  // Delete Confirmation Dialog
  if (itemToDelete != null) {
    AlertDialog(
      onDismissRequest = { itemToDelete = null },
      title = { Text("Hapus Menu") },
      text = { Text("Apakah Anda yakin ingin menghapus '${itemToDelete?.nama}' dari daftar menu?") },
      confirmButton = {
        Button(
          onClick = {
            itemToDelete?.let { viewModel.deleteMenuItem(it) }
            itemToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Hapus")
        }
      },
      dismissButton = {
        TextButton(onClick = { itemToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}
