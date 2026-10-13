package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FilterCategory
import com.example.ui.theme.PastelMintContainer
import com.example.ui.theme.PastelMintPrimary

@Composable
fun SmartSearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
  selectedFilter: FilterCategory,
  onFilterSelect: (FilterCategory) -> Unit,
  placeholderText: String = "Cari RRN, Kasir, atau Nominal...",
  modifier: Modifier = Modifier
) {
  Row(modifier = modifier.fillMaxWidth()) {
    OutlinedTextField(
      value = query,
      onValueChange = onQueryChange,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("search_text_field"),
      placeholder = {
        Text(text = placeholderText, fontSize = 13.sp)
      },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp)
        )
      },
      trailingIcon = {
        if (query.isNotEmpty()) {
          IconButton(
            onClick = { onQueryChange("") },
            modifier = Modifier.testTag("clear_search_button")
          ) {
            Icon(
              imageVector = Icons.Default.Clear,
              contentDescription = "Clear",
              modifier = Modifier.size(18.dp)
            )
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(16.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PastelMintPrimary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface
      )
    )
  }

  // Horizontal scrollable filter chips
  val scrollState = rememberScrollState()
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
      .horizontalScroll(scrollState),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    FilterChipItem(
      label = "Semua",
      selected = selectedFilter == FilterCategory.ALL,
      onClick = { onFilterSelect(FilterCategory.ALL) },
      tag = "filter_chip_all"
    )
    FilterChipItem(
      label = "🚨 Risiko Tinggi",
      selected = selectedFilter == FilterCategory.HIGH_RISK,
      onClick = { onFilterSelect(FilterCategory.HIGH_RISK) },
      tag = "filter_chip_high_risk"
    )
    FilterChipItem(
      label = "⚠️ Pantau",
      selected = selectedFilter == FilterCategory.LOW_RISK,
      onClick = { onFilterSelect(FilterCategory.LOW_RISK) },
      tag = "filter_chip_low_risk"
    )
    FilterChipItem(
      label = "✅ Aman",
      selected = selectedFilter == FilterCategory.SAFE,
      onClick = { onFilterSelect(FilterCategory.SAFE) },
      tag = "filter_chip_safe"
    )
    FilterChipItem(
      label = "✂️ Split Tx",
      selected = selectedFilter == FilterCategory.SPLIT_TX,
      onClick = { onFilterSelect(FilterCategory.SPLIT_TX) },
      tag = "filter_chip_split"
    )
    FilterChipItem(
      label = "💳 Gestun",
      selected = selectedFilter == FilterCategory.GESTUN,
      onClick = { onFilterSelect(FilterCategory.GESTUN) },
      tag = "filter_chip_gestun"
    )
    FilterChipItem(
      label = "👤 Kasir Sendiri",
      selected = selectedFilter == FilterCategory.SELF_TX,
      onClick = { onFilterSelect(FilterCategory.SELF_TX) },
      tag = "filter_chip_self"
    )
    FilterChipItem(
      label = "🌙 Jam Tutup",
      selected = selectedFilter == FilterCategory.JAM_TUTUP,
      onClick = { onFilterSelect(FilterCategory.JAM_TUTUP) },
      tag = "filter_chip_closed"
    )
  }
}

@Composable
private fun FilterChipItem(
  label: String,
  selected: Boolean,
  onClick: () -> Unit,
  tag: String
) {
  FilterChip(
    selected = selected,
    onClick = onClick,
    label = {
      Text(
        text = label,
        fontSize = 11.sp,
        style = MaterialTheme.typography.labelSmall
      )
    },
    shape = RoundedCornerShape(12.dp),
    colors = FilterChipDefaults.filterChipColors(
      selectedContainerColor = PastelMintContainer,
      selectedLabelColor = PastelMintPrimary,
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    border = null,
    modifier = Modifier.testTag(tag)
  )
}
