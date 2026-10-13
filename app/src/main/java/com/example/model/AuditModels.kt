package com.example.model

enum class RiskLevel {
  SAFE,
  LOW_RISK,
  HIGH_RISK;

  fun getTitle(isIndonesian: Boolean): String = when (this) {
    SAFE -> if (isIndonesian) "Aman / Valid" else "Safe / Valid"
    LOW_RISK -> if (isIndonesian) "Risiko Rendah (Pantau)" else "Low Risk (Monitor)"
    HIGH_RISK -> if (isIndonesian) "RISIKO TINGGI (Investigasi)" else "HIGH RISK (Investigate)"
  }
}

enum class FraudIndicator {
  JAM_TUTUP,
  SPLIT_TX,
  NOMINAL_BULAT,
  SELF_TX,
  BENFORD_ANOMALI;

  fun getLabel(isIndonesian: Boolean): String = when (this) {
    JAM_TUTUP -> if (isIndonesian) "Jam Toko Tutup" else "After Hours (Closed)"
    SPLIT_TX -> if (isIndonesian) "Transaksi Dipecah (Split)" else "Split Transaction"
    NOMINAL_BULAT -> if (isIndonesian) "Nominal Bulat Besar (Gestun)" else "Round Cashout (Gestun)"
    SELF_TX -> if (isIndonesian) "Transaksi Kasir Sendiri" else "Cashier Self-Transaction"
    BENFORD_ANOMALI -> if (isIndonesian) "Anomali Benford's Law" else "Benford's Law Anomaly"
  }

  fun getDescription(isIndonesian: Boolean): String = when (this) {
    JAM_TUTUP -> if (isIndonesian)
      "Transaksi terjadi pada pukul 23:00 - 05:00 saat outlet tutup operasional."
    else
      "Transaction occurred between 23:00 - 05:00 outside operating hours."
    SPLIT_TX -> if (isIndonesian)
      "Konsumen sama melakukan transaksi berulang dengan nominal identik dalam < 5 menit."
    else
      "Same customer repeated identical amount transaction within < 5 minutes."
    NOMINAL_BULAT -> if (isIndonesian)
      "Nominal >= Rp1.000.000 kelipatan Rp100.000 tanpa pecahan rupiah (indikasi gestun)."
    else
      "Amount >= Rp1,000,000 rounded to 100k without change (potential cashout)."
    SELF_TX -> if (isIndonesian)
      "ID Kasir sama dengan ID Konsumen (indikasi fraud poin atau insentif)."
    else
      "Cashier ID matches Customer ID (self-scan for incentives/points)."
    BENFORD_ANOMALI -> if (isIndonesian)
      "Nominal > Rp500.000 dengan angka depan 7, 8, atau 9 (indikasi angka rekayasa)."
    else
      "Amount > Rp500k starting with 7, 8, or 9 (fabricated number pattern)."
  }
}

data class TransactionItem(
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
  val riskLevel: RiskLevel = RiskLevel.SAFE,
  val conclusionText: String = "",
  val isSynced: Boolean = true
) {
  val activeFlags: List<FraudIndicator>
    get() {
      val list = mutableListOf<FraudIndicator>()
      if (flagJamTutup) list.add(FraudIndicator.JAM_TUTUP)
      if (flagSplitTx) list.add(FraudIndicator.SPLIT_TX)
      if (flagNominalBulat) list.add(FraudIndicator.NOMINAL_BULAT)
      if (flagSelfTx) list.add(FraudIndicator.SELF_TX)
      if (flagBenfordAnomali) list.add(FraudIndicator.BENFORD_ANOMALI)
      return list
    }
}
