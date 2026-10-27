package com.example.ui.design

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.MenuItemEntity
import com.example.ui.components.decodeMenuImage
import com.example.ui.theme.ArsyCardBorder
import com.example.ui.theme.ArsyCream
import com.example.ui.theme.ArsyGold
import com.example.ui.theme.ArsyGoldSoft
import com.example.ui.theme.ArsyPeach
import com.example.ui.theme.ArsyTextMuted
import java.text.NumberFormat
import java.util.Locale

// ============================ Navigation model ============================

enum class CustomerTab(val label: String, val icon: ImageVector) {
  Home("Beranda", Icons.Filled.Home),
  Menu("Menu", Icons.Filled.Restaurant),
  Orders("Pesanan", Icons.Filled.Assignment),
  Profile("Profil", Icons.Filled.Person)
}

enum class AdminTab(val label: String, val icon: ImageVector) {
  Orders("Pesanan", Icons.Filled.Assignment),
  MenuStock("Menu & Stok", Icons.Filled.Restaurant),
  Promo("Promo", Icons.Filled.LocalOffer),
  Reports("Laporan", Icons.Filled.BarChart),
  Settings("Pengaturan", Icons.Filled.Settings)
}

// ============================ Order domain model ============================

data class OrderLine(val name: String, val qty: Int, val variant: String, val price: Long)

data class OrderRecord(
  val code: String,
  val timeLabel: String,
  val mode: String, // "Dine In" / "Takeaway" / "Delivery"
  val customer: String,
  val method: String,
  val lines: List<OrderLine>,
  val total: Long,
  val status: String, // "Diterima" / "Dimasak" / "Siap Saji" / "Selesai"
  val isAdminView: Boolean = false
)

fun rupiah(amount: Long): String {
  val f = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
  f.maximumFractionDigits = 0
  return f.format(amount).replace("Rp", "Rp ")
}

// ============================ Reusable UI atoms ============================

@Composable
fun ArsyScreen(modifier: Modifier = Modifier, content: @Composable (PaddingValues) -> Unit) {
  Box(modifier = modifier.fillMaxSize().background(ArsyCream)) { content(PaddingValues(0.dp)) }
}

@Composable
fun WhiteCard(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(16.dp),
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  Surface(
    modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    shape = RoundedCornerShape(18.dp),
    color = Color.White,
    border = BorderStroke(1.dp, ArsyCardBorder),
    tonalElevation = 0.dp
  ) {
    Column(modifier = Modifier.padding(contentPadding)) { content() }
  }
}

@Composable
fun SectionHeader(
  title: String,
  subtitle: String? = null,
  action: String? = null,
  onAction: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        title,
        fontSize = 17.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onBackground
      )
      if (subtitle != null) {
        Text(subtitle, fontSize = 12.sp, color = ArsyTextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
      }
    }
    if (action != null) {
      Text(
        action,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clickable(enabled = onAction != null) { onAction?.invoke() }
      )
    }
  }
}

@Composable
fun MenuPhoto(
  item: MenuItemEntity,
  modifier: Modifier = Modifier,
  shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(14.dp),
  contentScale: ContentScale = ContentScale.Crop
) {
  val decoded = androidx.compose.runtime.remember(item.imageData) { decodeMenuImage(item.imageData) }
  val context = androidx.compose.ui.platform.LocalContext.current
  val resPhoto = androidx.compose.runtime.remember(item.imageData) {
    if (item.imageData.startsWith("res:")) {
      val id = context.resources.getIdentifier(item.imageData.removePrefix("res:"), "drawable", context.packageName)
      if (id != 0) id else null
    } else null
  }
  Box(modifier = modifier.clip(shape).background(ArsyPeach), contentAlignment = Alignment.Center) {
    when {
      resPhoto != null -> Image(
        painter = androidx.compose.ui.res.painterResource(resPhoto),
        contentDescription = item.nama,
        modifier = Modifier.fillMaxSize().clip(shape),
        contentScale = contentScale
      )
      decoded != null -> Image(bitmap = decoded, contentDescription = item.nama, modifier = Modifier.fillMaxSize().clip(shape), contentScale = contentScale)
      item.imageUrl.isNotBlank() -> AsyncImage(
        model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current).data(item.imageUrl).crossfade(true).build(),
        contentDescription = item.nama,
        modifier = Modifier.fillMaxSize().clip(shape),
        contentScale = contentScale
      )
      else -> Icon(Icons.Filled.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), modifier = Modifier.size(28.dp))
    }
  }
}

