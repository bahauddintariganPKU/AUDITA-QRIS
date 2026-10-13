package com.example.vpn

import android.content.Context
import com.example.data.TransactionRepository
import com.example.forensics.ForensicAuditEngine
import com.example.model.TransactionItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class VpnConnectionState {
  DISCONNECTED,
  CONNECTING,
  CONNECTED,
  FAILED
}

enum class VpnProtocol(val displayName: String, val cipher: String) {
  WIREGUARD("WireGuard Kernel", "ChaCha20-Poly1305 (256-bit)"),
  IPSEC_IKEV2("IPsec IKEv2 Banking", "AES-256-GCM / SHA-384"),
  SSL_TLS("OpenVPN SSL/TLS Tunnel", "AES-256-CBC / HMAC-SHA256")
}

data class DatabaseServerProfile(
  val id: String,
  val name: String,
  val host: String,
  val port: Int = 5432,
  val databaseName: String = "qris_forensic_db",
  val region: String = "Jakarta IDC-1",
  val assignedSubnet: String = "10.88.1.0/24"
)

data class VpnStatus(
  val state: VpnConnectionState = VpnConnectionState.DISCONNECTED,
  val selectedProfile: DatabaseServerProfile = DefaultProfiles.first(),
  val protocol: VpnProtocol = VpnProtocol.WIREGUARD,
  val virtualIp: String = "10.88.1.42",
  val latencyMs: Int = 22,
  val bytesSent: Long = 0,
  val bytesReceived: Long = 0,
  val connectedTimeSeconds: Long = 0,
  val isDatabaseReachable: Boolean = false,
  val lastSyncTimestamp: String = "Belum sinkron",
  val connectionMessage: String = "VPN Server Database Terputus"
)

val DefaultProfiles = listOf(
  DatabaseServerProfile(
    id = "jkt_prod",
    name = "Jakarta Central DB Server (Primary)",
    host = "db-prod.jkt.qris-audit.internal",
    port = 5432,
    databaseName = "qris_forensic_prod",
    region = "Jakarta (Tier-4 Datacenter)",
    assignedSubnet = "10.88.1.42/24"
  ),
  DatabaseServerProfile(
    id = "sg_dr",
    name = "Singapore Cloud DB Cluster (Replica)",
    host = "db-dr.sg.qris-backup.internal",
    port = 5432,
    databaseName = "qris_forensic_dr",
    region = "Singapore (AWS Cloud Cluster)",
    assignedSubnet = "10.99.4.15/24"
  ),
  DatabaseServerProfile(
    id = "local_intranet",
    name = "Merchant Local Gateway Intranet",
    host = "192.168.100.250",
    port = 5432,
    databaseName = "pos_qris_local",
    region = "Local Store Gateway Router",
    assignedSubnet = "192.168.100.42/24"
  )
)

class VpnDatabaseManager(
  private val repository: TransactionRepository,
  private val scope: CoroutineScope
) {

  private val _status = MutableStateFlow(
    VpnStatus(selectedProfile = DefaultProfiles.first())
  )
  val status: StateFlow<VpnStatus> = _status.asStateFlow()

  private var telemetryJob: Job? = null

  fun setProfile(profile: DatabaseServerProfile) {
    if (_status.value.state != VpnConnectionState.CONNECTED) {
      _status.value = _status.value.copy(
        selectedProfile = profile,
        virtualIp = profile.assignedSubnet.substringBefore("/")
      )
    }
  }

  fun setProtocol(protocol: VpnProtocol) {
    if (_status.value.state != VpnConnectionState.CONNECTED) {
      _status.value = _status.value.copy(protocol = protocol)
    }
  }

  fun connect(onResult: ((Boolean) -> Unit)? = null) {
    if (_status.value.state == VpnConnectionState.CONNECTED ||
      _status.value.state == VpnConnectionState.CONNECTING
    ) return

    scope.launch(Dispatchers.IO) {
      _status.value = _status.value.copy(
        state = VpnConnectionState.CONNECTING,
        connectionMessage = "Melakukan Handshake Kunci Enkripsi (${_status.value.protocol.displayName})..."
      )

      // Step 1: Handshake
      delay(700)
      _status.value = _status.value.copy(
        connectionMessage = "Membuka Terowongan Tunnel ke ${_status.value.selectedProfile.host}..."
      )

      // Step 2: Virtual IP Assignment & DB Socket Handshake
      delay(800)
      val randomLatency = (16..35).random()
      _status.value = _status.value.copy(
        state = VpnConnectionState.CONNECTED,
        isDatabaseReachable = true,
        latencyMs = randomLatency,
        connectionMessage = "Terhubung Aman ke Database Server (${_status.value.selectedProfile.name})",
        bytesSent = 1240L,
        bytesReceived = 4850L
      )

      startTelemetryLoop()
      onResult?.let { launch(Dispatchers.Main) { it(true) } }
    }
  }

  fun disconnect() {
    telemetryJob?.cancel()
    telemetryJob = null
    _status.value = _status.value.copy(
      state = VpnConnectionState.DISCONNECTED,
      isDatabaseReachable = false,
      connectedTimeSeconds = 0,
      connectionMessage = "Koneksi VPN Server Database Dihentikan"
    )
  }

  fun testDatabasePing(onDone: (Int) -> Unit) {
    scope.launch(Dispatchers.IO) {
      delay(400)
      val newLatency = if (_status.value.state == VpnConnectionState.CONNECTED) {
        (14..32).random()
      } else {
        -1
      }
      _status.value = _status.value.copy(latencyMs = if (newLatency > 0) newLatency else 0)
      launch(Dispatchers.Main) { onDone(newLatency) }
    }
  }

  fun syncWithDatabaseServer(
    onProgress: (String) -> Unit,
    onComplete: (Int) -> Unit
  ) {
    if (_status.value.state != VpnConnectionState.CONNECTED) {
      onProgress("Gagal: VPN Database Server belum terhubung!")
      onComplete(0)
      return
    }

    scope.launch(Dispatchers.IO) {
      onProgress("Mengirim query SQL ke ${_status.value.selectedProfile.databaseName}...")
      delay(600)
      
      // Simulate pulling server updates and pushing local audit findings
      _status.value = _status.value.copy(
        bytesSent = _status.value.bytesSent + 8420L,
        bytesReceived = _status.value.bytesReceived + 14890L
      )

      onProgress("Memverifikasi integritas hash transaksi melalui enkripsi ${_status.value.protocol.cipher}...")
      delay(700)
      repository.markAllSynced()

      val nowTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
      _status.value = _status.value.copy(
        lastSyncTimestamp = "$nowTime via VPN (${_status.value.selectedProfile.name})"
      )

      onProgress("Sinkronisasi dua arah dengan server database berhasil!")
      delay(300)
      launch(Dispatchers.Main) { onComplete(1) }
    }
  }

  private fun startTelemetryLoop() {
    telemetryJob?.cancel()
    telemetryJob = scope.launch(Dispatchers.IO) {
      while (isActive && _status.value.state == VpnConnectionState.CONNECTED) {
        delay(1000)
        val current = _status.value
        val addTx = (80..350).random().toLong()
        val addRx = (120..780).random().toLong()
        _status.value = current.copy(
          connectedTimeSeconds = current.connectedTimeSeconds + 1,
          bytesSent = current.bytesSent + addTx,
          bytesReceived = current.bytesReceived + addRx
        )
      }
    }
  }
}
