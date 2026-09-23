package com.example.ledger.domain.usecase

import com.example.ledger.data.local.dao.RawSmsDao
import com.example.ledger.data.local.dao.TransactionDao
import com.example.ledger.data.local.entities.RawSmsEntity
import com.example.ledger.data.local.entities.TransactionEntity
import com.example.ledger.data.parser.DedupeKeyGenerator
import com.example.ledger.data.parser.ParseResult
import com.example.ledger.data.parser.ParserRegistry
import com.example.ledger.domain.model.ReviewStatus
import java.time.Instant
import javax.inject.Inject

data class ImportSummary(
    var imported: Int = 0,
    var duplicates: Int = 0,
    var unrecognized: Int = 0,
    var skipped: Int = 0,
)

/**
 * Shared pipeline for both the one-off historical import and the per-message flow
 * triggered by SmsReceiver -> SmsImportWorker. Mirrors the README's system architecture:
 * Normalizer -> Sender Resolver -> Parser -> Smart Rules -> Duplicate Detector -> Validator.
 */
class ImportSmsUseCase @Inject constructor(
    private val parserRegistry: ParserRegistry,
    private val applySmartRules: ApplySmartRulesUseCase,
    private val transactionDao: TransactionDao,
    private val rawSmsDao: RawSmsDao,
) {
    suspend fun importOne(sender: String, body: String, receivedAt: Instant): ImportSummary {
        val summary = ImportSummary()
        val normalizedSender = sender.trim().uppercase()

        when (val result = parserRegistry.parse(normalizedSender, body, receivedAt)) {
            is ParseResult.Success -> {
                val parsed = result.transaction
                val dedupeKey = DedupeKeyGenerator.generate(parsed)
                if (transactionDao.findByDedupeKey(dedupeKey) != null) {
                    summary.duplicates++
                    return summary
                }

                val ruleMatches = applySmartRules(normalizedSender, body)
                val categoryId = ruleMatches.firstOrNull()?.rule?.categoryId
                val needsReviewForMultiMatch = ruleMatches.size > 1

                val entity = TransactionEntity(
                    source = parsed.source.name,
                    type = parsed.type.name,
                    amountMinor = parsed.amountMinor,
                    balanceAfterMinor = parsed.balanceAfterMinor,
                    counterparty = parsed.counterparty,
                    reference = parsed.reference,
                    categoryId = categoryId,
                    occurredAtEpochMillis = parsed.occurredAt.toEpochMilli(),
                    rawSmsId = null,
                    reviewStatus = if (needsReviewForMultiMatch) ReviewStatus.NEEDS_REVIEW.name else ReviewStatus.OK.name,
                    notes = null,
                    parserConfidence = parsed.confidence,
                    isFuliza = parsed.isFuliza,
                    dedupeKey = dedupeKey,
                )
                val insertedId = transactionDao.insert(entity)
                if (insertedId == -1L) summary.duplicates++ else summary.imported++
            }

            is ParseResult.NeedsReview -> {
                rawSmsDao.insert(
                    RawSmsEntity(
                        sender = normalizedSender,
                        body = body,
                        receivedAtEpochMillis = receivedAt.toEpochMilli(),
                        parseStatus = "FAILED",
                        matchedParser = null,
                    ),
                )
                summary.unrecognized++
            }

            is ParseResult.NotMatched -> {
                summary.skipped++
            }
        }
        return summary
    }

    suspend fun importBatch(messages: List<Triple<String, String, Instant>>): ImportSummary {
        val total = ImportSummary()
        for ((sender, body, receivedAt) in messages) {
            val one = importOne(sender, body, receivedAt)
            total.imported += one.imported
            total.duplicates += one.duplicates
            total.unrecognized += one.unrecognized
            total.skipped += one.skipped
        }
        return total
    }
}
