package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.data.TransactionRepository
import com.example.data.local.AuditDatabase
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.AddTransactionDialog
import com.example.ui.components.AuditDetailDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PastelMintContainer
import com.example.ui.theme.PastelMintPrimary
import com.example.util.NotificationHelper
import com.example.util.Strings

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    NotificationHelper.initChannels(this)

    val database = AuditDatabase.getDatabase(this)
    val repository = TransactionRepository(database)
    val viewModel by viewModels<MainViewModel> {
      MainViewModel.provideFactory(repository)
    }

    setContent {
      val themeMode by viewModel.appThemeMode.collectAsState()
      val language by viewModel.appLanguage.collectAsState()
      val currentScreen by viewModel.currentScreen.collectAsState()
      val transactions by viewModel.rawTransactions.collectAsState()
      val filteredTransactions by viewModel.filteredTransactions.collectAsState()
      val searchQuery by viewModel.searchQuery.collectAsState()
      val selectedFilter by viewModel.selectedFilter.collectAsState()
      val syncStatus by viewModel.syncStatus.collectAsState()
      val vpnStatus by viewModel.vpnStatus.collectAsState()
      val selectedItem by viewModel.selectedTransaction.collectAsState()
      val showAddDialog by viewModel.showAddDialog.collectAsState()

      val notifyHighRisk by viewModel.notifyHighRisk.collectAsState()
      val notifyDailyReminder by viewModel.notifyDailyReminder.collectAsState()
      val reminderHour by viewModel.reminderHour.collectAsState()

      MyApplicationTheme(themeMode = themeMode) {
        // Back navigation handling
        BackHandler(enabled = currentScreen != Screen.DASHBOARD) {
          viewModel.setScreen(Screen.DASHBOARD)
        }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          contentWindowInsets = WindowInsets.safeDrawing,
          bottomBar = {
            NavigationBar(
              containerColor = MaterialTheme.colorScheme.surface,
              tonalElevation = 6.dp,
              modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .testTag("bottom_nav_bar")
            ) {
              val navItems = listOf(
                Triple(Screen.DASHBOARD, Strings.tabDashboard(language), Icons.Default.Dashboard to Icons.Outlined.Dashboard),
                Triple(Screen.TRANSACTIONS, Strings.tabTransactions(language), Icons.Default.ReceiptLong to Icons.Outlined.ReceiptLong),
                Triple(Screen.ANALYTICS, Strings.tabAnalytics(language), Icons.Default.Analytics to Icons.Outlined.Analytics),
                Triple(Screen.SETTINGS, Strings.tabSettings(language), Icons.Default.Settings to Icons.Outlined.Settings)
              )

              navItems.forEach { (screen, label, icons) ->
                val selected = currentScreen == screen
                NavigationBarItem(
                  selected = selected,
                  onClick = { viewModel.setScreen(screen) },
                  icon = {
                    Icon(
                      imageVector = if (selected) icons.first else icons.second,
                      contentDescription = label
                    )
                  },
                  label = {
                    Text(
                      text = label,
                      fontSize = 11.sp,
                      style = MaterialTheme.typography.labelSmall
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PastelMintPrimary,
                    selectedTextColor = PastelMintPrimary,
                    indicatorColor = PastelMintContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                  ),
                  modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                )
              }
            }
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            // Smooth Animated Transition between screens
            AnimatedContent(
              targetState = currentScreen,
              transitionSpec = {
                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
              },
              label = "ScreenTransition"
            ) { screen ->
              when (screen) {
                Screen.DASHBOARD -> DashboardScreen(
                  transactions = transactions,
                  syncStatus = syncStatus,
                  vpnStatus = vpnStatus,
                  language = language,
                  onTransactionClick = { viewModel.selectTransaction(it) },
                  onAddNewClick = { viewModel.setShowAddDialog(true) },
                  onSyncClick = { viewModel.triggerSync() },
                  onConnectVpnClick = { viewModel.connectVpn() },
                  onDisconnectVpnClick = { viewModel.disconnectVpn() },
                  onSyncVpnDbClick = {
                    viewModel.syncVpnDatabase(
                      onProgress = { msg -> Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show() },
                      onComplete = { Toast.makeText(this@MainActivity, "Kueri Sinkronisasi Database Berhasil", Toast.LENGTH_SHORT).show() }
                    )
                  },
                  onExportCsvClick = { viewModel.exportCsv(this@MainActivity) },
                  onScheduleCalendarClick = { viewModel.scheduleCalendar(this@MainActivity) },
                  onGenerateSyntheticClick = { viewModel.addQuickSyntheticTransaction(this@MainActivity) }
                )
                Screen.TRANSACTIONS -> TransactionsScreen(
                  transactions = filteredTransactions,
                  searchQuery = searchQuery,
                  onSearchQueryChange = { viewModel.setSearchQuery(it) },
                  selectedFilter = selectedFilter,
                  onFilterSelect = { viewModel.setFilter(it) },
                  language = language,
                  onTransactionClick = { viewModel.selectTransaction(it) },
                  onAddNewClick = { viewModel.setShowAddDialog(true) }
                )
                Screen.ANALYTICS -> AnalyticsScreen(
                  transactions = transactions,
                  language = language,
                  onExportCsvClick = { viewModel.exportCsv(this@MainActivity) },
                  onExportSummaryClick = { viewModel.exportSummaryReport(this@MainActivity) },
                  onScheduleCalendarClick = { viewModel.scheduleCalendar(this@MainActivity) }
                )
                Screen.SETTINGS -> SettingsScreen(
                  syncStatus = syncStatus,
                  vpnStatus = vpnStatus,
                  themeMode = themeMode,
                  language = language,
                  notifyHighRisk = notifyHighRisk,
                  notifyDailyReminder = notifyDailyReminder,
                  reminderHour = reminderHour,
                  onThemeModeChange = { viewModel.setThemeMode(it) },
                  onLanguageChange = { viewModel.setLanguage(it) },
                  onToggleOffline = { viewModel.toggleOfflineMode(it) },
                  onToggleAutoSync = { viewModel.toggleAutoSync(it) },
                  onTriggerSync = { viewModel.triggerSync() },
                  onConnectVpn = { viewModel.connectVpn() },
                  onDisconnectVpn = { viewModel.disconnectVpn() },
                  onSelectVpnProfile = { viewModel.setVpnProfile(it) },
                  onSelectVpnProtocol = { viewModel.setVpnProtocol(it) },
                  onTestVpnPing = {
                    viewModel.testVpnPing { latency ->
                      Toast.makeText(this@MainActivity, "Ping Server DB: $latency ms", Toast.LENGTH_SHORT).show()
                    }
                  },
                  onSyncVpnDatabase = {
                    viewModel.syncVpnDatabase(
                      onProgress = { msg -> Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show() },
                      onComplete = { Toast.makeText(this@MainActivity, "Database Pusat Berhasil Disinkronkan", Toast.LENGTH_SHORT).show() }
                    )
                  },
                  onToggleNotifyHighRisk = { viewModel.setNotifyHighRisk(it) },
                  onToggleNotifyDailyReminder = { viewModel.setNotifyDailyReminder(it) },
                  onReminderHourChange = { viewModel.setReminderHour(it) },
                  onTestHighRiskNotification = { viewModel.testHighRiskNotification(this@MainActivity) },
                  onTestReminderNotification = { viewModel.testReminderNotification(this@MainActivity) },
                  onScheduleCalendarClick = { viewModel.scheduleCalendar(this@MainActivity) },
                  onResetDemoData = { viewModel.resetDemoData() }
                )
              }
            }

            // Audit Investigation Drill-down Modal
            selectedItem?.let { item ->
              AuditDetailDialog(
                item = item,
                onDismiss = { viewModel.selectTransaction(null) },
                onDelete = { viewModel.deleteTransaction(it) },
                isIndonesian = language == com.example.util.AppLanguage.ID
              )
            }

            // Add / Test New Transaction Dialog
            if (showAddDialog) {
              AddTransactionDialog(
                onDismiss = { viewModel.setShowAddDialog(false) },
                onAdd = { cashier, customer, amt, hour ->
                  viewModel.addNewTransaction(
                    context = this@MainActivity,
                    cashierId = cashier,
                    customerId = customer,
                    amount = amt,
                    customHour = hour
                  )
                }
              )
            }
          }
        }
      }
    }
  }
}
