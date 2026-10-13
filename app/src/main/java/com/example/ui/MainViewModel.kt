package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.SampleData
import com.example.data.TransactionRepository
import com.example.data.sync.CloudSyncManager
import com.example.data.sync.SyncStatus
import com.example.forensics.ForensicAuditEngine
import com.example.model.FraudIndicator
import com.example.model.RiskLevel
import com.example.model.TransactionItem
import com.example.ui.theme.AppThemeMode
import com.example.util.AppLanguage
import com.example.util.CalendarHelper
import com.example.util.ExportHelper
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
  DASHBOARD,
  TRANSACTIONS,
  ANALYTICS,
  SETTINGS
}

enum class FilterCategory {
  ALL,
  HIGH_RISK,
  LOW_RISK,
  SAFE,
  SPLIT_TX,
  GESTUN,
  SELF_TX,
  JAM_TUTUP
}

class MainViewModel(
  private val repository: TransactionRepository
) : ViewModel() {

  private val syncManager = CloudSyncManager(repository, viewModelScope)
  val syncStatus: StateFlow<SyncStatus> = syncManager.status

  private val vpnManager = com.example.vpn.VpnDatabaseManager(repository, viewModelScope)
  val vpnStatus: StateFlow<com.example.vpn.VpnStatus> = vpnManager.status

  private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedFilter = MutableStateFlow(FilterCategory.ALL)
  val selectedFilter: StateFlow<FilterCategory> = _selectedFilter.asStateFlow()

  private val _selectedTransaction = MutableStateFlow<TransactionItem?>(null)
  val selectedTransaction: StateFlow<TransactionItem?> = _selectedTransaction.asStateFlow()

  private val _showAddDialog = MutableStateFlow(false)
  val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

  private val _appLanguage = MutableStateFlow(AppLanguage.ID)
  val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

  private val _appThemeMode = MutableStateFlow(AppThemeMode.AUTO)
  val appThemeMode: StateFlow<AppThemeMode> = _appThemeMode.asStateFlow()

  // Notification settings
  private val _notifyHighRisk = MutableStateFlow(true)
  val notifyHighRisk: StateFlow<Boolean> = _notifyHighRisk.asStateFlow()

  private val _notifyDailyReminder = MutableStateFlow(true)
  val notifyDailyReminder: StateFlow<Boolean> = _notifyDailyReminder.asStateFlow()

  private val _reminderHour = MutableStateFlow(21)
  val reminderHour: StateFlow<Int> = _reminderHour.asStateFlow()

  val rawTransactions: StateFlow<List<TransactionItem>> = repository.allTransactions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val filteredTransactions: StateFlow<List<TransactionItem>> = combine(
    rawTransactions,
    _searchQuery,
    _selectedFilter
  ) { list, query, filter ->
    val cleanQuery = query.trim().lowercase()

    list.filter { item ->
      // Query filter
      val matchesQuery = cleanQuery.isEmpty() ||
          item.rrn.lowercase().contains(cleanQuery) ||
          item.cashierId.lowercase().contains(cleanQuery) ||
          item.customerId.lowercase().contains(cleanQuery) ||
          item.amount.toString().contains(cleanQuery) ||
          item.conclusionText.lowercase().contains(cleanQuery)

      // Category filter
      val matchesFilter = when (filter) {
        FilterCategory.ALL -> true
        FilterCategory.HIGH_RISK -> item.riskLevel == RiskLevel.HIGH_RISK
        FilterCategory.LOW_RISK -> item.riskLevel == RiskLevel.LOW_RISK
        FilterCategory.SAFE -> item.riskLevel == RiskLevel.SAFE
        FilterCategory.SPLIT_TX -> item.flagSplitTx
        FilterCategory.GESTUN -> item.flagNominalBulat
        FilterCategory.SELF_TX -> item.flagSelfTx
        FilterCategory.JAM_TUTUP -> item.flagJamTutup
      }

      matchesQuery && matchesFilter
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    viewModelScope.launch {
      repository.ensureInitialized()
    }
  }

  fun setScreen(screen: Screen) {
    _currentScreen.value = screen
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setFilter(filter: FilterCategory) {
    _selectedFilter.value = filter
  }

  fun selectTransaction(item: TransactionItem?) {
    _selectedTransaction.value = item
  }

  fun setShowAddDialog(show: Boolean) {
    _showAddDialog.value = show
  }

  fun setLanguage(lang: AppLanguage) {
    _appLanguage.value = lang
  }

  fun setThemeMode(mode: AppThemeMode) {
    _appThemeMode.value = mode
  }

  fun toggleOfflineMode(isOffline: Boolean) {
    syncManager.toggleOfflineMode(isOffline)
  }

  fun toggleAutoSync(enabled: Boolean) {
    syncManager.setAutoSync(enabled)
  }

  fun triggerSync() {
    syncManager.triggerSync()
  }

  fun setNotifyHighRisk(enabled: Boolean) {
    _notifyHighRisk.value = enabled
  }

  fun setNotifyDailyReminder(enabled: Boolean) {
    _notifyDailyReminder.value = enabled
  }

  fun setReminderHour(hour: Int) {
    _reminderHour.value = hour
  }

  fun addNewTransaction(
    context: Context,
    cashierId: String,
    customerId: String,
    amount: Long,
    customHour: Int? = null
  ) {
    viewModelScope.launch {
      val now = System.currentTimeMillis()
      val cal = java.util.Calendar.getInstance()
      if (customHour != null) {
        cal.set(java.util.Calendar.HOUR_OF_DAY, customHour)
      }
      val timestamp = cal.timeInMillis

      val count = rawTransactions.value.size
      val rrn = String.format(java.util.Locale.US, "RRN_%03d", count + 1)
      val formatted = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(timestamp)

      val newItem = TransactionItem(
        id = 0,
        rrn = rrn,
        timestamp = timestamp,
        timeFormatted = formatted,
        cashierId = cashierId.ifBlank { "KASIR_01" },
        customerId = customerId.ifBlank { "CUST_99" },
        amount = amount,
        merchantName = "Toko Berkah Utama",
        isSynced = !syncStatus.value.isOfflineMode
      )

      val audited = repository.addTransaction(newItem)

      // If high risk and notification enabled, dispatch push alert!
      if (audited.riskLevel == RiskLevel.HIGH_RISK && _notifyHighRisk.value) {
        NotificationHelper.notifyHighRiskDetected(context, audited)
      }

      if (!syncStatus.value.isOfflineMode && syncStatus.value.isAutoSyncEnabled) {
        syncManager.triggerSync()
      }
    }
  }

  fun addQuickSyntheticTransaction(context: Context) {
    viewModelScope.launch {
      val synthetic = SampleData.generateSyntheticTransaction(rawTransactions.value.size)
      val audited = repository.addTransaction(synthetic)
      if (audited.riskLevel == RiskLevel.HIGH_RISK && _notifyHighRisk.value) {
        NotificationHelper.notifyHighRiskDetected(context, audited)
      }
    }
  }

  fun resetDemoData() {
    viewModelScope.launch {
      repository.resetToDefaultSeed()
      syncManager.triggerSync()
    }
  }

  fun deleteTransaction(item: TransactionItem) {
    viewModelScope.launch {
      repository.deleteTransaction(item)
      _selectedTransaction.value = null
    }
  }

  fun exportCsv(context: Context) {
    ExportHelper.shareCsvFile(context, rawTransactions.value)
  }

  fun exportSummaryReport(context: Context) {
    ExportHelper.sharePdfSummaryReport(
      context,
      rawTransactions.value,
      _appLanguage.value == AppLanguage.ID
    )
  }

  fun scheduleCalendar(context: Context) {
    CalendarHelper.scheduleAuditCalendarEvent(
      context = context,
      hourOfDay = _reminderHour.value
    )
  }

  fun testHighRiskNotification(context: Context) {
    val sampleHighRisk = rawTransactions.value.find { it.riskLevel == RiskLevel.HIGH_RISK }
      ?: TransactionItem(
        rrn = "RRN_TEST",
        timestamp = System.currentTimeMillis(),
        timeFormatted = "2026-10-13 23:45:00",
        cashierId = "KASIR_02",
        customerId = "KASIR_02",
        amount = 5000000,
        flagJamTutup = true,
        flagNominalBulat = true,
        flagSelfTx = true,
        totalFraudScore = 3,
        conclusionText = "RISIKO TINGGI (Investigasi Segera)"
      )
    NotificationHelper.notifyHighRiskDetected(context, sampleHighRisk)
  }

  fun testReminderNotification(context: Context) {
    NotificationHelper.notifyAuditReminder(
      context,
      title = "⏰ Jadwal Audit Forensik QRIS Harian",
      message = "Waktu tutup buku toko telah tiba. Silakan tinjau ringkasan anomali transaksi hari ini di Audita."
    )
  }

  // VPN Database Server Methods
  fun connectVpn(onResult: ((Boolean) -> Unit)? = null) = vpnManager.connect(onResult)
  fun disconnectVpn() = vpnManager.disconnect()
  fun setVpnProfile(profile: com.example.vpn.DatabaseServerProfile) = vpnManager.setProfile(profile)
  fun setVpnProtocol(protocol: com.example.vpn.VpnProtocol) = vpnManager.setProtocol(protocol)
  fun testVpnPing(onDone: (Int) -> Unit) = vpnManager.testDatabasePing(onDone)
  fun syncVpnDatabase(onProgress: (String) -> Unit, onComplete: (Int) -> Unit) =
    vpnManager.syncWithDatabaseServer(onProgress, onComplete)

  companion object {
    fun provideFactory(repository: TransactionRepository): ViewModelProvider.Factory {
      return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return MainViewModel(repository) as T
        }
      }
    }
  }
}
