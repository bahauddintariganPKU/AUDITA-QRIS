package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.sync.SyncStatus
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.PastelMintContainer
import com.example.ui.theme.PastelMintPrimary
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelRose
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun SettingsScreen(
  syncStatus: SyncStatus,
  vpnStatus: com.example.vpn.VpnStatus,
  themeMode: AppThemeMode,
  language: AppLanguage,
  notifyHighRisk: Boolean,
  notifyDailyReminder: Boolean,
  reminderHour: Int,
  onThemeModeChange: (AppThemeMode) -> Unit,
  onLanguageChange: (AppLanguage) -> Unit,
  onToggleOffline: (Boolean) -> Unit,
  onToggleAutoSync: (Boolean) -> Unit,
  onTriggerSync: () -> Unit,
  onConnectVpn: () -> Unit,
  onDisconnectVpn: () -> Unit,
  onSelectVpnProfile: (com.example.vpn.DatabaseServerProfile) -> Unit,
  onSelectVpnProtocol: (com.example.vpn.VpnProtocol) -> Unit,
  onTestVpnPing: () -> Unit,
  onSyncVpnDatabase: () -> Unit,
  onToggleNotifyHighRisk: (Boolean) -> Unit,
  onToggleNotifyDailyReminder: (Boolean) -> Unit,
  onReminderHourChange: (Int) -> Unit,
  onTestHighRiskNotification: () -> Unit,
  onTestReminderNotification: () -> Unit,
  onScheduleCalendarClick: () -> Unit,
  onResetDemoData: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  // Permission launcher for POST_NOTIFICATIONS (Android 13+)
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      Toast.makeText(context, "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
    }
  }

  fun requestNotificationPermissionIfNeeded() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("settings_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Column {
        Text(
          text = Strings.tabSettings(language),
          style = MaterialTheme.typography.headlineLarge,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Konfigurasi tampilan pastel, sinkronisasi awan, dan sistem notifikasi",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Section 1: Tema & Mode Gelap Otomatis
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DarkMode, contentDescription = null, tint = PastelMintPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mode Gelap Otomatis", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Pilih mode tampilan atau aktifkan otomatis untuk kenyamanan malam hari (19:00 - 06:00).",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = themeMode == AppThemeMode.AUTO,
              onClick = { onThemeModeChange(AppThemeMode.AUTO) },
              label = { Text("🌙 Otomatis", fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PastelMintContainer,
                selectedLabelColor = PastelMintPrimary
              )
            )
            FilterChip(
              selected = themeMode == AppThemeMode.LIGHT,
              onClick = { onThemeModeChange(AppThemeMode.LIGHT) },
              label = { Text("☀️ Terang", fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PastelMintContainer,
                selectedLabelColor = PastelMintPrimary
              )
            )
            FilterChip(
              selected = themeMode == AppThemeMode.DARK,
              onClick = { onThemeModeChange(AppThemeMode.DARK) },
              label = { Text("🌑 Gelap", fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PastelMintContainer,
                selectedLabelColor = PastelMintPrimary
              )
            )
          }
        }
      }
    }

    // Section 2: Mode Offline & Awan Enkripsi
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Storage, contentDescription = null, tint = PastelMintPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Mode Offline & Sinkronisasi Awan", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Data disimpan di database Room lokal dan disinkronkan secara otomatis dengan enkripsi AES-256.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Offline Switch
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Paksa Mode Offline", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              Text("Akses data murni offline tanpa sinkronisasi jaringan", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
              checked = syncStatus.isOfflineMode,
              onCheckedChange = { onToggleOffline(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = PastelMintPrimary, checkedTrackColor = PastelMintContainer)
            )
          }

          Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

          // Auto Sync Switch
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Sinkronisasi Awan Otomatis", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              Text("Cadangan otomatis terenkripsi saat data berubah", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
              checked = syncStatus.isAutoSyncEnabled,
              onCheckedChange = { onToggleAutoSync(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = PastelMintPrimary, checkedTrackColor = PastelMintContainer)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = onTriggerSync,
            colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_sync_now_btn"),
            enabled = !syncStatus.isOfflineMode
          ) {
            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sinkronkan Sekarang (${syncStatus.lastSyncTime})", fontSize = 12.sp)
          }
        }
      }
    }

    // Section 3: Koneksi VPN Server Database
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().testTag("settings_vpn_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Security, contentDescription = null, tint = PastelMintPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Koneksi VPN Server Database", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Koneksi terenkripsi langsung ke server database audit forensik internal perbankan.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Server Endpoint Profiles
          Text("Pilih Server Database:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.height(6.dp))
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            com.example.vpn.DefaultProfiles.forEach { profile ->
              val isSelected = vpnStatus.selectedProfile.id == profile.id
              FilterChip(
                selected = isSelected,
                onClick = { onSelectVpnProfile(profile) },
                label = {
                  Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(profile.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${profile.host}:${profile.port} • ${profile.region}", fontSize = 10.sp)
                  }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = PastelMintContainer,
                  selectedLabelColor = PastelMintPrimary
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Protocol Selection
          Text("Protokol Enkripsi Tunnel:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            com.example.vpn.VpnProtocol.values().forEach { proto ->
              FilterChip(
                selected = vpnStatus.protocol == proto,
                onClick = { onSelectVpnProtocol(proto) },
                label = { Text(proto.name.replace("_", " "), fontSize = 10.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = PastelMintContainer,
                  selectedLabelColor = PastelMintPrimary
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          val isConnected = vpnStatus.state == com.example.vpn.VpnConnectionState.CONNECTED
          val isConnecting = vpnStatus.state == com.example.vpn.VpnConnectionState.CONNECTING

          if (isConnected) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                "IP: ${vpnStatus.virtualIp} • Latensi: ${vpnStatus.latencyMs} ms",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = com.example.ui.theme.RiskSafeText
              )
              OutlinedButton(
                onClick = onTestVpnPing,
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("Uji Ping", fontSize = 10.sp)
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (!isConnected) {
              Button(
                onClick = onConnectVpn,
                enabled = !isConnecting,
                colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("settings_connect_vpn_btn")
              ) {
                Text(if (isConnecting) "Menyambungkan VPN..." else "Hubungkan VPN ke Database", fontSize = 12.sp)
              }
            } else {
              OutlinedButton(
                onClick = onDisconnectVpn,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("settings_disconnect_vpn_btn")
              ) {
                Text("Putuskan", color = PastelRose, fontSize = 12.sp)
              }
              Button(
                onClick = onSyncVpnDatabase,
                colors = ButtonDefaults.buttonColors(containerColor = PastelMintPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("settings_sync_vpn_db_btn")
              ) {
                Text("Kueri Sinkron", fontSize = 12.sp)
              }
            }
          }
        }
      }
    }

    // Section 3: Notifikasi Push & Pengingat Waktu
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = PastelMintPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Notifikasi & Pengingat Terjadwal", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }

          Spacer(modifier = Modifier.height(14.dp))

          // High risk alert toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Peringatan Fraud Real-Time", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              Text("Notifikasi popup seketika jika skor >= 2", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
              checked = notifyHighRisk,
              onCheckedChange = {
                requestNotificationPermissionIfNeeded()
                onToggleNotifyHighRisk(it)
              },
              colors = SwitchDefaults.colors(checkedThumbColor = PastelMintPrimary, checkedTrackColor = PastelMintContainer)
            )
          }

          OutlinedButton(
            onClick = {
              requestNotificationPermissionIfNeeded()
              onTestHighRiskNotification()
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("test_fraud_notification_btn")
          ) {
            Text("🔔 Uji Notifikasi Risiko Tinggi", fontSize = 11.sp)
          }

          Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

          // Daily Reminder toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Pengingat Audit Tutup Buku Harian", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
              Text("Setiap pukul $reminderHour:00 WIB", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
              checked = notifyDailyReminder,
              onCheckedChange = {
                requestNotificationPermissionIfNeeded()
                onToggleNotifyDailyReminder(it)
              },
              colors = SwitchDefaults.colors(checkedThumbColor = PastelMintPrimary, checkedTrackColor = PastelMintContainer)
            )
          }

          OutlinedButton(
            onClick = {
              requestNotificationPermissionIfNeeded()
              onTestReminderNotification()
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("test_reminder_notification_btn")
          ) {
            Text("⏰ Uji Notifikasi Jadwal Tutup Buku", fontSize = 11.sp)
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedButton(
            onClick = onScheduleCalendarClick,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("settings_calendar_sync_btn")
          ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sinkronkan Jadwal ke Google Calendar", fontSize = 11.sp)
          }
        }
      }
    }

    // Section 4: Bahasa
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Language, contentDescription = null, tint = PastelMintPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Bahasa (Language)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = language == AppLanguage.ID,
              onClick = { onLanguageChange(AppLanguage.ID) },
              label = { Text("🇮🇩 Bahasa Indonesia", fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PastelMintContainer,
                selectedLabelColor = PastelMintPrimary
              )
            )

            FilterChip(
              selected = language == AppLanguage.EN,
              onClick = { onLanguageChange(AppLanguage.EN) },
              label = { Text("🇺🇸 English (US)", fontSize = 11.sp) },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PastelMintContainer,
                selectedLabelColor = PastelMintPrimary
              )
            )
          }
        }
      }
    }

    // Section 5: Data & Reset
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Kelola Data & Audit", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Kembalikan dataset ke data pengujian forensik Python mock awal (RRN_001 s/d RRN_008).",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedButton(
            onClick = {
              onResetDemoData()
              Toast.makeText(context, "Data berhasil direset ke dataset forensik awal", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("reset_demo_data_btn")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Reset ke Data Demo Awal", fontSize = 11.sp)
          }
        }
      }
    }
  }
}
