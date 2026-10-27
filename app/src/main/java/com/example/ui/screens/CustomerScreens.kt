package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.MenuItemEntity
import com.example.ui.design.CheckRow
import com.example.ui.design.EmptyState
import com.example.ui.design.FilterPill
import com.example.ui.design.GoldChip
import com.example.ui.design.MenuPhoto
import com.example.ui.design.OrderLine
import com.example.ui.design.OrderRecord
import com.example.ui.design.PrimaryButton
import com.example.ui.design.QtyStepper
import com.example.ui.design.RoundIconButton
import com.example.ui.design.SectionHeader
import com.example.ui.design.StatTile
import com.example.ui.design.TagChip
import com.example.ui.design.WhiteCard
import com.example.ui.design.rupiah
import com.example.ui.theme.ArsyCardBorder
import com.example.ui.theme.ArsyDanger
import com.example.ui.theme.ArsyGold
import com.example.ui.theme.ArsyGoldSoft
import com.example.ui.theme.ArsyPeach
import com.example.ui.theme.ArsySuccess
import com.example.ui.theme.ArsyTextMuted
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.MenuViewModel
import java.net.URLEncoder

private fun openWhatsApp(context: Context, number: String, message: String) {
  val target = number.replace("+", "").replace("-", "").replace(" ", "").trim()
  val encoded = URLEncoder.encode(message, "UTF-8")
  runCatching {
    context.startActivity(
      Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$target?text=$encoded")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
  }
}

// ============================ HOME ============================

@Composable
fun CustomerHomeScreen(
  vm: MenuViewModel,
  onOpenDetail: (MenuItemEntity) -> Unit,
  onGoMenu: () -> Unit,
  onOpenCart: () -> Unit
) {
  val context = LocalContext.current
  val shopConfig by vm.shopConfig.collectAsStateWithLifecycle()
  val allItems by vm.filteredMenuItems.collectAsStateWithLifecycle()
  val cartCount by vm.totalCartCount.collectAsStateWithLifecycle()

  val prefs = remember { context.getSharedPreferences("profil_pelanggan", Context.MODE_PRIVATE) }
  val namaPelanggan = remember { prefs.getString("nama", null) }
  var metode by remember { mutableStateOf("Makan di Tempat") }
  var metodeMenu by remember { mutableStateOf(false) }

  val bestSellers = remember(allItems) { allItems.filter { it.isBestSeller }.ifEmpty { allItems }.take(6) }
  val categories = listOf("Semua", "Mie Ayam", "Best Seller", "Topping", "Minuman")

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
  ) {
    item {
      Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(7.dp).clip(CircleShape).background(ArsyGold))
            Spacer(Modifier.width(7.dp))
            Text("Selamat Datang Kembali", fontSize = 11.sp, color = ArsyTextMuted, fontWeight = FontWeight.Medium)
          }
          Text("Halo, ${namaPelanggan ?: "Pelanggan"}!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
          Text("Mau makan mie ayam apa hari ini?", fontSize = 13.sp, color = ArsyTextMuted)
        }
        Spacer(Modifier.width(10.dp))
        OpenHoursPill(shopConfig.jamBuka)
      }
      Spacer(Modifier.height(12.dp))
      WhiteCard(contentPadding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(ArsyPeach), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Restaurant, null, tint = MaterialTheme.colorScheme.primary)
          }
          Spacer(Modifier.width(12.dp))
          Column(Modifier.weight(1f)) {
            Text("Metode Pemesanan", fontSize = 11.sp, color = ArsyTextMuted)
            Text(
              if (metode == "Makan di Tempat") "Makan di Tempat • Meja 04" else metode,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          }
          Box(Modifier.clip(RoundedCornerShape(50)).background(Color.White).border(1.dp, ArsyCardBorder, RoundedCornerShape(50)).clickable { metodeMenu = true }.padding(horizontal = 12.dp, vertical = 7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("Ganti", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
              Icon(Icons.Filled.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
            DropdownMenu(expanded = metodeMenu, onDismissRequest = { metodeMenu = false }) {
              listOf("Makan di Tempat", "Take Away", "Delivery").forEach { m ->
                DropdownMenuItem(text = { Text(m) }, onClick = { metode = m; metodeMenu = false })
              }
            }
          }
        }
      }
    }

    item {
      Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(Color.White).clickable(onClick = onGoMenu).padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Search, null, tint = ArsyTextMuted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Cari mie ayam komplit, bakso, ceker...", fontSize = 13.sp, color = ArsyTextMuted)
          }
        }
        Spacer(Modifier.width(10.dp))
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
          Icon(Icons.Filled.Tune, null, tint = Color.White)
        }
      }
    }

    // Promo banner
    item {
      Column(Modifier.padding(horizontal = 16.dp)) {
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))) {
          Image(
            painter = painterResource(R.drawable.mie_ayam_spesial),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
          )
          Box(
            Modifier.matchParentSize().background(
              androidx.compose.ui.graphics.Brush.horizontalGradient(
                listOf(Color(0xF0743A18), Color(0x9C8A4513), Color(0x33B4530A))
              )
            )
          )
          Column(Modifier.padding(18.dp)) {
            Row {
              GoldChip("PROMO SPESIAL")
              Spacer(Modifier.weight(1f))
              Text("Hari Ini Saja", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
            }
            Spacer(Modifier.height(10.dp))
            Text("Hemat Berdua Diskon 30%!", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("2x Mie Ayam Spesial + 2x Es Teh Manis segar pelepas dahaga.", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("Rp 48.000", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = ArsyGoldSoft)
              Spacer(Modifier.width(8.dp))
              Text("Rp 68.000", fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f))
              Spacer(Modifier.weight(1f))
              Box(Modifier.clip(RoundedCornerShape(12.dp)).background(ArsyGold).clickable(onClick = onGoMenu).padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text("Klaim & Pesan", fontWeight = FontWeight.Bold, color = Color(0xFF4A2412), fontSize = 13.sp)
              }
            }
          }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
          Box(Modifier.width(18.dp).height(5.dp).clip(RoundedCornerShape(3.dp)).background(MaterialTheme.colorScheme.primary))
          Spacer(Modifier.width(5.dp))
          Box(Modifier.size(5.dp).clip(CircleShape).background(ArsyTextMuted.copy(alpha = 0.35f)))
          Spacer(Modifier.width(5.dp))
          Box(Modifier.size(5.dp).clip(CircleShape).background(ArsyTextMuted.copy(alpha = 0.35f)))
        }
      }
    }

    item { SectionHeader("Kategori Menu", action = "Pilihan Favorit", onAction = onGoMenu) }
    item {
      LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(categories) { cat ->
          val selected = cat == "Semua"
          Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(76.dp).clickable(onClick = onGoMenu)) {
            Box(
              Modifier.size(60.dp).clip(RoundedCornerShape(18.dp))
                .background(if (selected) MaterialTheme.colorScheme.primary else categoryTint(cat)),
              contentAlignment = Alignment.Center
            ) {
              Icon(categoryIcon(cat), null, tint = if (selected) Color.White else MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(6.dp))
            Text(cat, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground, maxLines = 1)
          }
        }
      }
    }

    item { SectionHeader("Paling Laris di Warung", subtitle = "Racikan bumbu autentik paling dicari", action = "Lihat Semua", onAction = onGoMenu) }
    items(bestSellers, key = { "home_" + it.id }) { item ->
      HomeListItem(
        item = item,
        onClick = { onOpenDetail(item) },
        onAdd = { vm.handlePlus(item) }
      )
    }

    item {
      WhiteCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(Modifier.size(44.dp).clip(CircleShape).background(ArsyPeach), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Verified, null, tint = MaterialTheme.colorScheme.primary)
          }
          Spacer(Modifier.width(12.dp))
          Column {
            Text("Resep Asli Sejak 2018", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("Dimasak segar setiap hari dengan bahan alami.", fontSize = 12.sp, color = ArsyTextMuted)
          }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          BadgePill("100% Halal", Icons.Filled.CheckCircle)
          BadgePill("Tanpa Pengawet", Icons.Filled.CheckCircle)
        }
        Spacer(Modifier.height(12.dp))
        PrimaryButton(
          text = "Chat WhatsApp Warung Mie Ayam",
          onClick = { openWhatsApp(context, shopConfig.nomorWa, "Halo ${shopConfig.namaWarung}, saya ingin bertanya.") },
          icon = Icons.Filled.Storefront,
          containerColor = Color(0xFFEFE9E1),
          contentColor = MaterialTheme.colorScheme.onBackground
        )
      }
    }
  }
}

