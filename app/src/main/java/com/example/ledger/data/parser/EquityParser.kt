package com.example.ledger.data.parser

import com.example.ledger.domain.model.TransactionSource
import com.example.ledger.domain.model.TransactionType
import java.time.Instant
import javax.inject.Inject

/**
 * Sample formats targeted (see src/test/resources/sms/equity/):
 *
 *   "EQUITY BANK: A/C ****1234 has been Debited with KES 2,500.00 on 23-Sep-2026.
 *    New Balance is KES 8,300.00. Ref: EBXXXXXXX"
 *
 *   "EQUITY BANK: A/C ****1234 has been Credited with KES 15,000.00 on 23-Sep-2026.
 *    New Balance is KES 23,300.00. Ref: EBYYYYYYY"
 */
class EquityParser @Inject constructor() : BankParser() {

    override val source = TransactionSource.EQUITY
    override val senderMatchers = listOf(Regex("(?i)equity"))

    private val debitRegex = Regex(
        """(?i)A/C\s+([\w*]+)\s+has been Debited with KES\s+([\d,]+\.\d{2})\s+on\s+([\d\-A-Za-z]+)\.?\s*New Balance is KES\s+([\d,]+\.\d{2})\.?\s*Ref:?\s*(\S+)""",
    )
    private val creditRegex = Regex(
        """(?i)A/C\s+([\w*]+)\s+has been Credited with KES\s+([\d,]+\.\d{2})\s+on\s+([\d\-A-Za-z]+)\.?\s*New Balance is KES\s+([\d,]+\.\d{2})\.?\s*Ref:?\s*(\S+)""",
    )

    override fun parse(sender: String, body: String, receivedAt: Instant): ParseResult {
        debitRegex.find(body)?.let { m ->
            val (acct, amount, date, balance, ref) = m.destructured
            val amountMinor = ParsingUtils.parseAmountToMinor(amount) ?: return needsReview("bad amount")
            val balanceMinor = ParsingUtils.parseAmountToMinor(balance)
            val occurredAt = ParsingUtils.parseBankDateTime(date) ?: receivedAt
            return result(TransactionType.EXPENSE, amountMinor, balanceMinor, acct, ref, occurredAt)
        }
        creditRegex.find(body)?.let { m ->
            val (acct, amount, date, balance, ref) = m.destructured
            val amountMinor = ParsingUtils.parseAmountToMinor(amount) ?: return needsReview("bad amount")
            val balanceMinor = ParsingUtils.parseAmountToMinor(balance)
            val occurredAt = ParsingUtils.parseBankDateTime(date) ?: receivedAt
            return result(TransactionType.INCOME, amountMinor, balanceMinor, acct, ref, occurredAt)
        }
        return if (body.contains("EQUITY", ignoreCase = true)) needsReview("Recognized as Equity but no known template matched")
        else ParseResult.NotMatched()
    }
}
