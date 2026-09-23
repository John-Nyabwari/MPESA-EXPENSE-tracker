package com.example.ledger.data.parser

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tries each registered parser's sender matcher, in order, and delegates parsing to the
 * first one whose sender pattern matches. Additional bank parsers (Absa, I&M, Stanbic,
 * DTB, Co-op) follow the same BankParser pattern as KcbParser/EquityParser/NcbaParser —
 * add them here once implemented.
 */
@Singleton
class ParserRegistry @Inject constructor(
    mpesaParser: MpesaParser,
    kcbParser: KcbParser,
    equityParser: EquityParser,
    ncbaParser: NcbaParser,
) {
    private val parsers: List<TransactionParser> = listOf(mpesaParser, kcbParser, equityParser, ncbaParser)

    fun parse(sender: String, body: String, receivedAt: Instant): ParseResult {
        val parser = parsers.firstOrNull { it.canHandle(sender) }
            ?: return ParseResult.NotMatched("no parser registered for sender '$sender'")
        return parser.parse(sender, body, receivedAt)
    }
}
