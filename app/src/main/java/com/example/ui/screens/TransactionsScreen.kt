package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransactionItem
import com.example.ui.FilterCategory
import com.example.ui.components.FraudFlagChip
import com.example.ui.components.RiskBadge
import com.example.ui.components.SmartSearchBar
import com.example.ui.theme.PastelMintPrimary
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun TransactionsScreen(
  transactions: List<TransactionItem>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  selectedFilter: FilterCategory,
  onFilterSelect: (FilterCategory) -> Unit,
  language: AppLanguage,
  onTransactionClick: (TransactionItem) -> Unit,
  onAddNewClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isIndonesian = language == AppLanguage.ID

  Scaffold(
    modifier = modifier.testTag("transactions_screen"),
    floatingActionButton = {
      FloatingActionButton(
        onClick = onAddNewClick,
        containerColor = PastelMintPrimary,
        contentColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("fab_add_transaction")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Add Transaction")
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = Strings.tabTransactions(language),
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Audit forensik real-time untuk seluruh riwayat transaksi QRIS",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Smart Search & Filters
      SmartSearchBar(
        query = searchQuery,
        onQueryChange = onSearchQueryChange,
        selectedFilter = selectedFilter,
        onFilterSelect = onFilterSelect,
        placeholderText = Strings.searchPlaceholder(language)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Transaction items list
      if (transactions.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
          contentAlignment = Alignment.TopCenter
        ) {
          Text(
            text = "Tidak ada transaksi yang cocok dengan kriteria pencarian.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(bottom = 80.dp, top = 4.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(transactions, key = { it.rrn }) { item ->
            TransactionRowItem(
              item = item,
              onClick = { onTransactionClick(item) },
              isIndonesian = isIndonesian
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TransactionRowItem(
  item: TransactionItem,
  onClick: () -> Unit,
  isIndonesian: Boolean
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("transaction_row_${item.rrn}")
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
            fontSize = 15.sp
          )
          Spacer(modifier = Modifier.width(8.dp))
          RiskBadge(riskLevel = item.riskLevel)
        }
        Text(
          text = "Rp %,d".format(item.amount),
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Kasir: ${item.cashierId} • Konsumen: ${item.customerId}",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (item.isSynced) Icons.Default.CloudDone else Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = item.timeFormatted.substringAfter(" "),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
          )
        }
      }

      if (item.activeFlags.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          item.activeFlags.forEach { flag ->
            FraudFlagChip(flag = flag, isIndonesian = isIndonesian)
          }
        }
      }
    }
  }
}
