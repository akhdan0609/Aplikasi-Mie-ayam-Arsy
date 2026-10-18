package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShopConfigEntity
import com.example.ui.theme.WhatsAppDarkGreen
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.CartItem
import com.example.ui.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummarySheet(
  cartItems: List<CartItem>,
  totalPrice: Long,
  totalCount: Int,
  shopConfig: ShopConfigEntity,
  paymentMethod: String,
  customerName: String,
  customerNotes: String,
  onUpdateQty: (CartItem, Int) -> Unit,
  onClearCart: () -> Unit,
  onPaymentMethodChange: (String) -> Unit,
  onCustomerNameChange: (String) -> Unit,
  onCustomerNotesChange: (String) -> Unit,
  onSendOrderWhatsApp: () -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val context = LocalContext.current

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingBag,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp)
            )
          }
          Column {
            Text(
              text = "Ringkasan Pesanan",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "$totalCount Menu Terpilih",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (cartItems.isNotEmpty()) {
          TextButton(
            onClick = onClearCart,
            modifier = Modifier.testTag("btn_clear_cart")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteSweep,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Kosongkan", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
          }
        }
      }

      HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

      if (cartItems.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "Keranjang Anda Masih Kosong",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "Pilih menu mie ayam dan minuman favorit Anda di atas",
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
            .weight(1f, fill = false),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(cartItems, key = { it.key }) { cartItem ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = cartItem.item.nama,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                if (cartItem.variant.isNotBlank()) {
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(top = 2.dp)
                  ) {
                    Text(
                      text = cartItem.variant,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                      color = MaterialTheme.colorScheme.onPrimaryContainer,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "${MenuViewModel.formatRupiah(cartItem.item.harga)} x ${cartItem.quantity} = ${MenuViewModel.formatRupiah(cartItem.subtotal)}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(top = 4.dp)
                )
              }

              // Stepper in sheet
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                IconButton(
                  onClick = { onUpdateQty(cartItem, cartItem.quantity - 1) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Kurang",
                    modifier = Modifier.size(16.dp)
                  )
                }

                Text(
                  text = "${cartItem.quantity}",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  modifier = Modifier.padding(horizontal = 4.dp)
                )

                IconButton(
                  onClick = { onUpdateQty(cartItem, cartItem.quantity + 1) },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah",
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }

          item {
            // Customer Details Inputs
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedTextField(
                value = customerName,
                onValueChange = onCustomerNameChange,
                label = { Text("Nama Pemesan (Opsional)") },
                placeholder = { Text("Contoh: Kak Ardi") },
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_customer_name"),
                shape = RoundedCornerShape(12.dp)
              )

              OutlinedTextField(
                value = customerNotes,
                onValueChange = onCustomerNotesChange,
                label = { Text("Catatan Pesanan (Opsional)") },
                placeholder = { Text("Contoh: Kuah dipisah, jangan pakai sawi") },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_customer_notes"),
                shape = RoundedCornerShape(12.dp)
              )
            }
          }

          item {
            // Payment Method Selector
            Text(
              text = "Metode Pembayaran",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              // Cash Option
              Card(
                modifier = Modifier
                  .weight(1f)
                  .clickable { onPaymentMethodChange("Tunai") }
                  .testTag("payment_method_tunai"),
                shape = RoundedCornerShape(12.dp),
                border = if (paymentMethod == "Tunai") {
                  androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else null,
                colors = CardDefaults.cardColors(
                  containerColor = if (paymentMethod == "Tunai") {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                  } else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    tint = if (paymentMethod == "Tunai") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "Tunai",
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = "Bayar di Kasir",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }

              // Transfer Option
              Card(
                modifier = Modifier
                  .weight(1f)
                  .clickable { onPaymentMethodChange("Transfer") }
                  .testTag("payment_method_transfer"),
                shape = RoundedCornerShape(12.dp),
                border = if (paymentMethod == "Transfer") {
                  androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                } else null,
                colors = CardDefaults.cardColors(
                  containerColor = if (paymentMethod == "Transfer") {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                  } else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = if (paymentMethod == "Transfer") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "Transfer Bank",
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = "BCA/BRI/QRIS",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }

            // Bank details card if Transfer is selected
            if (paymentMethod == "Transfer") {
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 10.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Rekening Pembayaran:",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                      onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Info Bank", shopConfig.infoBank))
                        Toast.makeText(context, "Nomor rekening disalin!", Toast.LENGTH_SHORT).show()
                      },
                      modifier = Modifier.size(28.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Salin Rekening",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                  Text(
                    text = shopConfig.infoBank,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                  )
                  Text(
                    text = "*Lampirkan bukti transfer pada chat WhatsApp setelah pesan terkirim.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 6.dp)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total & WhatsApp Order Button
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Total Pembayaran",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = MenuViewModel.formatRupiah(totalPrice),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("sheet_total_price")
          )
        }

        Button(
          onClick = onSendOrderWhatsApp,
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("btn_send_order_whatsapp"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = WhatsAppGreen,
            contentColor = Color.White
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Send,
              contentDescription = null,
              modifier = Modifier.size(20.dp)
            )
            Text(
              text = "Kirim Pesanan via WhatsApp",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              fontSize = 16.sp
            )
          }
        }
      }
    }
  }
}