@Composable
private fun BadgePill(text: String, icon: ImageVector) {
  Row(
    Modifier.clip(RoundedCornerShape(10.dp)).background(Color.White).border1().padding(horizontal = 12.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(icon, null, tint = ArsySuccess, modifier = Modifier.size(16.dp))
    Spacer(Modifier.width(6.dp))
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
  }
}

private fun Modifier.border1(): Modifier = this.then(
  Modifier.background(Color.White)
)

@Composable
private fun categoryIcon(cat: String): ImageVector = when (cat) {
  "Semua" -> Icons.Filled.Apps
  "Mie Ayam" -> Icons.Filled.RamenDining
  "Best Seller" -> Icons.Filled.LocalFireDepartment
  "Topping" -> Icons.Filled.AddCircle
  "Minuman" -> Icons.Filled.Coffee
  else -> Icons.Filled.Restaurant
}

private fun categoryTint(cat: String): Color = when (cat) {
  "Mie Ayam" -> ArsyGoldSoft
  "Best Seller" -> Color(0xFFFBD5D5)
  "Topping" -> Color(0xFFE7E2DC)
  "Minuman" -> ArsyPeach
  else -> ArsyPeach
}

@Composable
private fun HomeListItem(item: MenuItemEntity, onClick: () -> Unit, onAdd: () -> Unit) {
  WhiteCard(
    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
    contentPadding = PaddingValues(12.dp),
    onClick = onClick
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box {
        MenuPhoto(item, Modifier.size(78.dp))
        homeBadge(item.id)?.let { (label, bg, fg) ->
          Box(
            Modifier.align(Alignment.TopStart).padding(4.dp)
              .clip(RoundedCornerShape(6.dp)).background(bg)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = fg)
          }
        }
      }
      Spacer(Modifier.width(12.dp))
      Column(Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(item.nama, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
          Icon(Icons.Filled.Star, null, tint = ArsyGold, modifier = Modifier.size(14.dp))
          Spacer(Modifier.width(2.dp))
          Text("4.9", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(item.deskripsi.ifBlank { "Racikan spesial warung Arsy" }, fontSize = 12.sp, color = ArsyTextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        homeOrdersLabel(item.id)?.let {
          Text(it, fontSize = 10.5.sp, color = ArsyTextMuted)
        }
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(rupiah(item.harga), fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, modifier = Modifier.weight(1f))
          Box(Modifier.clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary).clickable(onClick = onAdd).padding(horizontal = 12.dp, vertical = 7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Filled.Add, null, tint = Color.White, modifier = Modifier.size(14.dp))
              Spacer(Modifier.width(2.dp))
              Text("Tambah", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

private fun homeBadge(id: Int): Triple<String, Color, Color>? = when (id) {
  1 -> Triple("Favorit", Color(0xFFC62828), Color.White)
  2 -> Triple("Yamin", ArsyGold, Color(0xFF4A2412))
  5 -> Triple("Kriuk", Color.White, Color(0xFF4A2412))
  else -> null
}

private fun homeOrdersLabel(id: Int): String? = when (id) {
  1 -> "1.2rb+ pesanan dibuat"
  2 -> "890+ pesanan dibuat"
  5 -> "2.1rb+ terjual"
  else -> null
}

@Composable
private fun OpenHoursPill(jamBuka: String) {
  val jam = Regex("\\d{1,2}[.:]\\d{2}\\s*[-–]\\s*\\d{1,2}[.:]\\d{2}")
    .find(jamBuka)?.value?.replace(" ", "")?.replace('-', '–') ?: ""
  Row(
    Modifier
      .clip(RoundedCornerShape(50))
      .background(Color.White)
      .border(1.dp, ArsyCardBorder, RoundedCornerShape(50))
      .padding(horizontal = 10.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(Modifier.size(6.dp).clip(CircleShape).background(ArsySuccess))
    Spacer(Modifier.width(6.dp))
    Text(
      if (jam.isBlank()) "Buka Setiap Hari" else "Buka ($jam)",
      fontSize = 10.5.sp,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onBackground,
      maxLines = 1
    )
  }
}

// ============================ MENU BROWSE ============================

@Composable
fun MenuBrowseScreen(vm: MenuViewModel, onOpenDetail: (MenuItemEntity) -> Unit) {
  val items by vm.filteredMenuItems.collectAsStateWithLifecycle()
  val query by vm.searchQuery.collectAsStateWithLifecycle()
  val selected by vm.selectedCategory.collectAsStateWithLifecycle()
  val cats = listOf("Semua", "Mie Ayam", "Topping", "Minuman", "Best Seller")

  Column(Modifier.fillMaxSize()) {
    Box(Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(14.dp)).background(Color.White)) {
      Row(Modifier.padding(horizontal = 14.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Search, null, tint = ArsyTextMuted, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        androidx.compose.foundation.text.BasicTextField(
          value = query,
          onValueChange = { vm.setSearchQuery(it) },
          singleLine = true,
          textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground),
          modifier = Modifier.weight(1f),
          decorationBox = { inner ->
            Box { if (query.isBlank()) Text("Cari mie ayam kesukaanmu...", fontSize = 14.sp, color = ArsyTextMuted); inner() }
          }
        )
        if (query.isNotBlank()) Icon(Icons.Filled.Clear, null, tint = ArsyTextMuted, modifier = Modifier.size(18.dp).clickable { vm.setSearchQuery("") })
      }
    }
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      items(cats) { c -> FilterPill(label = c, selected = selected == c, onClick = { vm.setCategory(c) }) }
    }
    SectionHeader("Menu Paling Dicari", action = "Lihat Semua")
    if (items.isEmpty()) {
      EmptyState(Icons.Filled.Restaurant, "Menu tidak ditemukan", "Coba kata kunci atau kategori lain.")
    } else {
      LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp)) {
        items(items.chunked(2), key = { it.joinToString { i -> i.id.toString() } }) { row ->
          Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 12.dp)) {
            row.forEach { item ->
              MenuGridCard(item = item, modifier = Modifier.weight(1f), onClick = { onOpenDetail(item) }, onAdd = { vm.handlePlus(item) })
            }
            if (row.size == 1) Spacer(Modifier.weight(1f))
          }
        }
      }
    }
  }
}

@Composable
private fun MenuGridCard(item: MenuItemEntity, modifier: Modifier = Modifier, onClick: () -> Unit, onAdd: () -> Unit) {
  WhiteCard(modifier = modifier, contentPadding = PaddingValues(10.dp), onClick = onClick) {
    Box {
      MenuPhoto(item, Modifier.fillMaxWidth().height(110.dp))
      if (item.isBestSeller) Box(Modifier.padding(8.dp)) { TagChip("Terlaris", ArsyDanger, Color.White) }
    }
    Spacer(Modifier.height(8.dp))
    Text(item.nama, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.height(38.dp))
    Text(item.deskripsi.ifBlank { "Spesial warung Arsy" }, fontSize = 11.5.sp, color = ArsyTextMuted, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.height(32.dp))
    Spacer(Modifier.height(6.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(rupiah(item.harga), fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp, modifier = Modifier.weight(1f))
      Box(Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary).clickable(onClick = onAdd), contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.Add, null, tint = Color.White, modifier = Modifier.size(18.dp))
      }
    }
  }
}

// ============================ DETAIL ============================

@Composable
fun DetailScreen(vm: MenuViewModel, item: MenuItemEntity, onBack: () -> Unit, onOpenCart: () -> Unit) {
  val context = LocalContext.current
  val allItems by vm.filteredMenuItems.collectAsStateWithLifecycle()
  val cartItems by vm.cartItems.collectAsStateWithLifecycle()

  val toppings = remember(allItems) { allItems.filter { it.kategori.equals("Topping", true) && it.id != item.id }.take(5) }
  var selectedToppings by remember(item.id) { mutableStateOf(setOf<Int>()) }
  var notes by remember(item.id) { mutableStateOf("") }

  val baseQty = cartItems.filter { it.item.id == item.id }.sumOf { it.quantity }
  val toppingTotal = toppings.filter { it.id in selectedToppings }.sumOf { it.harga }
  val unit = item.harga + toppingTotal
  val grandTotal = unit * baseQty.coerceAtLeast(1)

  Column(Modifier.fillMaxSize()) {
    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
      RoundIconButton(Icons.AutoMirrored.Filled.ArrowBack, onClick = onBack)
      Spacer(Modifier.weight(1f))
      Text("Detail Menu", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
      Spacer(Modifier.weight(1f))
      RoundIconButton(Icons.Filled.Favorite, onClick = {})
    }
    LazyColumn(contentPadding = PaddingValues(bottom = 120.dp)) {
      item {
        Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(20.dp))) {
          MenuPhoto(item, Modifier.fillMaxWidth().height(220.dp), contentScale = androidx.compose.ui.layout.ContentScale.Crop)
          Box(Modifier.align(Alignment.BottomStart).padding(12.dp)) { TagChip("★ 4.9 (1.2rb+)", Color.White, MaterialTheme.colorScheme.primary) }
        }
      }
      item {
        Column(Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.Top) {
            Text(item.nama, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(10.dp))
            Text(rupiah(item.harga), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
          }
          Spacer(Modifier.height(8.dp))
          Text(item.deskripsi.ifBlank { "Racikan bumbu autentik khas Arsy dengan topping melimpah dan kuah kaldu hangat terpisah." }, fontSize = 13.5.sp, color = ArsyTextMuted)
          Spacer(Modifier.height(12.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoChip(Icons.Filled.Home, "~12 Menit")
            InfoChip(Icons.Filled.Restaurant, "Porsi Reguler")
          }
        }
      }
      if (toppings.isNotEmpty()) {
        item { SectionHeader("Topping Tambahan", subtitle = "Bisa pilih lebih dari satu", action = "Opsional") }
        items(toppings) { t ->
          val on = t.id in selectedToppings
          CheckRow(
            title = t.nama,
            subtitle = t.deskripsi.ifBlank { "Tambahan spesial" },
            price = "+" + rupiah(t.harga),
            checked = on,
            onToggle = { selectedToppings = if (on) selectedToppings - t.id else selectedToppings + t.id },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
          )
        }
      }
      item {
        WhiteCard(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Edit, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Catatan Khusus untuk Koki", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
          }
          Spacer(Modifier.height(10.dp))
          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            placeholder = { Text("Misal: kuah dipisah, sambal jangan terlalu banyak...", fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = ArsyPeach),
            maxLines = 3
          )
        }
      }
    }
  }

  // Bottom add-to-cart bar
  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
    WhiteCard(contentPadding = PaddingValues(14.dp), modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        QtyStepper(
          qty = baseQty.coerceAtLeast(0),
          onInc = { vm.handlePlus(item) },
          onDec = { vm.handleMinus(item) }
        )
        Spacer(Modifier.width(12.dp))
        PrimaryButton(
          text = "Keranjang  ${rupiah(grandTotal)}",
          onClick = {
            // add selected toppings to cart too
            toppings.filter { it.id in selectedToppings }.forEach { vm.handlePlus(it) }
            vm.setCustomerNotes(notes)
            if (baseQty == 0) vm.handlePlus(item)
            onOpenCart()
          },
          icon = Icons.Filled.ShoppingBag,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
  Row(Modifier.clip(RoundedCornerShape(10.dp)).background(ArsyPeach).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
    Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
    Spacer(Modifier.width(6.dp))
    Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
  }
}

// ============================ CART / CHECKOUT ============================

@Composable
fun CartScreen(vm: MenuViewModel, onBack: () -> Unit, onOrderPlaced: (OrderRecord) -> Unit) {
  val context = LocalContext.current
  val cartItems by vm.cartItems.collectAsStateWithLifecycle()
  val total by vm.totalCartPrice.collectAsStateWithLifecycle()
  val shopConfig by vm.shopConfig.collectAsStateWithLifecycle()
  val name by vm.customerName.collectAsStateWithLifecycle()
  val notes by vm.customerNotes.collectAsStateWithLifecycle()

  var mode by remember { mutableStateOf("Dine In") }
  var method by remember { mutableStateOf("COD / Bayar Tunai saat Diantar") }
  val modes = listOf("Dine In" to Icons.Filled.Restaurant, "Takeaway" to Icons.Filled.Outbox, "Delivery" to Icons.Filled.DeliveryDining)
  val methods = listOf(
    "COD / Bayar Tunai saat Diantar (di Rumah)" to "Siapkan uang pas saat pesanan tiba di alamat Anda.",
    "Transfer / QRIS Bank (Tanpa Kode Unik)" to "Kirim bukti transfer asli bank/e-wallet. Warung memverifikasi langsung.",
    "Bayar di Kasir Warung (Makan di Tempat)" to "Pembayaran tunai atau QRIS kasir langsung saat makanan siap."
  )
  val fee = if (mode == "Delivery") 2000L else 0L
  val grand = total + fee

  if (cartItems.isEmpty()) {
    EmptyState(Icons.Filled.ShoppingBag, "Keranjang masih kosong", "Yuk pilih menu favoritmu dulu.")
    return
  }

  LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 140.dp)) {
    item {
      Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        RoundIconButton(Icons.AutoMirrored.Filled.ArrowBack, onClick = onBack)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
          Text("Keranjang Pemesanan", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
          Text("${cartItems.size} Menu Dipilih", fontSize = 12.sp, color = ArsyTextMuted)
        }
        Text("Kosongkan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ArsyDanger, modifier = Modifier.clickable { vm.clearCart() })
      }
    }
    item {
      Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        modes.forEach { (label, icon) ->
          val on = mode == label
          Box(
            Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(if (on) MaterialTheme.colorScheme.primary else Color.White)
              .border2(if (on) MaterialTheme.colorScheme.primary else ArsyPeach)
              .clickable { mode = label }.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(icon, null, tint = if (on) Color.White else ArsyTextMuted, modifier = Modifier.size(16.dp))
              Spacer(Modifier.width(6.dp))
              Text(label, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = if (on) Color.White else MaterialTheme.colorScheme.onBackground)
            }
          }
        }
      }
    }
    item {
      WhiteCard(modifier = Modifier.padding(16.dp)) {
        Text("Informasi Pemesan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
          value = name,
          onValueChange = { vm.setCustomerName(it) },
          placeholder = { Text("Nama Lengkap", fontSize = 13.sp) },
          leadingIcon = { Icon(Icons.Filled.Person, null, tint = ArsyTextMuted) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = ArsyPeach)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
          value = notes,
          onValueChange = { vm.setCustomerNotes(it) },
          placeholder = { Text("Catatan (opsional)", fontSize = 13.sp) },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = ArsyPeach)
        )
      }
    }
    item { SectionHeader("Daftar Menu", action = "${cartItems.size} Macam") }
    items(cartItems, key = { it.key }) { ci ->
      WhiteCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), contentPadding = PaddingValues(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          MenuPhoto(ci.item, Modifier.size(56.dp))
          Spacer(Modifier.width(10.dp))
          Column(Modifier.weight(1f)) {
            Text(ci.item.nama, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onBackground)
            if (ci.variant.isNotBlank()) Text(ci.variant, fontSize = 11.5.sp, color = ArsyTextMuted)
            Text(rupiah(ci.subtotal), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
          }
          QtyStepper(qty = ci.quantity, onInc = { vm.updateCartItemQuantity(ci, ci.quantity + 1) }, onDec = { vm.updateCartItemQuantity(ci, ci.quantity - 1) })
        }
      }
    }
    item {
      WhiteCard(modifier = Modifier.padding(16.dp)) {
        Text("Metode Pembayaran", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(10.dp))
        methods.forEachIndexed { idx, (label, desc) ->
          val on = method == label
          Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(if (on) ArsyPelseSafe() else Color.White)
              .padding(12.dp).clickable { method = label },
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(Modifier.size(20.dp).clip(CircleShape).background(if (on) MaterialTheme.colorScheme.primary else Color.White).border2(if (on) MaterialTheme.colorScheme.primary else ArsyTextMuted), contentAlignment = Alignment.Center) {
              if (on) Box(Modifier.size(10.dp).clip(CircleShape).background(Color.White))
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
              Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
              Text(desc, fontSize = 11.5.sp, color = ArsyTextMuted)
            }
          }
        }
      }
    }
    item {
      WhiteCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        SummaryLine("Subtotal Menu", rupiah(total))
        SummaryLine("Biaya Kemasan & Layanan", rupiah(fee))
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("Total Pembayaran", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
          Text(rupiah(grand), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
        }
      }
    }
  }

  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
    WhiteCard(contentPadding = PaddingValues(12.dp), modifier = Modifier.padding(12.dp)) {
      PrimaryButton(
        text = "Kirim Pesanan via WhatsApp",
        icon = Icons.Filled.Storefront,
        containerColor = WhatsAppGreen,
        modifier = Modifier.fillMaxWidth(),
        onClick = {
          val code = "#ARSY-" + (1000..9999).random()
          val sb = StringBuilder()
          sb.append("Halo ${shopConfig.namaWarung}, saya ${name.ifBlank { "Pelanggan" }} mau pesan:\n")
          cartItems.forEach { sb.append("• ${it.quantity}x ${it.item.nama}${if (it.variant.isNotBlank()) " (${it.variant})" else ""} = ${rupiah(it.subtotal)}\n") }
          sb.append("Layanan: $mode\n")
          sb.append("Metode Pembayaran: $method\n")
          if (notes.isNotBlank()) sb.append("Catatan: $notes\n")
          sb.append("Total: ${rupiah(grand)}. Mohon diproses ya!")
          openWhatsApp(context, shopConfig.nomorWa, sb.toString())
          onOrderPlaced(
            OrderRecord(
              code = code,
              timeLabel = "Baru saja",
              mode = mode,
              customer = name.ifBlank { "Pelanggan" },
              method = method,
              lines = cartItems.map { OrderLine(it.item.nama, it.quantity, it.variant, it.subtotal) },
              total = grand,
              status = "Baru Masuk"
            )
          )
          vm.clearCart()
          onBack()
        }
      )
    }
  }
}

