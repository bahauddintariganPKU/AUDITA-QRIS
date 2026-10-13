package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

  @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
  fun getAllTransactions(): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
  suspend fun getAllTransactionsSnapshot(): List<TransactionEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(entity: TransactionEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(entities: List<TransactionEntity>)

  @Update
  suspend fun update(entity: TransactionEntity)

  @Delete
  suspend fun delete(entity: TransactionEntity)

  @Query("DELETE FROM transactions")
  suspend fun deleteAll()

  @Query("SELECT * FROM transactions WHERE isSynced = 0")
  suspend fun getUnsynced(): List<TransactionEntity>

  @Query("UPDATE transactions SET isSynced = 1")
  suspend fun markAllSynced()
}
