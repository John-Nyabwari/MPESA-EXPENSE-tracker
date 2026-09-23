package com.example.ledger.domain.model

import java.time.Instant

/**
 * Normalized, parsed transaction — the core domain object the whole app operates on.
 * Amounts are stored in minor units (cents) as Long to avoid floating point drift.
 */
data class Transaction(
    val id: Long = 0,
    val source: TransactionSource,
    val type: TransactionType,
    val amountMinor: Long,
    val balanceAfterMinor: Long?,
    val counterparty: String?,
    val reference: String?,
    val categoryId: Long?,
    val occurredAt: Instant,
    val rawSmsId: Long?,
    val reviewStatus: ReviewStatus,
    val notes: String? = null,
    val parserConfidence: Float = 1f,
    val isFuliza: Boolean = false,
)
