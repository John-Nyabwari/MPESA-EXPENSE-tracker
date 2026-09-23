package com.example.ledger.data.parser

import com.example.ledger.domain.model.TransactionSource
import com.example.ledger.domain.model.TransactionType
import java.time.Instant
import javax.inject.Inject

/**
 * Parses Safaricom M-Pesa confirmation SMS. Safaricom uses several distinct templates
 * depending on the action (send, receive, pay bill, buy goods, withdraw, airtime, Fuliza).
 * Each template gets its own regex; `parse` tries them in a fixed order and returns the
 * first match. Unmatched-but-M-Pesa-looking messages fall through to NeedsReview so they
 * surface in the review queue instead of being silently dropped.
 *
 * Sample messages this parser targets (real message text varies by Safaricom's changes,
 * hence the module's own fixtures in src/test/resources/sms/mpesa/ should be kept current):
 *
 *   "QFT7XXXXX Confirmed. Ksh500.00 sent to JOHN DOE 0722111222 on 23/9/26 at 10:14 AM.
 *    New M-PESA balance is Ksh2,340.00. Transaction cost, Ksh7.00."
 *
 *   "QFT7XXXXX Confirmed.You have received Ksh1,000.00 from JANE DOE 0733222111 on
 *    23/9/26 at 9:02 AM. New M-PESA balance is Ksh3,340.00."
 *
 *   "QFT7XXXXX Confirmed. Ksh200.00 paid to CARREFOUR. on 23/9/26 at 1:15 PM.
 *    New M-PESA balance is Ksh1,140.00."
 *
 *   "QFT7XXXXX Confirmed. Fuliza M-PESA amount is Ksh300.00, transaction cost Ksh15.00.
 *    Total outstanding Fuliza M-PESA balance including charges is Ksh315.00."
 */
class MpesaParser @Inject constructor() : TransactionParser {

    override val senderMatchers = listOf(Regex("(?i)^m-?pesa$"))

    private val sentRegex = Regex(
        """(?i)Confirmed\.\s*Ksh([\d,]+\.\d{2})\s+sent to\s+(.+?)\s+(?:\d{9,12}\s+)?on\s+([\d/]+)\s+at\s+([\d:apAPM\s]+?)\.\s*New M-PESA balance is Ksh([\d,]+\.\d{2})(?:\.\s*Transaction cost,?\s*Ksh([\d,]+\.\d{2}))?""",
    )

    private val receivedRegex = Regex(
        """(?i)Confirmed\.\s*You have received Ksh([\d,]+\.\d{2})\s+from\s+(.+?)\s+(?:\d{9,12}\s+)?on\s+([\d/]+)\s+at\s+([\d:apAPM\s]+?)\.\s*New M-PESA balance is Ksh([\d,]+\.\d{2})""",
    )

    private val paidRegex = Regex(
        """(?i)Confirmed\.\s*Ksh([\d,]+\.\d{2})\s+paid to\s+(.+?)\.\s*on\s+([\d/]+)\s+at\s+([\d:apAPM\s]+?)\.\s*New M-PESA balance is Ksh([\d,]+\.\d{2})""",
    )

    private val withdrawRegex = Regex(
        """(?i)Confirmed\.\s*Ksh([\d,]+\.\d{2})\s+withdrawn from\s+(.+?)\s+on\s+([\d/]+)\s+at\s+([\d:apAPM\s]+?)\.\s*New M-PESA balance is Ksh([\d,]+\.\d{2})(?:\.\s*Transaction cost,?\s*Ksh([\d,]+\.\d{2}))?""",
    )

    private val airtimeRegex = Regex(
        """(?i)Confirmed\.\s*Ksh([\d,]+\.\d{2})\s+(?:of airtime )?bought\s?(?:for)?\s*(.+?)?\.?\s*on\s+([\d/]+)\s+at\s+([\d:apAPM\s]+?)\.\s*New M-PESA balance is Ksh([\d,]+\.\d{2})""",
    )