private fun ArsyPelseSafe(): Color = ArsyGoldSoft.copy(alpha = 0.4f)

private fun Modifier.border2(color: Color): Modifier = this.then(
  Modifier.background(color.copy(alpha = 0f))
)

@Composable
private fun SummaryLine(label: String, value: String) {
  Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
    Text(label, fontSize = 13.sp, color = ArsyTextMuted, modifier = Modifier.weight(1f))
    Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
  }
}

// ============================ ORDERS ============================

@Composable
fun OrdersScreen(orders: List<OrderRecord>, onBackToMenu: () -> Unit) {
  var tab by remember { mutableStateOf(0) }
  val active = orders.filter { it.status != "Selesai" }
  val done = orders.filter { it.status == "Selesai" }
  val list = if (tab == 0) active else done

  LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 96.dp)) {
    item {
      Column(Modifier.padding(16.dp)) {
        Text("Pesanan Saya", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        Text("Pantau hidangan favoritmu yang sedang disiapkan", fontSize = 12.sp, color = ArsyTextMuted)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          FilterPill("Pesanan Aktif", tab == 0, { tab = 0 }, count = active.size)
          FilterPill("Riwayat Selesai", tab == 1, { tab = 1 })
        }
      }
    }
    if (list.isEmpty()) {
      item { EmptyState(Icons.Filled.Outbox, "Belum ada pesanan", "Pesananmu akan muncul di sini setelah kamu checkout.") }
      item { PrimaryButton(text = "Pesan Sekarang", onClick = onBackToMenu, modifier = Modifier.padding(horizontal = 16.dp)) }
    } else {
      items(list) { o -> OrderCard(o) }
    }
  }
}

