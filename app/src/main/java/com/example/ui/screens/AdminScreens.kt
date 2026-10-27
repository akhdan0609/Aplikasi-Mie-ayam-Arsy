package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WifiTetheringError
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MenuItemEntity
import com.example.data.model.PromoSpesial
import com.example.ui.design.EmptyState
import com.example.ui.design.FilterPill
import com.example.ui.design.GoldChip
import com.example.ui.design.MenuPhoto
import com.example.ui.design.OrderRecord
import com.example.ui.design.PrimaryButton
import com.example.ui.design.SectionHeader
import com.example.ui.design.StatTile
import com.example.ui.design.TagChip
import com.example.ui.design.WhiteCard
import com.example.ui.design.rupiah
import com.example.ui.theme.ArsyDanger
import com.example.ui.theme.ArsyGold
import com.example.ui.theme.ArsyGoldSoft
import com.example.ui.theme.ArsyPeach
import com.example.ui.theme.ArsySuccess
import com.example.ui.theme.ArsyTextMuted
import com.example.ui.viewmodel.MenuViewModel

// ============================ ADMIN: ORDERS / POS ============================

@Composable
fun AdminOrdersScreen(vm: MenuViewModel, orders: List<OrderRecord>, onAdvance: (String) -> Unit) {
  val shopConfig by vm.shopConfig.collectAsStateWithLifecycle()
  var filter by remember { mutableStateOf("Baru Masuk") }
  val tabs = listOf("Baru Masuk", "Dimasak", "Siap Saji", "Selesai")
  val shown = orders.filter { it.status == filter }

  val needCook = orders.count { it.status == "Baru Masuk" || it.status == "Dimasak" }
  val omset = orders.filter { it.status == "Selesai" }.sumOf { it.total }

  LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 96.dp)) {
    item {
      WhiteCard(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(ArsyPeach), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Storefront, null, tint = MaterialTheme.colorScheme.primary)
          }
          Spacer(Modifier.width(12.dp))
          Column(Modifier.weight(1f)) {
            Text("Warung Siap Jualan", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
            Text("Menerima pesanan Dine-in & COD", fontSize = 12.sp, color = ArsyTextMuted)
          }
          TagChip("Aktif", ArsyGoldSoft, Color(0xFF7A5A00))
        }
      }
    }
    item {
      Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatTile("Hari Ini", "${orders.size}", "Pesanan", modifier = Modifier.weight(1f))
        StatTile("Antrean", "$needCook", "Perlu Masak", accent = ArsyDanger, modifier = Modifier.weight(1f))
        StatTile("Omset", "${omset / 1000}rb", "Rp $omset".let { "Rp ${(omset / 1000)}rb" }, modifier = Modifier.weight(1f))
      }
    }
    item { SectionHeader("Pesanan Langsung", action = "Segarkan", onAction = { vm.refreshMenu() }) }
    item {
      androidx.compose.foundation.lazy.LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(tabs) { t -> FilterPill(label = t, selected = filter == t, onClick = { filter = t }, count = if (t == "Baru Masuk") orders.count { it.status == "Baru Masuk" } else null) }
      }
    }
    if (shown.isEmpty()) {
      item { EmptyState(Icons.Filled.NotificationsActive, "Tidak ada pesanan", "Pesanan baru akan muncul di sini.") }
    } else {
      items(shown) { o -> AdminOrderCard(o, onAdvance = { onAdvance(o.code) }) }
    }
  }
}

