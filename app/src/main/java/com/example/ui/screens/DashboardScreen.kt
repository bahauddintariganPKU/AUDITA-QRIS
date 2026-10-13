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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.sync.SyncStatus
import com.example.model.RiskLevel
import com.example.model.TransactionItem
import com.example.ui.components.BenfordChartCard
import com.example.ui.components.FraudFlagChip
import com.example.ui.components.HourlyDistributionCard
import com.example.ui.components.PastelKpiCard
import com.example.ui.components.RiskBadge
import com.example.ui.components.RiskBreakdownMeter
import com.example.ui.components.SyncStatusBanner
import com.example.ui.theme.PastelHoney
import com.example.ui.theme.PastelHoneyLight
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelLavenderContainer
import com.example.ui.theme.PastelMintContainer
import com.example.ui.theme.PastelMintPrimary
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPeachLight
import com.example.ui.theme.PastelRose
import com.example.ui.theme.PastelRoseLight
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun DashboardScreen(
  transactions: List<TransactionItem>,
  syncStatus: SyncStatus,
  vpnStatus: com.example.vpn.VpnStatus,
  language: AppLanguage,
  onTransactionClick: (TransactionItem) -> Unit,
  onAddNewClick: () -> Unit,
  onSyncClick: () -> Unit,
  onConnectVpnClick: () -> Unit,
  onDisconnectVpnClick: () -> Unit,
  onSyncVpnDbClick: () -> Unit,
  onExportCsvClick: () -> Unit,
  onScheduleCalendarClick: () -> Unit,
  onGenerateSyntheticClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isIndonesian = language == AppLanguage.ID

  val totalCount = transactions.size
  val totalVolume = transactions.sumOf { it.amount }
  val highRiskCount = transactions.count { it.riskLevel == RiskLevel.HIGH_RISK }
  val lowRiskCount = transactions.count { it.riskLevel == RiskLevel.LOW_RISK }
  val safeCount = transactions.count { it.riskLevel == RiskLevel.SAFE }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("dashboard_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Column {
        Text(
          text = Strings.appTitle(language),
          style = MaterialTheme.typography.headlineLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = Strings.subtitle(language),
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Sync Banner
    item {
      SyncStatusBanner(
        syncStatus = syncStatus,
        onSyncClick = onSyncClick
      )
    }

    // VPN Database Server Tunnel Card
    item {
      com.example.ui.components.VpnTunnelCard(
        vpnStatus = vpnStatus,
        onConnectClick = onConnectVpnClick,
        onDisconnectClick = onDisconnectVpnClick,
        onSyncDbClick = onSyncVpnDbClick
      )
    }

    // KPI Cards Grid (2x2)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          PastelKpiCard(
            title = Strings.totalTransactions(language),
            value = "$totalCount",
            subtitle = "$safeCount Valid",
            icon = Icons.Default.Payments,
            accentColor = PastelMintPrimary,
            backgroundColor = PastelMintContainer,
            modifier = Modifier.weight(1f)
          )
          PastelKpiCard(
            title = Strings.totalAmount(language),
            value = "Rp %,d".format(totalVolume),
            subtitle = "Volume Terproses",
            icon = Icons.Default.Shield,
            accentColor = PastelLavender,
            backgroundColor = PastelLavenderContainer,
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          PastelKpiCard(
            title = Strings.highRisk(language),
            value = "$highRiskCount",
            subtitle = "Investigasi Kasir Segera",
            icon = Icons.Default.Warning,
            accentColor = PastelRose,
            backgroundColor = PastelRoseLight,
            modifier = Modifier.weight(1f)
          )
          PastelKpiCard(
            title = Strings.lowRisk(language),
            value = "$lowRiskCount",
            subtitle = "Pantau Transaksi",
            icon = Icons.Default.CheckCircle,
            accentColor = PastelHoney,
            backgroundColor = PastelHoneyLight,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Risk Breakdown Progress
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        RiskBreakdownMeter(
          items = transactions,
          modifier = Modifier.padding(16.dp)
        )
      }
    }

    // Quick Action Buttons
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onAddNewClick,
          colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("dashboard_add_tx_button")
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Uji Transaksi", fontSize = 12.sp)
        }

        OutlinedButton(
          onClick = onExportCsvClick,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("dashboard_export_csv_button")
        ) {
          Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Ekspor CSV", fontSize = 12.sp)
        }

        OutlinedButton(
          onClick = onScheduleCalendarClick,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("dashboard_calendar_button")
        ) {
          Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Kalender", fontSize = 12.sp)
        }
      }
    }

    // Benford Law First-Digit Chart
    item {
      BenfordChartCard(items = transactions)
    }

    // Hourly Timeline Distribution
    item {
      HourlyDistributionCard(items = transactions)
    }

    // Section: Top Anomalies Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Temuan Anomali Terkini",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onGenerateSyntheticClick) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Generate Test",
            tint = PastelMintPrimary
          )
        }
      }
    }

    // Top Anomaly List
    val flaggedItems = transactions.filter { it.totalFraudScore > 0 }
    if (flaggedItems.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "✅ Tidak ada transaksi anomali. Semua data QRIS bersih dan valid.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    } else {
      items(flaggedItems) { item ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onTransactionClick(item) }
            .testTag("anomaly_item_${item.rrn}")
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = item.rrn,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                RiskBadge(riskLevel = item.riskLevel)
              }
              Text(
                text = "Rp %,d".format(item.amount),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Kasir: ${item.cashierId} • Konsumen: ${item.customerId}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = item.timeFormatted,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Flag chips
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              item.activeFlags.forEach { flag ->
                FraudFlagChip(flag = flag, isIndonesian = isIndonesian)
              }
            }
          }
        }
      }
    }
  }
}
