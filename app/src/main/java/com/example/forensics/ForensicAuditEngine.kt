package com.example.forensics

import com.example.model.RiskLevel
import com.example.model.TransactionItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ForensicAuditEngine {

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

  /**
   * Run forensic audit on a batch of transactions.
   * Accurately reproduces the digital forensic rules specified in the audit script.
   */
  fun auditBatch(items: List<TransactionItem>): List<TransactionItem> {
    if (items.isEmpty()) return emptyList()

    // Sort by Customer ID and then timestamp for split transaction detection
    val sortedByCustomerAndTime = items.sortedWith(
      compareBy<TransactionItem> { it.customerId }
        .thenBy { it.timestamp }
    )

    // Identify split transactions: consecutive tx by same customer with same amount within 5 minutes
    val splitTxIds = mutableSetOf<Long>()
    var previousItem: TransactionItem? = null

    for (current in sortedByCustomerAndTime) {
      if (previousItem != null && previousItem.customerId == current.customerId) {
        val diffMinutes = Math.abs(current.timestamp - previousItem.timestamp) / (1000 * 60)
        if (diffMinutes <= 5 && current.amount == previousItem.amount) {
          splitTxIds.add(current.id)
          splitTxIds.add(previousItem.id)
        }
      }
      previousItem = current
    }

    return items.map { item ->
      val cal = Calendar.getInstance().apply { timeInMillis = item.timestamp }
      val hour = cal.get(Calendar.HOUR_OF_DAY)

      // Formula 1: Jam Toko Tutup (23:00 - 05:00)
      val flagJamTutup = hour >= 23 || hour <= 5

      // Formula 2: Split Transaction (< 5 mins repeat with same amount)
      val flagSplitTx = splitTxIds.contains(item.id)

      // Formula 3: Nominal Bulat Besar (>= 1.000.000 & kelipatan 100.000)
      val flagNominalBulat = (item.amount >= 1_000_000L) && (item.amount % 100_000L == 0L)

      // Formula 4: Kolusi / Self-Transaction (ID Kasir == User ID Konsumen)
      val flagSelfTx = item.cashierId.equals(item.customerId, ignoreCase = true)

      // Formula 5: Benford's Law Anomali (nominal > 500.000 dan digit pertama 7, 8, atau 9)
      val firstDigit = getFirstDigit(item.amount)
      val flagBenford = (item.amount > 500_000L) && (firstDigit in listOf(7, 8, 9))

      val totalScore = (if (flagJamTutup) 1 else 0) +
          (if (flagSplitTx) 1 else 0) +
          (if (flagNominalBulat) 1 else 0) +
          (if (flagSelfTx) 1 else 0) +
          (if (flagBenford) 1 else 0)

      val risk = when {
        totalScore >= 2 -> RiskLevel.HIGH_RISK
        totalScore == 1 -> RiskLevel.LOW_RISK
        else -> RiskLevel.SAFE
      }

      val conclusion = when (risk) {
        RiskLevel.HIGH_RISK -> "RISIKO TINGGI (Investigasi Segera)"
        RiskLevel.LOW_RISK -> "Risiko Rendah (Pantau)"
        RiskLevel.SAFE -> "Aman / Valid"
      }

      item.copy(
        flagJamTutup = flagJamTutup,
        flagSplitTx = flagSplitTx,
        flagNominalBulat = flagNominalBulat,
        flagSelfTx = flagSelfTx,
        flagBenfordAnomali = flagBenford,
        totalFraudScore = totalScore,
        riskLevel = risk,
        conclusionText = conclusion
      )
    }
  }

  fun getFirstDigit(amount: Long): Int {
    if (amount <= 0) return 0
    val s = amount.toString()
    return Character.getNumericValue(s[0])
  }

  /**
   * Theoretical Benford's Law distribution P(d) = log10(1 + 1/d) for d in 1..9
   */
  fun getExpectedBenfordFrequencies(): Map<Int, Float> {
    return (1..9).associateWith { d ->
      (Math.log10(1.0 + 1.0 / d) * 100).toFloat()
    }
  }

  /**
   * Actual digit distribution from current transactions
   */
  fun calculateActualDigitFrequencies(items: List<TransactionItem>): Map<Int, Float> {
    if (items.isEmpty()) return (1..9).associateWith { 0f }
    val counts = mutableMapOf<Int, Int>()
    (1..9).forEach { counts[it] = 0 }

    var validCount = 0
    for (item in items) {
      val d = getFirstDigit(item.amount)
      if (d in 1..9) {
        counts[d] = (counts[d] ?: 0) + 1
        validCount++
      }
    }

    if (validCount == 0) return (1..9).associateWith { 0f }

    return counts.mapValues { (_, count) ->
      (count.toFloat() / validCount) * 100f
    }
  }
}
