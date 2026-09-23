package com.example.ledger.data.repository

import com.example.ledger.data.local.dao.CategoryDao
import com.example.ledger.data.local.dao.RawSmsDao
import com.example.ledger.data.local.dao.TransactionDao
import com.example.ledger.data.local.entities.TransactionEntity
import com.example.ledger.domain.model.ReviewStatus
import com.example.ledger.domain.model.Transaction
import com.example.ledger.domain.model.TransactionSource
import com.example.ledger.domain.model.TransactionType
import com.example.ledger.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val rawSmsDao: RawSmsDao,
    private val categoryDao: CategoryDao,
) : TransactionRepository {

    override fun observeInRange(fromMillis: Long, toMillis: Long): Flow<List<Transaction>> =
        transactionDao.observeInRange(fromMillis, toMillis).map { list -> list.map { it.toDomain() } }

    override fun observeUnreviewed(): Flow<List<Transaction>> =
        transactionDao.observeUnreviewed().map { list -> list.map { it.toDomain() } }

    override suspend fun updateCategory(transactionId: Long, categoryId: Long) {
        val entity = transactionDao.getById(transactionId) ?: return
        transactionDao.update(entity.copy(categoryId = categoryId, reviewStatus = ReviewStatus.OK.name))
    }

    override suspend fun ignore(transactionId: Long) {
        val entity = transactionDao.getById(transactionId) ?: return
        transactionDao.update(entity.copy(reviewStatus = ReviewStatus.IGNORED.name))
    }

    override suspend fun deleteAllData() {
        transactionDao.deleteAll()
        rawSmsDao.deleteAll()
    }
}

private fun TransactionEntity.toDomain() = Transaction(
    id = id,
    source = TransactionSource.valueOf(source),
    type = TransactionType.valueOf(type),
    amountMinor = amountMinor,
    balanceAfterMinor = balanceAfterMinor,
    counterparty = counterparty,
    reference = reference,
    categoryId = categoryId,
    occurredAt = Instant.ofEpochMilli(occurredAtEpochMillis),
    rawSmsId = rawSmsId,
    reviewStatus = ReviewStatus.valueOf(reviewStatus),
    notes = notes,
    parserConfidence = parserConfidence,
    isFuliza = isFuliza,
)
