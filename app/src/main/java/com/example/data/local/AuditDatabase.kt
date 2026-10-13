package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class AuditDatabase : RoomDatabase() {

  abstract fun transactionDao(): TransactionDao

  companion object {
    @Volatile
    private var INSTANCE: AuditDatabase? = null

    fun getDatabase(context: Context): AuditDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AuditDatabase::class.java,
          "audita_qris_forensic.db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
