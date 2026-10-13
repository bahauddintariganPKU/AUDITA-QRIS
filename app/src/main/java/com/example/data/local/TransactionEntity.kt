package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.RiskLevel
import com.example.model.TransactionItem

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val rrn: String,
  val timestamp: Long,
  val timeFormatted: String,
  val cashierId: String,
  val customerId: String,
  val amount: Long,
  val merchantName: String = "Merchant QRIS Utama",
  val flagJamTutup: Boolean = false,
  val flagSplitTx: Boolean = false,
  val flagNominalBulat: Boolean = false,
  val flagSelfTx: Boolean = false,
  val flagBenfordAnomali: Boolean = false,
  val totalFraudScore: Int = 0,
  val riskLevelString: String = "SAFE",
  val conclusionText: String = "",
  val isSynced: Boolean = true
) {
  fun toModel(): TransactionItem {
    val level = try {
      RiskLevel.valueOf(riskLevelString)
    } catch (_: Exception) {
      RiskLevel.SAFE
    }
    return TransactionItem(
      id = id,
      rrn = rrn,
      timestamp = timestamp,
      timeFormatted = timeFormatted,
      cashierId = cashierId,
      customerId = customerId,
      amount = amount,
      merchantName = merchantName,
      flagJamTutup = flagJamTutup,
      flagSplitTx = flagSplitTx,
      flagNominalBulat = flagNominalBulat,
      flagSelfTx = flagSelfTx,
      flagBenfordAnomali = flagBenfordAnomali,
      totalFraudScore = totalFraudScore,
      riskLevel = level,
      conclusionText = conclusionText,
      isSynced = isSynced
    )
  }

  companion object {
    fun fromModel(item: TransactionItem): TransactionEntity {
      return TransactionEntity(
        id = item.id,
        rrn = item.rrn,
        timestamp = item.timestamp,
        timeFormatted = item.timeFormatted,
        cashierId = item.cashierId,
        customerId = item.customerId,
        amount = item.amount,
        merchantName = item.merchantName,
        flagJamTutup = item.flagJamTutup,
        flagSplitTx = item.flagSplitTx,
        flagNominalBulat = item.flagNominalBulat,
        flagSelfTx = item.flagSelfTx,
        flagBenfordAnomali = item.flagBenfordAnomali,
        totalFraudScore = item.totalFraudScore,
        riskLevelString = item.riskLevel.name,
        conclusionText = item.conclusionText,
        isSynced = item.isSynced
      )
    }
  }
}
