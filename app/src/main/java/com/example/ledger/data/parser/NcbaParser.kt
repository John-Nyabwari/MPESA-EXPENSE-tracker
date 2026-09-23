package com.example.ledger.data.parser

import com.example.ledger.domain.model.TransactionSource
import com.example.ledger.domain.model.TransactionType
import java.time.Instant
import javax.inject.Inject

/**
 * Sample formats targeted (see src/test/resources/sms/ncba/):
 *
 *   "NCBA: Your account ****1234 has been debited KES 3,000.00. Transaction ref NCBAXXXXX.
 *    Balance: KES 9,400.00 as at 23-09-2026 10:14:00"
 */
class NcbaParser @Inject constructor() : BankParser() {

    override val source = TransactionSource.NCBA
    override val senderMatchers = listOf(Regex("(?i)ncba"))

    private val debitRegex = Regex(
        """(?i)account\s+([\w*]+)\s+has been debited KES\s+([\d,]+\.\d{2})\.?\s*Transaction ref\s+(\S+)\.?\s*Balance:?\s*KES\s+([\d,]+\.\d{2})\s+as at\s+([\d\-:\s]+)""",
    )
    private val creditRegex = Regex(
        """(?i)account\s+([\w*]+)\s+has been credited KES\s+([\d,]+\.\d{2})\.?\s*Transaction ref\s+(\S+)\.?\s*Balance:?\s*KES\s+([\d,]+\.\d{2})\s+as at\s+([\d\-:\s]+)""",
    )

    override fun parse(sender: String, body: String, receivedAt: Instant): ParseResult {
        debitRegex.find(body)?.let { m ->
            val (acct, amount, ref, balance, date) = m.destructured
            val amountMinor = ParsingUtils.parseAmountToMinor(amount) ?: return needsReview("bad amount")
            val balanceMinor = ParsingUtils.parseAmountToMinor(balance)
            val occurredAt = ParsingUtils.parseBankDateTime(date) ?: receivedAt
            return result(TransactionType.EXPENSE, amountMinor, balanceMinor, acct, ref, occurredAt)
        }
        creditRegex.find(body)?.let { m ->
            val (acct, amount, ref, balance, date) = m.destructured
            val amountMinor = ParsingUtils.parseAmountToMinor(amount) ?: return needsReview("bad amount")
            val balanceMinor = ParsingUtils.parseAmountToMinor(balance)
            val occurredAt = ParsingUtils.parseBankDateTime(date) ?: receivedAt
            return result(TransactionType.INCOME, amountMinor, balanceMinor, acct, ref, occurredAt)
        }
        return if (body.contains("NCBA", ignoreCase = true)) needsReview("Recognized as NCBA but no known template matched")
        else ParseResult.NotMatched()
    }
}