@Composable
private fun OrderCard(o: OrderRecord) {
  WhiteCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(o.code, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
      TagChip(o.status, ArsyGoldSoft, Color(0xFF7A5A00))
    }
    Text("${o.mode} • ${o.timeLabel}", fontSize = 12.sp, color = ArsyTextMuted)
    Spacer(Modifier.height(10.dp))
    StatusTimeline(o.status)
    Spacer(Modifier.height(10.dp))
    o.lines.forEach { l ->
      Row(Modifier.padding(vertical = 3.dp)) {
        Text("${l.qty}x ${l.name}${if (l.variant.isNotBlank()) " (${l.variant})" else ""}", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
        Text(rupiah(l.price), fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
      }
    }
    Spacer(Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text("Total", fontSize = 12.sp, color = ArsyTextMuted, modifier = Modifier.weight(1f))
      Text(rupiah(o.total), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
    }
  }
}

@Composable
private fun StatusTimeline(status: String) {
  val steps = listOf("Diterima", "Dimasak", "Siap Saji", "Selesai")
  val idx = steps.indexOf(status).coerceAtLeast(0)
  Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
    steps.forEachIndexed { i, s ->
      val done = i <= idx
      Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
        Box(Modifier.size(26.dp).clip(CircleShape).background(if (done) MaterialTheme.colorScheme.primary else ArsyPeach), contentAlignment = Alignment.Center) {
          Icon(if (done) Icons.Filled.CheckCircle else Icons.Filled.Close, null, tint = if (done) Color.White else ArsyTextMuted, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(s, fontSize = 10.sp, color = if (done) MaterialTheme.colorScheme.onBackground else ArsyTextMuted, maxLines = 1)
      }
    }
  }
}

// ============================ PROFILE ============================

@Composable
fun ProfileScreen(
  vm: MenuViewModel,
  orders: List<OrderRecord>,
  onOpenAdminLogin: () -> Unit,
  onGoMenu: () -> Unit,
  onOpenOrders: () -> Unit
) {
  val context = LocalContext.current
  val shopConfig by vm.shopConfig.collectAsStateWithLifecycle()
  var notif by remember { mutableStateOf(true) }
  val points = orders.sumOf { it.total.toInt() } / 1000 * 10
  val level = (points / 150) + 1
  val prefs = remember { context.getSharedPreferences("profil_pelanggan", Context.MODE_PRIVATE) }
  var namaPelanggan by remember { mutableStateOf(prefs.getString("nama", null) ?: "Pelanggan Arsy") }
  var telepon by remember { mutableStateOf(prefs.getString("telepon", "") ?: "") }
  var showEditProfil by remember { mutableStateOf(false) }
  var showKeluar by remember { mutableStateOf(false) }

  LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 96.dp)) {
    item {
      Column(Modifier.padding(16.dp)) {
        GoldChip("AKUN PELANGGAN")
        Text("Profil Saya", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        Text("Kelola akun dan nikmati promo langganan mie ayam favoritmu.", fontSize = 12.sp, color = ArsyTextMuted)
      }
    }
    item {
      WhiteCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(Modifier.size(52.dp).clip(CircleShape).background(ArsyPeach), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
          }
          Spacer(Modifier.width(12.dp))
          Column(Modifier.weight(1f)) {
            Text(namaPelanggan, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(telepon.ifBlank { shopConfig.nomorWa }, fontSize = 12.sp, color = ArsyTextMuted)
              Spacer(Modifier.width(6.dp))
              Icon(Icons.Filled.CheckCircle, null, tint = ArsySuccess, modifier = Modifier.size(14.dp))
            }
          }
        }
        Spacer(Modifier.height(12.dp))
        PrimaryButton(text = "Ubah Informasi Akun", onClick = { showEditProfil = true }, containerColor = ArsyPeach, contentColor = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth())
      }
    }
    item {
      Box(
        Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(20.dp))
          .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF7A3E1E), Color(0xFFB4530A)))).padding(18.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Member Setia Arsy", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            GoldChip("LEVEL $level")
          }
          Spacer(Modifier.height(8.dp))
          Text("$points", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
          Text("Arsy Poin", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
          Spacer(Modifier.height(10.dp))
          Text("Kumpulkan 150 poin lagi untuk dapat 1 Porsi Mie Ayam Spesial Gratis!", fontSize = 12.sp, color = ArsyGoldSoft)
        }
      }
    }
    item { SectionHeader("Aktivitas & Bantuan") }
    item {
      WhiteCard(modifier = Modifier.padding(horizontal = 16.dp), contentPadding = PaddingValues(vertical = 4.dp)) {
        ProfileRow(Icons.Filled.Favorite, "Menu Favorit Saya", "Mie Ayam Spesial Komplit", null, onClick = onGoMenu)
        ProfileRow(Icons.Filled.Outbox, "Riwayat Pesanan", "${orders.size} pesanan sebelumnya", null, onClick = onOpenOrders)
        ProfileRow(Icons.Filled.Storefront, "Layanan Pelanggan WhatsApp", "Respons cepat & bantuan online", null) {
          openWhatsApp(context, shopConfig.nomorWa, "Halo ${shopConfig.namaWarung}, saya butuh bantuan.")
        }
        ProfileRow(Icons.Filled.Lock, "Masuk Mode Pengelola", "Kelola menu, stok & laporan", null, onClick = onOpenAdminLogin)
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Filled.Notifications, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
          Spacer(Modifier.width(12.dp))
          Column(Modifier.weight(1f)) {
            Text("Notifikasi Promo & Pesanan", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Text("Kabar diskon dan update kurir", fontSize = 11.5.sp, color = ArsyTextMuted)
          }
          Switch(checked = notif, onCheckedChange = { notif = it }, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
        }
      }
    }
    item {
      Box(Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(14.dp)).background(Color.White).clickable { showKeluar = true }.padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
        Text("Keluar dari Akun", color = ArsyDanger, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
    }
    item {
      Column(Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Filled.CheckCircle, null, tint = ArsySuccess, modifier = Modifier.size(14.dp))
          Spacer(Modifier.width(4.dp))
          Text("100% Halal, Higienis & Dibuat Segar Setiap Hari", fontSize = 11.5.sp, color = ArsyTextMuted)
        }
        Text("Mie Ayam Arsy Mobile", fontSize = 11.sp, color = ArsyTextMuted)
      }
    }
  }

  if (showEditProfil) {
    EditProfilDialog(
      initialNama = namaPelanggan,
      initialTelepon = telepon.ifBlank { shopConfig.nomorWa },
      onDismiss = { showEditProfil = false },
      onSave = { nama, telp ->
        prefs.edit().putString("nama", nama).putString("telepon", telp).apply()
        namaPelanggan = nama.ifBlank { "Pelanggan Arsy" }
        telepon = telp
        showEditProfil = false
        Toast.makeText(context, "Informasi akun disimpan", Toast.LENGTH_SHORT).show()
      }
    )
  }
  if (showKeluar) {
    AlertDialog(
      onDismissRequest = { showKeluar = false },
      title = { Text("Keluar dari Akun?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
      text = { Text("Informasi akun di perangkat ini akan dihapus dan profil kembali ke pelanggan umum.") },
      confirmButton = {
        TextButton(onClick = {
          prefs.edit().clear().apply()
          namaPelanggan = "Pelanggan Arsy"
          telepon = ""
          showKeluar = false
          Toast.makeText(context, "Anda telah keluar dari akun", Toast.LENGTH_SHORT).show()
        }) { Text("Keluar", color = ArsyDanger) }
      },
      dismissButton = { TextButton(onClick = { showKeluar = false }) { Text("Batal") } }
    )
  }
}

@Composable
private fun EditProfilDialog(
  initialNama: String,
  initialTelepon: String,
  onDismiss: () -> Unit,
  onSave: (String, String) -> Unit
) {
  var nama by remember { mutableStateOf(initialNama) }
  var telepon by remember { mutableStateOf(initialTelepon) }
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Ubah Informasi Akun", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = nama,
          onValueChange = { nama = it },
          label = { Text("Nama Pelanggan") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = telepon,
          onValueChange = { telepon = it.filter { c -> c.isDigit() || c == '+' } },
          label = { Text("No. HP / WhatsApp") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Text("Disimpan hanya di perangkat ini.", fontSize = 11.sp, color = ArsyTextMuted)
      }
    },
    confirmButton = { TextButton(onClick = { onSave(nama.trim(), telepon.trim()) }) { Text("Simpan") } },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
  )
}

@Composable
private fun ProfileRow(icon: ImageVector, title: String, subtitle: String, trailing: String?, onClick: (() -> Unit)? = null) {
  Row(
    Modifier.fillMaxWidth().clickable(enabled = onClick != null) { onClick?.invoke() }.padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(ArsyPeach), contentAlignment = Alignment.Center) {
      Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
    }
    Spacer(Modifier.width(12.dp))
    Column(Modifier.weight(1f)) {
      Text(title, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
      Text(subtitle, fontSize = 11.5.sp, color = ArsyTextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
    if (trailing != null) Text(trailing, fontSize = 12.sp, color = ArsyTextMuted)
    Icon(Icons.Filled.ArrowForward, null, tint = ArsyTextMuted, modifier = Modifier.size(16.dp))
  }
}
