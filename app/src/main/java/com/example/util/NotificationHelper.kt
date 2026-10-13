package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.model.TransactionItem

object NotificationHelper {

  private const val CHANNEL_FRAUD_ID = "audita_fraud_alerts"
  private const val CHANNEL_REMINDER_ID = "audita_reminders"

  fun initChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

      val fraudChannel = NotificationChannel(
        CHANNEL_FRAUD_ID,
        "Peringatan Fraud & Anomali QRIS",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Notifikasi saat transaksi berisiko tinggi atau kecurangan terdeteksi"
        enableVibration(true)
      }

      val reminderChannel = NotificationChannel(
        CHANNEL_REMINDER_ID,
        "Pengingat Jadwal Audit",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "Pengingat berkala untuk tutup buku dan audit forensik harian"
      }

      notificationManager.createNotificationChannel(fraudChannel)
      notificationManager.createNotificationChannel(reminderChannel)
    }
  }

  fun notifyHighRiskDetected(context: Context, item: TransactionItem) {
    initChannels(context)

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val flagNames = item.activeFlags.joinToString(", ") { it.name }

    val builder = NotificationCompat.Builder(context, CHANNEL_FRAUD_ID)
      .setSmallIcon(android.R.drawable.stat_notify_error)
      .setContentTitle("⚠️ Deteksi Risiko Tinggi: ${item.rrn}")
      .setContentText("Skor Fraud: ${item.totalFraudScore} • Rp %,d • ${item.conclusionText}".format(item.amount))
      .setStyle(
        NotificationCompat.BigTextStyle().bigText(
          "Peringatan Forensik:\nTransaksi ${item.rrn} oleh ${item.cashierId} (Konsumen: ${item.customerId}) menunjukkan indikator anomali:\n[$flagNames]\nSegera lakukan investigasi kasir dan verifikasi bukti struk QRIS."
        )
      )
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)

    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
  }

  fun notifyAuditReminder(context: Context, title: String, message: String) {
    initChannels(context)

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
      context,
      1,
      intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_REMINDER_ID)
      .setSmallIcon(android.R.drawable.ic_popup_reminder)
      .setContentTitle(title)
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)

    val notificationManager =
      context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(999, builder.build())
  }
}