@Composable
private fun AdminOrderCard(o: OrderRecord, onAdvance: () -> Unit) {
  WhiteCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      TagChip(o.status.uppercase(), ArsyPeach, MaterialTheme.colorScheme.primary)
      Spacer(Modifier.width(10.dp))
      Text(o.code, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
      Text(o.timeLabel, fontSize = 11.sp, color = ArsyTextMuted)
    }
    Spacer(Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(Icons.Filled.Person, null, tint = ArsyTextMuted, modifier = Modifier.size(15.dp))
      Spacer(Modifier.width(6.dp))
      Text(o.customer, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))
      TagChip(o.mode, ArsyGoldSoft, Color(0xFF7A5A00))
    }
    Spacer(Modifier.height(8.dp))
    o.lines.forEach { l ->
      Row(Modifier.padding(vertical = 2.dp)) {
        Text("${l.qty}x ${l.name}", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(rupiah(l.price), fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
      }
    }
    Spacer(Modifier.height(10.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
      Column(Modifier.weight(1f)) {
        Text("Total", fontSize = 11.sp, color = ArsyTextMuted)
        Text(rupiah(o.total), fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onBackground)
      }
      PrimaryButton(
        text = if (o.status == "Selesai") "Selesai" else "Terima & Proses",
        onClick = onAdvance,
        enabled = o.status != "Selesai",
        modifier = Modifier.width(180.dp)
      )
    }
  }
}

// ============================ ADMIN: MENU & STOCK ============================

@Composable
fun AdminMenuStockScreen(vm: MenuViewModel) {
  val items by vm.filteredMenuItems.collectAsStateWithLifecycle()
  var query by remember { mutableStateOf("") }
  val filtered = items.filter { it.nama.contains(query, true) }
  val active = items.count { it.isAvailable }
  val sold = items.count { !it.isAvailable }

  LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 96.dp)) {
    item {
      Box(
        Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(20.dp))
          .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF7A3E1E), Color(0xFFB4530A)))).padding(18.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Column(Modifier.weight(1f)) {
            Text("KETERSEDIAAN MENU", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
            Text("$active Menu Aktif", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("$sold Menu Habis (Sold Out)", fontSize = 12.sp, color = ArsyGoldSoft)
          }
          Box(Modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.15f)).clickable { vm.openAddMenuDialog() }.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Filled.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
              Spacer(Modifier.width(4.dp))
              Text("Tambah Menu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }
    }
    item {
      Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(14.dp)).background(Color.White)) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Filled.Search, null, tint = ArsyTextMuted, modifier = Modifier.size(18.dp))
          Spacer(Modifier.width(8.dp))
          androidx.compose.foundation.text.BasicTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.weight(1f),
            decorationBox = { inner -> Box { if (query.isBlank()) Text("Cari menu untuk ubah stok cepat...", fontSize = 13.sp, color = ArsyTextMuted); inner() } }
          )
        }
      }
    }
    items(filtered, key = { "adm_" + it.id }) { item -> AdminStockRow(item = item, vm = vm) }
  }
}

@Composable
private fun AdminStockRow(item: MenuItemEntity, vm: MenuViewModel) {
  WhiteCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), contentPadding = PaddingValues(12.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      MenuPhoto(item, Modifier.size(60.dp))
      Spacer(Modifier.width(12.dp))
      Column(Modifier.weight(1f)) {
        Text(item.nama, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onBackground)
        Text(rupiah(item.harga), fontWeight = FontWeight.Bold, color = if (item.isAvailable) MaterialTheme.colorScheme.primary else ArsyTextMuted, fontSize = 13.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(Modifier.size(8.dp).clip(CircleShape).background(if (item.isAvailable) ArsySuccess else ArsyDanger))
          Spacer(Modifier.width(6.dp))
          Text(if (item.isAvailable) "Tersedia (Stok Aman)" else "HABIS • Ditutup dari Menu", fontSize = 11.sp, color = ArsyTextMuted)
        }
      }
      Column(horizontalAlignment = Alignment.End) {
        Switch(
          checked = item.isAvailable,
          onCheckedChange = { vm.toggleItemStatus(item) },
          colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
        )
        Icon(Icons.Filled.Edit, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp).clickable { vm.openEditMenuDialog(item) })
      }
    }
  }
}

// ============================ ADMIN: PROMO SPESIAL ============================

@Composable
fun AdminPromoScreen(vm: MenuViewModel) {
  val promos by vm.promos.collectAsStateWithLifecycle()
  val menuItems by vm.allMenuItems.collectAsStateWithLifecycle()
  var promoToDelete by remember { mutableStateOf<PromoSpesial?>(null) }

  LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 96.dp)) {
    item {
      Box(
        Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(20.dp))
          .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF7A3E1E), Color(0xFFB4530A)))).padding(18.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Column(Modifier.weight(1f)) {
            Text("PROMO SPESIAL BERANDA", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
            Text("${promos.count { it.aktif }} Promo Aktif", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Kartu bisa digeser pelanggan di Beranda", fontSize = 12.sp, color = ArsyGoldSoft)
          }
          Box(Modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.15f)).clickable { vm.openAddPromoDialog() }.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Filled.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
              Spacer(Modifier.width(4.dp))
              Text("Tambah", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }
    }
    if (promos.isEmpty()) {
      item { EmptyState(Icons.Filled.LocalOffer, "Belum ada promo", "Tambahkan promo agar Beranda tampil menarik.") }
    }
    items(promos, key = { "promo_" + it.id }) { promo ->
      AdminPromoRow(
        promo = promo,
        menuItems = menuItems,
        onEdit = { vm.openEditPromoDialog(promo) },
        onDelete = { promoToDelete = promo },
        onToggleActive = { vm.savePromo(promo.id, promo.judul, promo.deskripsi, promo.hargaPromo, promo.hargaNormal, promo.menuId, !promo.aktif) }
      )
    }
  }

  if (promoToDelete != null) {
    AlertDialog(
      onDismissRequest = { promoToDelete = null },
      title = { Text("Hapus Promo") },
      text = { Text("Hapus promo '${promoToDelete?.judul}' dari Beranda?") },
      confirmButton = {
        Button(
          onClick = { promoToDelete?.let { vm.deletePromo(it) }; promoToDelete = null },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) { Text("Hapus") }
      },
      dismissButton = { TextButton(onClick = { promoToDelete = null }) { Text("Batal") } }
    )
  }
}

