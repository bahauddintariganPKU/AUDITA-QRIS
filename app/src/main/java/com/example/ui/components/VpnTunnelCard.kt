package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NetworkPing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PastelHoney
import com.example.ui.theme.PastelHoneyLight
import com.example.ui.theme.PastelLavender
import com.example.ui.theme.PastelLavenderContainer
import com.example.ui.theme.PastelMintContainer
import com.example.ui.theme.PastelMintDark
import com.example.ui.theme.PastelMintPrimary
import com.example.ui.theme.PastelPeachLight
import com.example.ui.theme.PastelRose
import com.example.ui.theme.PastelRoseLight
import com.example.ui.theme.RiskSafeBg
import com.example.ui.theme.RiskSafeText
import com.example.vpn.VpnConnectionState
import com.example.vpn.VpnStatus

@Composable
fun VpnTunnelCard(
  vpnStatus: VpnStatus,
  onConnectClick: () -> Unit,
  onDisconnectClick: () -> Unit,
  onSyncDbClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isConnected = vpnStatus.state == VpnConnectionState.CONNECTED
  val isConnecting = vpnStatus.state == VpnConnectionState.CONNECTING

  val (statusBg, statusText, statusIcon) = when (vpnStatus.state) {
    VpnConnectionState.CONNECTED -> Triple(
      RiskSafeBg,
      RiskSafeText,
      Icons.Default.Lock
    )
    VpnConnectionState.CONNECTING -> Triple(
      PastelHoneyLight,
      PastelHoney,
      Icons.Default.Sync
    )
    else -> Triple(
      MaterialTheme.colorScheme.surfaceVariant,
      MaterialTheme.colorScheme.onSurfaceVariant,
      Icons.Default.LockOpen
    )
  }

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier
      .fillMaxWidth()
      .testTag("vpn_tunnel_card")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top status row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (isConnected) PastelMintContainer else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.VpnKey,
              contentDescription = "VPN",
              tint = if (isConnected) PastelMintPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "VPN Database Server",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = vpnStatus.selectedProfile.name,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Status badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(statusBg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (isConnecting) {
              CircularProgressIndicator(
                modifier = Modifier.size(10.dp),
                strokeWidth = 1.5.dp,
                color = PastelHoney
              )
              Spacer(modifier = Modifier.width(4.dp))
            } else {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (isConnected) RiskSafeText else Color.Gray)
              )
              Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
              text = when (vpnStatus.state) {
                VpnConnectionState.CONNECTED -> "TERHUBUNG"
                VpnConnectionState.CONNECTING -> "HANDSHAKE..."
                else -> "TERPUTUS"
              },
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = statusText
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Host & Tunnel info banner
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Endpoint:",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "${vpnStatus.selectedProfile.host}:${vpnStatus.selectedProfile.port}",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Protokol & Sandi:",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "${vpnStatus.protocol.displayName} (${vpnStatus.protocol.cipher.substringBefore(" ")})",
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }

          if (isConnected) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "IP Tunnel Virtual:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${vpnStatus.virtualIp} • Latensi ${vpnStatus.latencyMs} ms",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RiskSafeText
              )
            }
          }
        }
      }

      // Telemetry row when connected
      AnimatedVisibility(visible = isConnected) {
        Column {
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "⏱️ Terhubung: ${formatSeconds(vpnStatus.connectedTimeSeconds)}",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "⬆ ${formatBytes(vpnStatus.bytesSent)}  ⬇ ${formatBytes(vpnStatus.bytesReceived)}",
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (!isConnected) {
          Button(
            onClick = onConnectClick,
            enabled = !isConnecting,
            colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("btn_connect_vpn")
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isConnecting) "Menghubungkan..." else "Sambungkan VPN",
              fontSize = 12.sp
            )
          }
        } else {
          OutlinedButton(
            onClick = onDisconnectClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("btn_disconnect_vpn")
          ) {
            Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Putuskan VPN", fontSize = 12.sp, color = PastelRose)
          }

          Button(
            onClick = onSyncDbClick,
            colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("btn_vpn_sync_database")
          ) {
            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Kueri Database", fontSize = 12.sp)
          }
        }
      }
    }
  }
}

private fun formatSeconds(sec: Long): String {
  val m = sec / 60
  val s = sec % 60
  return "%02d:%02d".format(m, s)
}

private fun formatBytes(bytes: Long): String {
  return when {
    bytes >= 1024 * 1024 -> "%.1f MB".format(bytes / (1024f * 1024f))
    bytes >= 1024 -> "%.1f KB".format(bytes / 1024f)
    else -> "$bytes B"
  }
}
