package com.example.data

import com.example.data.local.AuditDatabase
import com.example.data.local.TransactionEntity
import com.example.forensics.ForensicAuditEngine
import com.example.model.TransactionItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(private val database: AuditDatabase) {

  private val dao = database.transactionDao()

  val allTransactions: Flow<List<TransactionItem>> = dao.getAllTransactions().map { entities ->
    entities.map { it.toModel() }
  }

  suspend fun ensureInitialized() {
    val existing = dao.getAllTransactionsSnapshot()
    if (existing.isEmpty()) {
      val seed = SampleData.getInitialSeed()
      val entities = seed.map { TransactionEntity.fromModel(it) }
      dao.insertAll(entities)
    }
  }

  suspend fun addTransaction(rawItem: TransactionItem): TransactionItem {
    // Fetch all, append new item, re-run auditBatch for precise split transaction & cross-tx rules
    val current = dao.getAllTransactionsSnapshot().map { it.toModel() }
    val combined = current + rawItem.copy(id = System.currentTimeMillis() % 100000)
    val audited = ForensicAuditEngine.auditBatch(combined)
    
    // Save all updated audited items
    dao.deleteAll()
    dao.insertAll(audited.map { TransactionEntity.fromModel(it) })

    return audited.find { it.rrn == rawItem.rrn } ?: rawItem
  }

  suspend fun resetToDefaultSeed() {
    dao.deleteAll()
    val seed = SampleData.getInitialSeed()
    dao.insertAll(seed.map { TransactionEntity.fromModel(it) })
  }

  suspend fun markAllSynced() {
    dao.markAllSynced()
  }

  suspend fun deleteTransaction(item: TransactionItem) {
    val current = dao.getAllTransactionsSnapshot().map { it.toModel() }
    val remaining = current.filter { it.id != item.id }
    val reAudited = ForensicAuditEngine.auditBatch(remaining)
    dao.deleteAll()
    dao.insertAll(reAudited.map { TransactionEntity.fromModel(it) })
  }
}
