package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.sync.SyncState
import com.example.data.sync.SyncStatus
import com.example.model.FraudIndicator
import com.example.model.RiskLevel
import com.example.ui.theme.PastelHoney
import com.example.ui.theme.PastelHoneyLight
import com.example.ui.theme.PastelMintContainer
import com.example.ui.theme.PastelMintDark
import com.example.ui.theme.PastelMintPrimary
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPeachLight
import com.example.ui.theme.PastelRose
import com.example.ui.theme.PastelRoseLight
import com.example.ui.theme.RiskHighBg
import com.example.ui.theme.RiskHighDarkBg
import com.example.ui.theme.RiskHighDarkText
import com.example.ui.theme.RiskHighText
import com.example.ui.theme.RiskMediumBg
import com.example.ui.theme.RiskMediumDarkBg
import com.example.ui.theme.RiskMediumDarkText
import com.example.ui.theme.RiskMediumText
import com.example.ui.theme.RiskSafeBg
import com.example.ui.theme.RiskSafeDarkBg
import com.example.ui.theme.RiskSafeDarkText
import com.example.ui.theme.RiskSafeText

@Composable
fun RiskBadge(
  riskLevel: RiskLevel,
  modifier: Modifier = Modifier,
  isDark: Boolean = isSystemInDarkTheme()
) {
  val (bgColor, textColor, text) = when (riskLevel) {
    RiskLevel.HIGH_RISK -> Triple(
      if (isDark) RiskHighDarkBg else RiskHighBg,
      if (isDark) RiskHighDarkText else RiskHighText,
      "RISIKO TINGGI"
    )
    RiskLevel.LOW_RISK -> Triple(
      if (isDark) RiskMediumDarkBg else RiskMediumBg,
      if (isDark) RiskMediumDarkText else RiskMediumText,
      "PANTAU"
    )
    RiskLevel.SAFE -> Triple(
      if (isDark) RiskSafeDarkBg else RiskSafeBg,
      if (isDark) RiskSafeDarkText else RiskSafeText,
      "AMAN / VALID"
    )
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = text,
      color = textColor,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

@Composable
fun FraudFlagChip(
  flag: FraudIndicator,
  modifier: Modifier = Modifier,
  isIndonesian: Boolean = true
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(MaterialTheme.colorScheme.surfaceVariant)
      .padding(horizontal = 6.dp, vertical = 3.dp)
  ) {
    Text(
      text = flag.getLabel(isIndonesian),
      fontSize = 10.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
fun SyncStatusBanner(
  syncStatus: SyncStatus,
  onSyncClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val (icon, bgCol, text) = when {
    syncStatus.isOfflineMode -> Triple(
      Icons.Default.CloudOff,
      PastelPeachLight,
      "Mode Offline: Data tersimpan aman di database lokal Room"
    )
    syncStatus.state == SyncState.SYNCING -> Triple(
      Icons.Default.Sync,
      PastelHoneyLight,
      "Menyinkronkan transaksi ke cloud dengan enkripsi AES-256..."
    )
    else -> Triple(
      Icons.Default.CloudDone,
      PastelMintContainer,
      "Awan Tersinkron • ${syncStatus.lastSyncTime} (${syncStatus.encryptedHash})"
    )
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onSyncClick() }
      .testTag("sync_status_banner"),
    color = bgCol
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(18.dp),
        tint = PastelMintPrimary
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF1E2E25),
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
fun PastelKpiCard(
  title: String,
  value: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  backgroundColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.testTag("kpi_card_${title.lowercase().replace(" ", "_")}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.7f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = value,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
      )
      Text(
        text = subtitle,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
      )
    }
  }
}
