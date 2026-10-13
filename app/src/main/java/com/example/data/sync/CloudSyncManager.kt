package com.example.data.sync

import com.example.data.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SyncState {
  IDLE,
  SYNCING,
  SYNCED,
  OFFLINE_MODE
}

data class SyncStatus(
  val state: SyncState = SyncState.SYNCED,
  val lastSyncTime: String = "Baru saja",
  val isOfflineMode: Boolean = false,
  val isAutoSyncEnabled: Boolean = true,
  val encryptedHash: String = "SHA256:7e2b...4f89",
  val pendingItemsCount: Int = 0
)

class CloudSyncManager(
  private val repository: TransactionRepository,
  private val scope: CoroutineScope
) {

  private val _status = MutableStateFlow(
    SyncStatus(
      lastSyncTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    )
  )
  val status: StateFlow<SyncStatus> = _status.asStateFlow()

  fun toggleOfflineMode(isOffline: Boolean) {
    _status.value = _status.value.copy(
      isOfflineMode = isOffline,
      state = if (isOffline) SyncState.OFFLINE_MODE else SyncState.SYNCED
    )
    if (!isOffline && _status.value.isAutoSyncEnabled) {
      triggerSync()
    }
  }

  fun setAutoSync(enabled: Boolean) {
    _status.value = _status.value.copy(isAutoSyncEnabled = enabled)
    if (enabled && !_status.value.isOfflineMode) {
      triggerSync()
    }
  }

  fun triggerSync(onComplete: (() -> Unit)? = null) {
    if (_status.value.isOfflineMode) {
      return
    }

    scope.launch(Dispatchers.IO) {
      _status.value = _status.value.copy(state = SyncState.SYNCING)
      // Realistic cloud sync & encryption handshake delay
      delay(900)
      repository.markAllSynced()

      val nowTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
      val hash = "AES256-GCM:" + (1000..9999).random() + "..." + (1000..9999).random()

      _status.value = _status.value.copy(
        state = SyncState.SYNCED,
        lastSyncTime = nowTime,
        encryptedHash = hash,
        pendingItemsCount = 0
      )
      onComplete?.let { launch(Dispatchers.Main) { it() } }
    }
  }
}
