package com.example.ledger.data.parser

import com.example.ledger.domain.model.TransactionSource
import com.example.ledger.domain.model.TransactionType
import java.time.Instant
import javax.inject.Inject

/**
 * Sample formats targeted (verify/extend against real KCB messages — see
 * src/test/resources/sms/kcb/):
 *
 *   "KCB: Confirmed. Ksh5,000.00 debited from A/C ****1234 on 23-09-2026 10:14:00.
 *    Ref FT26XXXXX. Available balance Ksh12,340.00."
 *
 *   "KCB: Confirmed. Ksh10,000.00 credited to A/C ****1234 on 23-09-2026 09:02:00.
 *    Ref FT26XXXXY. Available balance Ksh22,340.00."
 */
class KcbParser @Inject constructor() : BankParser() {

    override val source = TransactionSource.KCB
    override val senderMatchers = listOf(Regex("(?i)kcb"))

    private val debitRegex = Regex(
        """(?i)Ksh([\d,]+\.\d{2})\s+debited from A/C\s+([\w*]+)\s+on\s+([\d-]+\s+[\d:]+)\.?\s*Ref\s+(\S+)\.?\s*Available balance Ksh([\d,]+\.\d{2})""",
    )
    private val creditRegex = Regex(
        """(?i)Ksh([\d,]+\.\d{2})\s+credited to A/C\s+([\w*]+)\s+on\s+([\d-]+\s+[\d:]+)\.?\s*Ref\s+(\S+)\.?\s*Available balance Ksh([\d,]+\.\d{2})""",
    )

    override fun parse(sender: String, body: String, receivedAt: Instant): ParseResult {
        debitRegex.find(body)?.let { m ->
            val (amount, acct, date, ref, balance) = m.destructured
            val amountMinor = ParsingUtils.parseAmountToMinor(amount) ?: return needsReview("bad amount")
            val balanceMinor = ParsingUtils.parseAmountToMinor(balance)
            val occurredAt = ParsingUtils.parseBankDateTime(date) ?: receivedAt
            return result(TransactionType.EXPENSE, amountMinor, balanceMinor, acct, ref, occurredAt)
        }
        creditRegex.find(body)?.let { m ->
            val (amount, acct, date, ref, balance) = m.destructured
            val amountMinor = ParsingUtils.parseAmountToMinor(amount) ?: return needsReview("bad amount")
            val balanceMinor = ParsingUtils.parseAmountToMinor(balance)
            val occurredAt = ParsingUtils.parseBankDateTime(date) ?: receivedAt
            return result(TransactionType.INCOME, amountMinor, balanceMinor, acct, ref, occurredAt)
        }
        return if (body.contains("KCB", ignoreCase = true)) needsReview("Recognized as KCB but no known template matched")
        else ParseResult.NotMatched()
    }
}
