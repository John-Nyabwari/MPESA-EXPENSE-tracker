package com.example.ledger.domain.repository

import com.example.ledger.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeInRange(fromMillis: Long, toMillis: Long): Flow<List<Transaction>>
    fun observeUnreviewed(): Flow<List<Transaction>>
    suspend fun updateCategory(transactionId: Long, categoryId: Long)
    suspend fun ignore(transactionId: Long)
    suspend fun deleteAllData()
}
