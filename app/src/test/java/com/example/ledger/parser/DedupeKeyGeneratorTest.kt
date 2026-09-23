package com.example.ledger.parser

import com.example.ledger.data.parser.DedupeKeyGenerator
import com.example.ledger.data.parser.ParsedTransaction
import com.example.ledger.domain.model.TransactionSource
import com.example.ledger.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.time.Instant

class DedupeKeyGeneratorTest {

    private fun sample(amount: Long = 50000, occurredAt: Instant = Instant.parse("2026-09-23T10:14:00Z")) =
        ParsedTransaction(
            source = TransactionSource.MPESA,
            type = TransactionType.TRANSFER_OUT,
            amountMinor = amount,
            balanceAfterMinor = null,
            counterparty = "JOHN DOE",
            reference = null,
            occurredAt = occurredAt,
        )

    @Test
    fun `identical transactions produce identical keys`() {
        assertEquals(DedupeKeyGenerator.generate(sample()), DedupeKeyGenerator.generate(sample()))
    }

    @Test
    fun `different amounts produce different keys`() {
        assertNotEquals(DedupeKeyGenerator.generate(sample(amount = 50000)), DedupeKeyGenerator.generate(sample(amount = 70000)))
    }

    @Test
    fun `sub-minute timestamp jitter still dedupes to the same key`() {
        val a = sample(occurredAt = Instant.parse("2026-09-23T10:14:00Z"))
        val b = sample(occurredAt = Instant.parse("2026-09-23T10:14:45Z"))
        assertEquals(DedupeKeyGenerator.generate(a), DedupeKeyGenerator.generate(b))
    }
}
