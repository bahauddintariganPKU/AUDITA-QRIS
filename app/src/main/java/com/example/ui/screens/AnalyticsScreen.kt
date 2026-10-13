package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FraudIndicator
import com.example.model.TransactionItem
import com.example.ui.components.BenfordChartCard
import com.example.ui.components.HourlyDistributionCard
import com.example.ui.components.RiskBreakdownMeter
import com.example.ui.theme.PastelHoney
import com.example.ui.theme.PastelMintContainer
import com.example.ui.theme.PastelMintPrimary
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelRose
import com.example.ui.theme.PastelRoseLight
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun AnalyticsScreen(
  transactions: List<TransactionItem>,
  language: AppLanguage,
  onExportCsvClick: () -> Unit,
  onExportSummaryClick: () -> Unit,
  onScheduleCalendarClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isIndonesian = language == AppLanguage.ID

  val countJamTutup = transactions.count { it.flagJamTutup }
  val countSplitTx = transactions.count { it.flagSplitTx }
  val countNominalBulat = transactions.count { it.flagNominalBulat }
  val countSelfTx = transactions.count { it.flagSelfTx }
  val countBenford = transactions.count { it.flagBenfordAnomali }

  // Cashier integrity ranking
  val cashierMap = transactions.groupBy { it.cashierId }
  val cashierRankings = cashierMap.map { (cashier, list) ->
    val anomalies = list.count { it.totalFraudScore > 0 }
    val total = list.size
    val anomalyRate = if (total > 0) anomalies.toFloat() / total else 0f
    Triple(cashier, list, anomalyRate)
  }.sortedByDescending { it.third }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("analytics_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Column {
        Text(
          text = Strings.tabAnalytics(language),
          style = MaterialTheme.typography.headlineLarge,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Dasbor visual ringkasan performa dan audit forensik mendalam",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Export & Calendar action card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Ekspor & Sinkronisasi Eksternal",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Unduh rekonsiliasi data atau bagikan hasil investigasi ke tim manajemen.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onExportCsvClick,
              colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("analytics_export_csv_btn")
            ) {
              Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Ekspor CSV", fontSize = 12.sp)
            }

            OutlinedButton(
              onClick = onExportSummaryClick,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .weight(1f)
                .testTag("analytics_export_pdf_btn")
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Bagikan Dokumen", fontSize = 11.sp)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedButton(
            onClick = onScheduleCalendarClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("analytics_calendar_schedule_btn")
          ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Jadwalkan Audit Rutin ke Google Calendar", fontSize = 12.sp)
          }
        }
      }
    }

    // Benford Chart
    item {
      BenfordChartCard(items = transactions)
    }

    // Hourly distribution
    item {
      HourlyDistributionCard(items = transactions)
    }

    // Fraud breakdown indicator cards
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Frekuensi Pelanggaran Berdasarkan Aturan Forensik",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(12.dp))

          IndicatorCountRow("1. Jam Operasional Tutup", countJamTutup, transactions.size)
          IndicatorCountRow("2. Transaksi Dipecah (Split Tx)", countSplitTx, transactions.size)
          IndicatorCountRow("3. Nominal Bulat Besar (Gestun)", countNominalBulat, transactions.size)
          IndicatorCountRow("4. Kasir Scan Akun Sendiri (Self-Tx)", countSelfTx, transactions.size)
          IndicatorCountRow("5. Anomali Angka Rekayasa Benford", countBenford, transactions.size)
        }
      }
    }

    // Cashier Integrity Ranking
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Integritas & Evaluasi Kasir",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Tingkat kepatuhan dan anomali transaksi per ID Kasir",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          cashierRankings.forEach { (cashier, list, rate) ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                      if (rate > 0.4f) PastelRoseLight else PastelMintContainer
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = cashier.takeLast(2),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rate > 0.4f) PastelRose else PastelMintPrimary
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(cashier, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                  Text(
                    text = "${list.size} Transaksi Total",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                val flagCount = list.count { it.totalFraudScore > 0 }
                Text(
                  text = if (flagCount > 0) "$flagCount Kasus Anomali" else "Integritas Sempurna",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (flagCount > 0) PastelRose else PastelMintPrimary
                )
                Text(
                  text = "${"%.0f".format(rate * 100)}% Rasio Anomali",
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
          }
        }
      }
    }
  }
}

@Composable
private fun IndicatorCountRow(
  title: String,
  count: Int,
  total: Int
) {
  val pct = if (total > 0) count * 100f / total else 0f
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurface
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = "$count kasus",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (count > 0) PastelRose else PastelMintPrimary
      )
      Spacer(modifier = Modifier.width(6.dp))
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(4.dp))
          .background(if (count > 0) PastelRoseLight else PastelMintContainer)
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = "${"%.0f".format(pct)}%",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = if (count > 0) PastelRose else PastelMintPrimary
        )
      }
    }
  }
}