    private val fulizaRegex = Regex(
        """(?i)Fuliza M-PESA amount is Ksh([\d,]+\.\d{2}),?\s*transaction cost Ksh([\d,]+\.\d{2})\.\s*Total outstanding Fuliza M-PESA balance including charges is Ksh([\d,]+\.\d{2})""",
    )

    private val fulizaRepaymentRegex = Regex(
        """(?i)Confirmed\.\s*Ksh([\d,]+\.\d{2}) from your M-PESA has been used to fully pay(?:ment for)? Fuliza M-PESA""",
    )

    override fun parse(sender: String, body: String, receivedAt: Instant): ParseResult {
        fulizaRegex.find(body)?.let { m ->
            val (amount, cost, _) = m.destructured
            val amountMinor = ParsingUtils.parseAmountToMinor(amount) ?: return needsReview(body)
            return ParseResult.Success(
                ParsedTransaction(
                    source = TransactionSource.MPESA,
                    type = TransactionType.FULIZA_DRAWDOWN,
                    amountMinor = amountMinor,
                    balanceAfterMinor = null,
                    counterparty = "Fuliza",
                    reference = null,
                    occurredAt = receivedAt,
                    isFuliza = true,
                ),
            )
        }

        fulizaRepaymentRegex.find(body)?.let { m ->
            val amountMinor = ParsingUtils.parseAmountToMinor(m.groupValues[1]) ?: return needsReview(body)
            return ParseResult.Success(
                ParsedTransaction(
                    source = TransactionSource.MPESA,
                    type = TransactionType.FULIZA_REPAYMENT,
                    amountMinor = amountMinor,
                    balanceAfterMinor = null,
                    counterparty = "Fuliza",
                    reference = null,
                    occurredAt = receivedAt,
                    isFuliza = true,
                ),
            )
        }

        sentRegex.find(body)?.let { m ->
            val (amount, to, date, time, balance, cost) = m.destructured
            return buildResult(TransactionType.TRANSFER_OUT, amount, to.trim(), date, time, balance, receivedAt)
        }
        receivedRegex.find(body)?.let { m ->
            val (amount, from, date, time, balance) = m.destructured
            return buildResult(TransactionType.INCOME, amount, from.trim(), date, time, balance, receivedAt)
        }
        paidRegex.find(body)?.let { m ->
            val (amount, to, date, time, balance) = m.destructured
            return buildResult(TransactionType.EXPENSE, amount, to.trim(), date, time, balance, receivedAt)
        }
        withdrawRegex.find(body)?.let { m ->
            val (amount, agent, date, time, balance, _) = m.destructured
            return buildResult(TransactionType.EXPENSE, amount, agent.trim(), date, time, balance, receivedAt)
        }
        airtimeRegex.find(body)?.let { m ->
            val (amount, _, date, time, balance) = m.destructured
            return buildResult(TransactionType.EXPENSE, amount, "Airtime", date, time, balance, receivedAt)
        }

        return if (body.contains("M-PESA", ignoreCase = true) || body.contains("Confirmed", ignoreCase = true)) {
            needsReview(body)
        } else {
            ParseResult.NotMatched()
        }
    }

    private fun buildResult(
        type: TransactionType,
        amount: String,
        counterparty: String,
        date: String,
        time: String,
        balance: String,
        fallbackInstant: Instant,
    ): ParseResult {
        val amountMinor = ParsingUtils.parseAmountToMinor(amount) ?: return needsReview("unparsable amount: $amount")
        val balanceMinor = ParsingUtils.parseAmountToMinor(balance)
        val occurredAt = ParsingUtils.parseMpesaDateTime(date, time) ?: fallbackInstant
        return ParseResult.Success(
            ParsedTransaction(
                source = TransactionSource.MPESA,
                type = type,
                amountMinor = amountMinor,
                balanceAfterMinor = balanceMinor,
                counterparty = counterparty,
                reference = null,
                occurredAt = occurredAt,
            ),
        )
    }

    private fun needsReview(body: String) =
        ParseResult.NeedsReview("Recognized as M-PESA but no known template matched")
}