@Composable
fun FilterPill(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  count: Int? = null
) {
  Surface(
    modifier = modifier.clickable(onClick = onClick),
    shape = RoundedCornerShape(50),
    color = if (selected) MaterialTheme.colorScheme.primary else Color.White,
    border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else ArsyCardBorder)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Text(
        label,
        fontSize = 13.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
        color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
      )
      if (count != null) {
        Box(
          modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(if (selected) Color.White.copy(alpha = 0.25f) else ArsyPeach),
          contentAlignment = Alignment.Center
        ) {
          Text("$count", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else MaterialTheme.colorScheme.primary)
        }
      }
    }
  }
}

@Composable
fun GoldChip(text: String, modifier: Modifier = Modifier) {
  Surface(modifier = modifier, shape = RoundedCornerShape(8.dp), color = ArsyGoldSoft) {
    Text(
      text,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = Color(0xFF7A5A00)
    )
  }
}

@Composable
fun TagChip(text: String, bg: Color, fg: Color, modifier: Modifier = Modifier) {
  Surface(modifier = modifier, shape = RoundedCornerShape(7.dp), color = bg) {
    Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = fg)
  }
}

@Composable
fun PrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  containerColor: Color = MaterialTheme.colorScheme.primary,
  contentColor: Color = Color.White,
  enabled: Boolean = true
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    modifier = modifier.height(52.dp),
    shape = RoundedCornerShape(14.dp),
    colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor, disabledContainerColor = containerColor.copy(alpha = 0.4f), disabledContentColor = contentColor.copy(alpha = 0.7f))
  ) {
    if (icon != null) Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
    if (icon != null) Spacer(Modifier.width(8.dp))
    Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
  }
}

@Composable
fun QtyStepper(qty: Int, onInc: () -> Unit, onDec: () -> Unit, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.primary),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).clickable(onClick = onDec), contentAlignment = Alignment.Center) {
      Icon(Icons.Filled.Remove, contentDescription = "Kurangi", tint = Color.White, modifier = Modifier.size(18.dp))
    }
    Text("$qty", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
    Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).clickable(onClick = onInc), contentAlignment = Alignment.Center) {
      Icon(Icons.Filled.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(18.dp))
    }
  }
}

@Composable
fun RoundIconButton(icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier, tint: Color = MaterialTheme.colorScheme.primary, bg: Color = Color.White, badge: Int = 0) {
  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Box(
      modifier = Modifier.size(44.dp).clip(CircleShape).background(bg).border(BorderStroke(1.dp, ArsyCardBorder), CircleShape).clickable(onClick = onClick),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
    if (badge > 0) {
      Box(
        modifier = Modifier.align(Alignment.TopEnd).size(18.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
      ) { Text("$badge", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
    }
  }
}

@Composable
fun StatTile(label: String, value: String, sub: String? = null, accent: Color = MaterialTheme.colorScheme.primary, modifier: Modifier = Modifier) {
  WhiteCard(modifier = modifier, contentPadding = PaddingValues(14.dp)) {
    Text(label, fontSize = 12.sp, color = ArsyTextMuted, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(6.dp))
    Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = accent)
    if (sub != null) {
      Spacer(Modifier.height(2.dp))
      Text(sub, fontSize = 11.sp, color = ArsyTextMuted)
    }
  }
}

@Composable
fun CheckRow(
  title: String,
  subtitle: String,
  price: String,
  checked: Boolean,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  WhiteCard(modifier = modifier, contentPadding = PaddingValues(12.dp), onClick = onToggle) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
      Box(
        modifier = Modifier.size(26.dp).clip(RoundedCornerShape(8.dp)).background(if (checked) MaterialTheme.colorScheme.primary else ArsyPeach),
        contentAlignment = Alignment.Center
      ) {
        if (checked) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
      }
      Column(Modifier.weight(1f)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
        Text(subtitle, fontSize = 11.5.sp, color = ArsyTextMuted, maxLines = 2, overflow = TextOverflow.Ellipsis)
      }
      Text(price, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
    }
  }
}

@Composable
fun EmptyState(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxWidth().padding(vertical = 48.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(Modifier.size(72.dp).clip(CircleShape).background(ArsyPeach), contentAlignment = Alignment.Center) {
      Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp))
    }
    Spacer(Modifier.height(14.dp))
    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
    Text(subtitle, fontSize = 13.sp, color = ArsyTextMuted, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
  }
}
