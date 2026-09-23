package com.example.ledger.data.parser

import com.example.ledger.domain.model.TransactionSource
import com.example.ledger.domain.model.TransactionType
import java.time.Instant

/**
 * Base class for bank SMS parsers. Banks tend to share a loose shape — a keyword
 * ("debited"/"credited"), an amount, a reference, and sometimes a running balance —
 * but exact wording and field order differs per bank and can change over time.
 * Each concrete bank parser supplies its own regex set; shared amount/date parsing
 * lives in ParsingUtils.
 */
abstract class BankParser : TransactionParser {

    protected abstract val source: TransactionSource

    protected fun result(
        type: TransactionType,
        amountMinor: Long,
        balanceMinor: Long?,
        counterparty: String?,
        reference: String?,
        occurredAt: Instant,
    ) = ParseResult.Success(
        ParsedTransaction(
            source = source,
            type = type,
            amountMinor = amountMinor,
            balanceAfterMinor = balanceMinor,
            counterparty = counterparty,
            reference = reference,
            occurredAt = occurredAt,
        ),
    )

    protected fun needsReview(reason: String) = ParseResult.NeedsReview(reason)
}
