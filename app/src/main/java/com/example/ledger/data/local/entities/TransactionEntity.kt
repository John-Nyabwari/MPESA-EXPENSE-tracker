package com.example.ledger.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["dedupeKey"], unique = true),
        Index(value = ["occurredAt"]),
        Index(value = ["categoryId"]),
        Index(value = ["reviewStatus"]),
    ],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val source: String,
    val type: String,
    val amountMinor: Long,
    val balanceAfterMinor: Long?,
    val counterparty: String?,
    val reference: String?,
    val categoryId: Long?,
    val occurredAtEpochMillis: Long,
    val rawSmsId: Long?,
    val reviewStatus: String,
    val notes: String?,
    val parserConfidence: Float,
    val isFuliza: Boolean,
    /**
     * Stable hash of (source + amount + occurredAt-bucket + reference) used to prevent
     * duplicate imports when the same SMS is scanned more than once.
     */
    val dedupeKey: String,
)
