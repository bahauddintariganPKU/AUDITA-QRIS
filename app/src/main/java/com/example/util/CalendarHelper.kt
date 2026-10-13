package com.example.util

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import java.util.Calendar

object CalendarHelper {

  fun scheduleAuditCalendarEvent(
    context: Context,
    title: String = "Audit Forensik QRIS & Rekonsiliasi Kasir",
    description: String = "Jadwal evaluasi transaksi anomali, deteksi fraud split tx, dan rekonsiliasi laporan QRIS di Audita.",
    hourOfDay: Int = 21,
    minute: Int = 0
  ) {
    val cal = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, hourOfDay)
      set(Calendar.MINUTE, minute)
      set(Calendar.SECOND, 0)
      if (before(Calendar.getInstance())) {
        add(Calendar.DAY_OF_YEAR, 1)
      }
    }

    val intent = Intent(Intent.ACTION_INSERT).apply {
      data = CalendarContract.Events.CONTENT_URI
      putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.timeInMillis)
      putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.timeInMillis + 30 * 60 * 1000)
      putExtra(CalendarContract.Events.TITLE, title)
      putExtra(CalendarContract.Events.DESCRIPTION, description)
      putExtra(CalendarContract.Events.EVENT_LOCATION, "Outlet Toko / Terminal QRIS")
      putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
      putExtra(CalendarContract.Events.HAS_ALARM, 1)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
      context.startActivity(intent)
    } catch (_: Exception) {
      // In case calendar app is not present
    }
  }
}
