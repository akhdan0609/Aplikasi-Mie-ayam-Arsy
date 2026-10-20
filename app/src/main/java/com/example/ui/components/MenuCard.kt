package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.MenuItemEntity
import com.example.ui.theme.AvailableBadge
import com.example.ui.theme.IceBadge
import com.example.ui.theme.SoldOutBadge
import com.example.ui.theme.SpicyBadge
import com.example.ui.viewmodel.MenuViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MenuCard(
  item: MenuItemEntity,
  totalQty: Int,
  variantDetails: List<String>,
  isAdminMode: Boolean,
  onPlus: () -> Unit,
  onMinus: () -> Unit,
  onEdit: () -> Unit,
  onToggleStatus: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isAvailable = item.isAvailable
  val isSpicy = item.hasSpiceLevel || item.nama.contains("jebew", ignoreCase = true)
  val isDrink = item.kategori.equals("Minuman", ignoreCase = true)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("menu_card_${item.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isAvailable) {
        MaterialTheme.colorScheme.surface
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isAvailable) 2.dp else 0.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        // Icon and details
        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Food category badge icon
          Box(
            modifier = Modifier
              .size(70.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(
                when {
                  isSpicy -> SpicyBadge.copy(alpha = 0.15f)
                  isDrink -> IceBadge.copy(alpha = 0.15f)
                  else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                }
              ),
            contentAlignment = Alignment.Center
          ) {
            if (item.imageUrl.isNotBlank()) {
              AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                  .data(item.imageUrl)
                  .crossfade(true)
                  .build(),
                contentDescription = item.nama,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
              )
            } else {
              Icon(
                imageVector = when {
                  isSpicy -> Icons.Default.LocalFireDepartment
                  isDrink -> Icons.Default.LocalDrink
                  else -> Icons.Default.Restaurant
                },
                contentDescription = item.kategori,
                tint = when {
                  isSpicy -> SpicyBadge
                  isDrink -> IceBadge
                  else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(28.dp)
              )
            }
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = item.nama,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isAvailable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
              if (item.isBestSeller) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.primaryContainer,
                  modifier = Modifier.padding(start = 2.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Star,
                      contentDescription = "Favorit",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                      text = "Favorit",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.primary,
                      fontSize = 10.sp
                    )
                  }
                }
              }
            }

            Text(
              text = MenuViewModel.formatRupiah(item.harga),
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }

        // Availability Badge
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isAvailable) AvailableBadge.copy(alpha = 0.12f) else SoldOutBadge.copy(alpha = 0.12f)
        ) {
          Text(
            text = if (isAvailable) "Tersedia" else "Habis",
            color = if (isAvailable) AvailableBadge else SoldOutBadge,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      if (item.deskripsi.isNotBlank()) {
        Text(
          text = item.deskripsi,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 8.dp)
        )
      }

      // Variant tags if item has spice level or temperature options
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        if (isSpicy) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = SpicyBadge.copy(alpha = 0.1f)
          ) {
            Text(
              text = "Pilihan Level 1-3",
              color = SpicyBadge,
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
        if (item.hasDrinkTemp || (isDrink && !item.nama.contains("mineral", ignoreCase = true))) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = IceBadge.copy(alpha = 0.1f)
          ) {
            Text(
              text = "Pilihan ICE / HOT",
              color = IceBadge,
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      // Display selected variants breakdown if any
      if (variantDetails.isNotEmpty()) {
        FlowRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          variantDetails.forEach { detail ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
              Text(
                text = detail,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom Row: Qty Controller & Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = item.kategori,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )

        // Stepper buttons
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          AnimatedVisibility(visible = totalQty > 0) {
            FilledIconButton(
              onClick = onMinus,
              modifier = Modifier
                .size(36.dp)
                .testTag("btn_minus_${item.id}"),
              colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
              )
            ) {
              Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Kurang",
                modifier = Modifier.size(18.dp)
              )
            }
          }

          AnimatedVisibility(visible = totalQty > 0) {
            Text(
              text = "$totalQty",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier
                .padding(horizontal = 4.dp)
                .testTag("qty_label_${item.id}")
            )
          }

          FilledIconButton(
            onClick = onPlus,
            enabled = isAvailable || isAdminMode,
            modifier = Modifier
              .size(36.dp)
              .testTag("btn_plus_${item.id}"),
            colors = IconButtonDefaults.filledIconButtonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary,
              disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
              disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Tambah",
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Admin Controls if in Admin Mode
      if (isAdminMode) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Toggle status button
          OutlinedButton(
            onClick = onToggleStatus,
            modifier = Modifier.testTag("admin_toggle_status_${item.id}"),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(
              imageVector = if (isAvailable) Icons.Default.Close else Icons.Default.Check,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isAvailable) "Set Habis" else "Set Tersedia",
              fontSize = 12.sp
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(
              onClick = onEdit,
              modifier = Modifier.testTag("admin_edit_btn_${item.id}")
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Ubah",
                tint = MaterialTheme.colorScheme.primary
              )
            }
            IconButton(
              onClick = onDelete,
              modifier = Modifier.testTag("admin_delete_btn_${item.id}")
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Hapus",
                tint = MaterialTheme.colorScheme.error
              )
            }
          }
        }
      }
    }
  }
}
