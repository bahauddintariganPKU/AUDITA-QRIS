package com.example.util

enum class AppLanguage {
  ID, EN;

  val displayName: String
    get() = when (this) {
      ID -> "Bahasa Indonesia"
      EN -> "English (US)"
    }
}

object Strings {
  fun appTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Audita Forensik QRIS"
    AppLanguage.EN -> "Audita QRIS Forensics"
  }

  fun subtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Sistem Deteksi Anomali & Rekonsiliasi Kasir"
    AppLanguage.EN -> "Anomaly Detection & Cashier Reconciliation"
  }

  fun tabDashboard(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Dasbor"
    AppLanguage.EN -> "Dashboard"
  }

  fun tabTransactions(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Transaksi"
    AppLanguage.EN -> "Transactions"
  }

  fun tabAnalytics(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Analitik"
    AppLanguage.EN -> "Analytics"
  }

  fun tabSettings(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Pengaturan"
    AppLanguage.EN -> "Settings"
  }

  fun totalTransactions(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Total Transaksi"
    AppLanguage.EN -> "Total Transactions"
  }

  fun totalAmount(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Total Nilai QRIS"
    AppLanguage.EN -> "Total QRIS Volume"
  }

  fun highRisk(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Risiko Tinggi"
    AppLanguage.EN -> "High Risk"
  }

  fun lowRisk(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Risiko Rendah"
    AppLanguage.EN -> "Low Risk"
  }

  fun safeValid(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Aman / Valid"
    AppLanguage.EN -> "Safe / Valid"
  }

  fun syncStatus(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Status Sinkronisasi"
    AppLanguage.EN -> "Sync Status"
  }

  fun offlineModeBadge(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Mode Offline Aktif (Penyimpanan Lokal Room)"
    AppLanguage.EN -> "Offline Mode Active (Room Local Storage)"
  }

  fun cloudSynced(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Tersinkronisasi ke Awan"
    AppLanguage.EN -> "Synced to Cloud"
  }

  fun searchPlaceholder(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Cari RRN, Kasir, Konsumen, atau Nominal..."
    AppLanguage.EN -> "Search RRN, Cashier, Customer, Amount..."
  }

  fun exportCsv(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Ekspor CSV"
    AppLanguage.EN -> "Export CSV"
  }

  fun exportReport(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Ringkasan Laporan"
    AppLanguage.EN -> "Summary Report"
  }

  fun syncNow(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Sinkron Sekarang"
    AppLanguage.EN -> "Sync Now"
  }

  fun scheduleCalendar(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Jadwalkan Kalender"
    AppLanguage.EN -> "Schedule in Calendar"
  }

  fun benfordChartTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Analisis Distribusi Hukum Benford"
    AppLanguage.EN -> "Benford's Law Digit Distribution"
  }

  fun benfordChartSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.ID -> "Perbandingan frekuensi digit awal (1-9) aktual vs kurva teoritis alami."
    AppLanguage.EN -> "Actual first-digit distribution vs theoretical natural curve."
  }
}