@Composable
private fun AdminPromoRow(
  promo: PromoSpesial,
  menuItems: List<MenuItemEntity>,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onToggleActive: () -> Unit
) {
  val linked = menuItems.firstOrNull { it.id == promo.menuId }
  WhiteCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), contentPadding = PaddingValues(12.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (linked != null) {
        MenuPhoto(linked, Modifier.size(60.dp))
      } else {
        Box(Modifier.size(60.dp).clip(RoundedCornerShape(14.dp)).background(ArsyPeach), contentAlignment = Alignment.Center) {
          Icon(Icons.Filled.LocalOffer, null, tint = MaterialTheme.colorScheme.primary)
        }
      }
      Spacer(Modifier.width(12.dp))
      Column(Modifier.weight(1f)) {
        Text(promo.judul, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onBackground)
        Text(if (linked != null) linked.nama else "Menu utama belum dipilih", fontSize = 11.5.sp, color = ArsyTextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(rupiah(promo.hargaPromo), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
          Spacer(Modifier.width(6.dp))
          Text(rupiah(promo.hargaNormal), fontSize = 11.sp, color = ArsyTextMuted, textDecoration = TextDecoration.LineThrough)
        }
      }
      Column(horizontalAlignment = Alignment.End) {
        Switch(checked = promo.aktif, onCheckedChange = { onToggleActive() }, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
        Row {
          Icon(Icons.Filled.Edit, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp).clickable(onClick = onEdit))
          Spacer(Modifier.width(10.dp))
          Icon(Icons.Filled.Delete, null, tint = ArsyDanger, modifier = Modifier.size(20.dp).clickable(onClick = onDelete))
        }
      }
    }
  }
}

// ============================ ADMIN: REPORTS ============================

@Composable
fun AdminReportsScreen(vm: MenuViewModel, orders: List<OrderRecord>) {
  val done = orders.filter { it.status == "Selesai" }
  val omset = done.sumOf { it.total }
  val trx = done.size
  val itemsSold = done.sumOf { o -> o.lines.sumOf { it.qty } }

  val byName = HashMap<String, Pair<Int, Long>>()
  done.forEach { o -> o.lines.forEach { l -> val p = byName[l.name] ?: (0 to 0L); byName[l.name] = (p.first + l.qty) to (p.second + l.price) } }
  val top = byName.entries.sortedByDescending { it.value.second }.take(5)

  LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 96.dp)) {
    item {
      Box(
        Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(20.dp))
          .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF7A3E1E), Color(0xFFB4530A)))).padding(18.dp)
      ) {
        Text("TOTAL OMSET BERSIH", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
        Text(rupiah(omset), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          MiniStat("Terjual", "$itemsSold Porsi")
          MiniStat("Transaksi", "$trx Struk")
          MiniStat("Rata-rata", if (trx > 0) rupiah(omset / trx) else "Rp 0")
        }
      }
    }
    item { SectionHeader("Menu Terlaris", action = "Top 5") }
    if (top.isEmpty()) {
      item { EmptyState(Icons.Filled.WifiTetheringError, "Belum ada data", "Laporan terisi otomatis setelah ada pesanan selesai.") }
    } else {
      items(top) { (name, v) ->
        WhiteCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp), contentPadding = PaddingValues(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(28.dp).clip(CircleShape).background(ArsyPeach), contentAlignment = Alignment.Center) {
              Text((top.indexOfFirst { it.key == name } + 1).toString(), fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
              Text(name, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = MaterialTheme.colorScheme.onBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
              Text("${v.first} porsi terjual", fontSize = 11.5.sp, color = ArsyTextMuted)
            }
            Text(rupiah(v.second), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
          }
        }
      }
    }
    item {
      WhiteCard(modifier = Modifier.padding(16.dp)) {
        Text("Arus Kas Pembayaran", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(8.dp))
        Text("Cash flow mengikuti metode pembayaran setiap transaksi yang diselesaikan.", fontSize = 12.sp, color = ArsyTextMuted)
      }
    }
  }
}

