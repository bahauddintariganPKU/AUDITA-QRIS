package com.example.data

import com.example.forensics.ForensicAuditEngine
import com.example.model.TransactionItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SampleData {

  private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

  private fun parseDate(dateStr: String): Long {
    return try {
      sdf.parse(dateStr)?.time ?: System.currentTimeMillis()
    } catch (_: Exception) {
      System.currentTimeMillis()
    }
  }

  fun getInitialSeed(): List<TransactionItem> {
    val rawList = listOf(
      TransactionItem(
        id = 1,
        rrn = "RRN_001",
        timestamp = parseDate("2026-10-04 10:15:00"),
        timeFormatted = "2026-10-04 10:15:00",
        cashierId = "KASIR_01",
        customerId = "CUST_99",
        amount = 45500,
        merchantName = "Toko Berkah Utama"
      ),
      TransactionItem(
        id = 2,
        rrn = "RRN_002",
        timestamp = parseDate("2026-10-04 23:55:00"),
        timeFormatted = "2026-10-04 23:55:00",
        cashierId = "KASIR_02",
        customerId = "CUST_12",
        amount = 150000,
        merchantName = "Toko Berkah Utama"
      ),
      TransactionItem(
        id = 3,
        rrn = "RRN_003",
        timestamp = parseDate("2026-10-04 11:20:00"),
        timeFormatted = "2026-10-04 11:20:00",
        cashierId = "KASIR_01",
        customerId = "CUST_55",
        amount = 600000,
        merchantName = "Toko Berkah Utama"
      ),
      TransactionItem(
        id = 4,
        rrn = "RRN_004",
        timestamp = parseDate("2026-10-04 11:22:00"),
        timeFormatted = "2026-10-04 11:22:00",
        cashierId = "KASIR_01",
        customerId = "CUST_55",
        amount = 600000,
        merchantName = "Toko Berkah Utama"
      ),
      TransactionItem(
        id = 5,
        rrn = "RRN_005",
        timestamp = parseDate("2026-10-04 14:00:00"),
        timeFormatted = "2026-10-04 14:00:00",
        cashierId = "KASIR_03",
        customerId = "CUST_77",
        amount = 5000000,
        merchantName = "Toko Berkah Utama"
      ),
      TransactionItem(
        id = 6,
        rrn = "RRN_006",
        timestamp = parseDate("2026-10-04 15:30:00"),
        timeFormatted = "2026-10-04 15:30:00",
        cashierId = "KASIR_02",
        customerId = "KASIR_02",
        amount = 250000,
        merchantName = "Toko Berkah Utama"
      ),
      TransactionItem(
        id = 7,
        rrn = "RRN_007",
        timestamp = parseDate("2026-10-04 15:45:00"),
        timeFormatted = "2026-10-04 15:45:00",
        cashierId = "KASIR_01",
        customerId = "CUST_88",
        amount = 988770,
        merchantName = "Toko Berkah Utama"
      ),
      TransactionItem(
        id = 8,
        rrn = "RRN_008",
        timestamp = parseDate("2026-10-04 16:10:00"),
        timeFormatted = "2026-10-04 16:10:00",
        cashierId = "KASIR_03",
        customerId = "CUST_44",
        amount = 32500,
        merchantName = "Toko Berkah Utama"
      )
    )

    return ForensicAuditEngine.auditBatch(rawList)
  }

  fun generateSyntheticTransaction(currentCount: Int): TransactionItem {
    val nextIndex = currentCount + 1
    val rrn = String.format(Locale.US, "RRN_%03d", nextIndex)
    val now = System.currentTimeMillis()
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    val formatted = sdf.format(now)

    val cashiers = listOf("KASIR_01", "KASIR_02", "KASIR_03", "KASIR_04")
    val customers = listOf("CUST_10", "CUST_23", "CUST_55", "CUST_99", "CUST_77", "KASIR_01")
    val amounts = listOf(35000L, 85000L, 120000L, 450000L, 1200000L, 890000L, 2000000L, 25000L)

    val cashier = cashiers.random()
    val customer = customers.random()
    val amount = amounts.random()

    return TransactionItem(
      id = 0,
      rrn = rrn,
      timestamp = now,
      timeFormatted = formatted,
      cashierId = cashier,
      customerId = customer,
      amount = amount,
      merchantName = "Toko Berkah Utama",
      isSynced = false
    )
  }
}
