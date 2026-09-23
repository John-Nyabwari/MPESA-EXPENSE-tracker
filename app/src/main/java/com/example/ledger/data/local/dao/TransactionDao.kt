package com.example.ledger.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.ledger.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // Returns -1 if a row with the same dedupeKey already exists (IGNORE strategy on unique index).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE dedupeKey = :dedupeKey LIMIT 1")
    suspend fun findByDedupeKey(dedupeKey: String): TransactionEntity?

    @Query(
        """
        SELECT * FROM transactions
        WHERE occurredAtEpochMillis BETWEEN :fromMillis AND :toMillis
        ORDER BY occurredAtEpochMillis DESC
        """,
    )
    fun observeInRange(fromMillis: Long, toMillis: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE reviewStatus = 'NEEDS_REVIEW' ORDER BY occurredAtEpochMillis DESC")
    fun observeUnreviewed(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE isFuliza = 1 ORDER BY occurredAtEpochMillis DESC")
    fun observeFulizaActivity(): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type = 'FULIZA_DRAWDOWN' THEN amountMinor ELSE 0 END)
             - SUM(CASE WHEN type = 'FULIZA_REPAYMENT' THEN amountMinor ELSE 0 END), 0)
        FROM transactions
        """,
    )
    fun observeFulizaOutstandingMinor(): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type IN ('INCOME','TRANSFER_IN') THEN amountMinor ELSE 0 END), 0)
        FROM transactions WHERE occurredAtEpochMillis BETWEEN :fromMillis AND :toMillis
        """,
    )
    fun observeIncomeTotal(fromMillis: Long, toMillis: Long): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN type IN ('EXPENSE','TRANSFER_OUT') THEN amountMinor ELSE 0 END), 0)
        FROM transactions WHERE occurredAtEpochMillis BETWEEN :fromMillis AND :toMillis
        """,
    )
    fun observeExpenseTotal(fromMillis: Long, toMillis: Long): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(amountMinor), 0) FROM transactions
        WHERE type IN ('FEE','FULIZA_FEE') AND occurredAtEpochMillis BETWEEN :fromMillis AND :toMillis
        """,
    )
    fun observeFeesTotal(fromMillis: Long, toMillis: Long): Flow<Long>

    @Query(
        """
        SELECT categoryId, COALESCE(SUM(amountMinor), 0) as total FROM transactions
        WHERE type = 'EXPENSE' AND occurredAtEpochMillis BETWEEN :fromMillis AND :toMillis
        GROUP BY categoryId ORDER BY total DESC
        """,
    )
    fun observeExpenseByCategory(fromMillis: Long, toMillis: Long): Flow<List<CategoryTotal>>

    @Query("SELECT * FROM transactions WHERE source = 'MPESA' ORDER BY occurredAtEpochMillis DESC LIMIT 1")
    suspend fun latestMpesaTransaction(): TransactionEntity?

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

data class CategoryTotal(val categoryId: Long?, val total: Long)