@Composable
private fun MiniStat(label: String, value: String) {
  Column(Modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.12f)).padding(horizontal = 12.dp, vertical = 10.dp)) {
    Text(label, fontSize = 10.5.sp, color = Color.White.copy(alpha = 0.8f))
    Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
  }
}

// ============================ ADMIN: SETTINGS ============================

@Composable
fun AdminSettingsScreen(vm: MenuViewModel, onExitToCustomer: () -> Unit) {
  val shopConfig by vm.shopConfig.collectAsStateWithLifecycle()
  var nama by remember(shopConfig.namaWarung) { mutableStateOf(shopConfig.namaWarung) }
  var wa by remember(shopConfig.nomorWa) { mutableStateOf(shopConfig.nomorWa) }
  var alamat by remember(shopConfig.alamat) { mutableStateOf(shopConfig.alamat) }
  var jam by remember(shopConfig.jamBuka) { mutableStateOf(shopConfig.jamBuka) }
  var bank by remember(shopConfig.infoBank) { mutableStateOf(shopConfig.infoBank) }
  var notif by remember { mutableStateOf(true) }
  var autoOpen by remember { mutableStateOf(true) }

  LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 96.dp)) {
    item { SectionHeader("Pengaturan Warung", subtitle = "Atur jadwal, kontak & metode bayar") }
    item {
      WhiteCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Field("Nama Warung", nama) { nama = it }
        Spacer(Modifier.height(8.dp))
        Field("Nomor WhatsApp", wa) { wa = it }
        Spacer(Modifier.height(8.dp))
        Field("Alamat", alamat) { alamat = it }
        Spacer(Modifier.height(8.dp))
        Field("Jam Buka", jam) { jam = it }
        Spacer(Modifier.height(8.dp))
        Field("Info Bank / QRIS", bank) { bank = it }
        Spacer(Modifier.height(12.dp))
        PrimaryButton("Simpan Pengaturan", onClick = { vm.saveShopConfig(nama, wa, bank, alamat, jam) }, icon = Icons.Filled.CheckCircle, modifier = Modifier.fillMaxWidth())
      }
    }
    item {
      WhiteCard(modifier = Modifier.padding(16.dp)) {
        ToggleRow(Icons.Filled.NotificationsActive, "Nada Notifikasi Pesanan", "Bunyi saat pesanan baru masuk", notif) { notif = it }
        ToggleRow(Icons.Filled.Storefront, "Jadwal Otomatis", "Buka/tutup mandiri sesuai jam", autoOpen) { autoOpen = it }
      }
    }
    item {
      WhiteCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Keamanan & Peran Akses", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(6.dp))
        Text("Ubah PIN masuk mode admin dan kelola backup data warung.", fontSize = 12.sp, color = ArsyTextMuted)
      }
    }
    item {
      Box(Modifier.fillMaxWidth().padding(16.dp)) {
        PrimaryButton("Lihat Tampilan Pembeli (Katalog)", onClick = onExitToCustomer, icon = Icons.Filled.Visibility, containerColor = ArsyGold, contentColor = Color(0xFF4A2412), modifier = Modifier.fillMaxWidth())
      }
    }
    item {
      Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(14.dp)).background(Color.White).clickable { vm.exitAdminMode() }.padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Filled.Delete, null, tint = ArsyDanger, modifier = Modifier.size(16.dp))
          Spacer(Modifier.width(6.dp))
          Text("Keluar dari Akun Admin", color = ArsyDanger, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }
    }
    item {
      Column(Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Mie Ayam Arsy POS System", fontSize = 11.5.sp, color = ArsyTextMuted)
      }
    }
  }
}

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit) {
  Column {
    Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
    Spacer(Modifier.height(4.dp))
    OutlinedTextField(
      value = value,
      onValueChange = onChange,
      modifier = Modifier.fillMaxWidth(),
      singleLine = label != "Info Bank / QRIS",
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = ArsyPeach)
    )
  }
}

@Composable
private fun ToggleRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
  Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
    Box(Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(ArsyPeach), contentAlignment = Alignment.Center) {
      Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
    }
    Spacer(Modifier.width(12.dp))
    Column(Modifier.weight(1f)) {
      Text(title, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
      Text(subtitle, fontSize = 11.5.sp, color = ArsyTextMuted)
    }
    Switch(checked = checked, onCheckedChange = onChange, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
  }
}
