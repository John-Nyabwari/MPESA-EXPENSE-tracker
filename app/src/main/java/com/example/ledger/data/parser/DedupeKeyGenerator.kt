package com.example.ledger.data.parser

import java.security.MessageDigest

/**
 * Same SMS scanned twice (e.g. historical import re-run, or a message delivered via both
 * the broadcast receiver and a later full-inbox scan) must not create two transactions.
 * We hash source + type + amount + a coarse time bucket + counterparty/reference; a coarse
 * (minute-level) time bucket tolerates the occasional few-hundred-ms timestamp jitter some
 * OEM SMS providers introduce without merging genuinely distinct transactions.
 */
object DedupeKeyGenerator {
    fun generate(parsed: ParsedTransaction): String {
        val bucketMillis = parsed.occurredAt.toEpochMilli() / 60_000
        val raw = listOf(
            parsed.source.name,
            parsed.type.name,
            parsed.amountMinor.toString(),
            bucketMillis.toString(),
            parsed.counterparty.orEmpty().trim().lowercase(),
            parsed.reference.orEmpty().trim().lowercase(),
        ).joinToString("|")

        val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
