package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import com.example.model.FraudIndicator
import com.example.model.RiskLevel
import com.example.model.TransactionItem
import com.example.ui.theme.PastelMintContainer
import com.example.ui.theme.PastelMintPrimary
import com.example.ui.theme.PastelRose
import com.example.ui.theme.PastelRoseLight
import com.example.ui.theme.RiskHighBg
import com.example.ui.theme.RiskHighText
import com.example.ui.theme.RiskSafeBg
import com.example.ui.theme.RiskSafeText

@Composable
fun AuditDetailDialog(
  item: TransactionItem,
  onDismiss: () -> Unit,
  onDelete: (TransactionItem) -> Unit,
  isIndonesian: Boolean = true
) {
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("audit_detail_dialog"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = item.rrn,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = item.timeFormatted,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_audit_dialog")) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Amount & Conclusion Box
        val headerBg = when (item.riskLevel) {
          RiskLevel.HIGH_RISK -> RiskHighBg
          RiskLevel.LOW_RISK -> Color(0xFFFFF7E6)
          RiskLevel.SAFE -> RiskSafeBg
        }
        val headerText = when (item.riskLevel) {
          RiskLevel.HIGH_RISK -> RiskHighText
          RiskLevel.LOW_RISK -> Color(0xFFD97706)
          RiskLevel.SAFE -> RiskSafeText
        }

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = headerBg,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Rp %,d".format(item.amount),
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              color = headerText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              RiskBadge(riskLevel = item.riskLevel)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Total Skor Fraud: ${item.totalFraudScore} / 5",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = headerText
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Entity identities
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("ID Kasir", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(item.cashierId, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
          }
          Column(modifier = Modifier.weight(1f)) {
            Text("ID Konsumen", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              item.customerId,
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (item.flagSelfTx) PastelRose else MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        Spacer(modifier = Modifier.height(14.dp))

        // Forensic Indicator Breakdown
        Text(
          text = if (isIndonesian) "Hasil Uji 5 Formula Forensik:" else "5 Forensic Formula Results:",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        ForensicRow(
          title = "1. Jam Operasional Toko",
          desc = FraudIndicator.JAM_TUTUP.getDescription(isIndonesian),
          triggered = item.flagJamTutup
        )
        ForensicRow(
          title = "2. Transaksi Dipecah (Split Tx)",
          desc = FraudIndicator.SPLIT_TX.getDescription(isIndonesian),
          triggered = item.flagSplitTx
        )
        ForensicRow(
          title = "3. Nominal Bulat Besar (Gestun)",
          desc = FraudIndicator.NOMINAL_BULAT.getDescription(isIndonesian),
          triggered = item.flagNominalBulat
        )
        ForensicRow(
          title = "4. Kolusi / Kasir Scan Sendiri",
          desc = FraudIndicator.SELF_TX.getDescription(isIndonesian),
          triggered = item.flagSelfTx
        )
        ForensicRow(
          title = "5. Hukum Benford (Digit 7, 8, 9)",
          desc = FraudIndicator.BENFORD_ANOMALI.getDescription(isIndonesian),
          triggered = item.flagBenfordAnomali
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText(
                "Audit ${item.rrn}",
                "RRN: ${item.rrn}\nNominal: Rp ${item.amount}\nKasir: ${item.cashierId}\nKonsumen: ${item.customerId}\nSkor Fraud: ${item.totalFraudScore}\nStatus: ${item.conclusionText}"
              )
              clipboard.setPrimaryClip(clip)
              Toast.makeText(context, "Data audit disalin ke clipboard", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
              .weight(1f)
              .testTag("copy_audit_evidence_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Salin Bukti", fontSize = 12.sp)
          }

          Button(
            onClick = { onDelete(item) },
            modifier = Modifier
              .weight(1f)
              .testTag("delete_transaction_button"),
            colors = ButtonDefaults.buttonColors(containerColor = PastelRose),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Hapus", fontSize = 12.sp)
          }
        }
      }
    }
  }
}

@Composable
private fun ForensicRow(
  title: String,
  desc: String,
  triggered: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(20.dp)
        .clip(CircleShape)
        .background(if (triggered) PastelRoseLight else PastelMintContainer),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (triggered) Icons.Default.Warning else Icons.Default.Check,
        contentDescription = null,
        tint = if (triggered) PastelRose else PastelMintPrimary,
        modifier = Modifier.size(12.dp)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = title,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = if (triggered) PastelRose else MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = if (triggered) "PELANGGARAN (+1)" else "Lolos (0)",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = if (triggered) PastelRose else RiskSafeText
        )
      }
      Text(
        text = desc,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
