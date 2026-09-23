package com.example.ledger.data.parser

/**
 * A parser handles messages from one sender family (M-Pesa, or a specific bank).
 * Implementations should NOT assume a sender's format is stable — banks change SMS
 * templates over time, so each parser tries several known message shapes in order
 * and falls back to ParseResult.NeedsReview rather than guessing.
 */
interface TransactionParser {
    /** Sender identifiers (as seen in SMS "from" address) this parser can attempt. */
    val senderMatchers: List<Regex>

    fun canHandle(sender: String): Boolean =
        senderMatchers.any { it.containsMatchIn(sender) }

    fun parse(sender: String, body: String, receivedAt: java.time.Instant): ParseResult
}
