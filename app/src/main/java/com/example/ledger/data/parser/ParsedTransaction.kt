package com.example.ledger.data.parser

import com.example.ledger.domain.model.TransactionSource
import com.example.ledger.domain.model.TransactionType
import java.time.Instant

/**
 * Output of a single parser attempt. `confidence` lets the review queue prioritize
 * low-confidence parses even when a regex technically matched.
 */
data class ParsedTransaction(
    val source: TransactionSource,
    val type: TransactionType,
    val amountMinor: Long,
    val balanceAfterMinor: Long?,
    val counterparty: String?,
    val reference: String?,
    val occurredAt: Instant,
    val isFuliza: Boolean = false,
    val confidence: Float = 1f,
)

sealed class ParseResult {
    data class Success(val transaction: ParsedTransaction) : ParseResult()
    data class NeedsReview(val reason: String, val partial: ParsedTransaction? = null) : ParseResult()
    data class NotMatched(val reason: String = "no parser matched") : ParseResult()
}
