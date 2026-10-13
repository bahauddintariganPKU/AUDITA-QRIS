package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.PastelMintContainer
import com.example.ui.theme.PastelMintPrimary

@Composable
fun AddTransactionDialog(
  onDismiss: () -> Unit,
  onAdd: (cashierId: String, customerId: String, amount: Long, customHour: Int?) -> Unit
) {
  var cashierId by remember { mutableStateOf("KASIR_01") }
  var customerId by remember { mutableStateOf("CUST_99") }
  var amountStr by remember { mutableStateOf("150000") }
  var isNightTime by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("add_transaction_dialog"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = "Uji Forensik Transaksi Baru",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Masukkan data transaksi untuk diaudit otomatis secara real-time.",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preset templates
        Text("Template Cepat Kasus:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          SuggestionChip(
            onClick = {
              cashierId = "KASIR_03"
              customerId = "CUST_77"
              amountStr = "5000000"
              isNightTime = false
            },
            label = { Text("Gestun 5jt", fontSize = 10.sp) },
            shape = RoundedCornerShape(8.dp)
          )
          SuggestionChip(
            onClick = {
              cashierId = "KASIR_02"
              customerId = "KASIR_02"
              amountStr = "250000"
              isNightTime = false
            },
            label = { Text("Self-Tx Kasir", fontSize = 10.sp) },
            shape = RoundedCornerShape(8.dp)
          )
          SuggestionChip(
            onClick = {
              cashierId = "KASIR_01"
              customerId = "CUST_55"
              amountStr = "600000"
              isNightTime = false
            },
            label = { Text("Split Tx 600rb", fontSize = 10.sp) },
            shape = RoundedCornerShape(8.dp)
          )
          SuggestionChip(
            onClick = {
              cashierId = "KASIR_02"
              customerId = "CUST_12"
              amountStr = "150000"
              isNightTime = true
            },
            label = { Text("Jam Tutup (23:45)", fontSize = 10.sp) },
            shape = RoundedCornerShape(8.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = cashierId,
          onValueChange = { cashierId = it },
          label = { Text("ID Kasir") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_cashier_id"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = customerId,
          onValueChange = { customerId = it },
          label = { Text("ID Konsumen / User") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_customer_id"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = amountStr,
          onValueChange = { amountStr = it.filter { char -> char.isDigit() } },
          label = { Text("Nominal Transaksi (Rp)") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_amount"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Time toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          TextButton(
            onClick = { isNightTime = false },
            colors = ButtonDefaults.textButtonColors(
              containerColor = if (!isNightTime) PastelMintContainer else MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Siang / Jam Buka (14:00)", fontSize = 11.sp)
          }

          TextButton(
            onClick = { isNightTime = true },
            colors = ButtonDefaults.textButtonColors(
              containerColor = if (isNightTime) PastelMintContainer else MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Malam / Tutup (23:45)", fontSize = 11.sp)
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
            Text("Batal")
          }
          Spacer(modifier = Modifier.padding(horizontal = 4.dp))
          Button(
            onClick = {
              val amt = amountStr.toLongOrNull() ?: 50000L
              onAdd(cashierId, customerId, amt, if (isNightTime) 23 else 14)
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("submit_transaction_button")
          ) {
            Text("Simpan & Audit")
          }
        }
      }
    }
  }
}
