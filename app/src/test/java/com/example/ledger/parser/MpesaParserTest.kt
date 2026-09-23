package com.example.ledger.parser

import com.example.ledger.data.parser.MpesaParser
import com.example.ledger.data.parser.ParseResult
import com.example.ledger.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class MpesaParserTest {

    private val parser = MpesaParser()

    @Test
    fun `parses a sent-money confirmation`() {
        val body = "QFT7XXXXX Confirmed. Ksh500.00 sent to JOHN DOE 0722111222 on 23/9/26 at 10:14 AM. " +
            "New M-PESA balance is Ksh2,340.00. Transaction cost, Ksh7.00."

        val result = parser.parse("MPESA", body, Instant.now())

        assertTrue(result is ParseResult.Success)
        val parsed = (result as ParseResult.Success).transaction
        assertEquals(TransactionType.TRANSFER_OUT, parsed.type)
        assertEquals(50000L, parsed.amountMinor)
        assertEquals(234000L, parsed.balanceAfterMinor)
        assertTrue(parsed.counterparty!!.contains("JOHN DOE"))
    }

    @Test
    fun `parses a received-money confirmation`() {
        val body = "QFT7XXXXX Confirmed.You have received Ksh1,000.00 from JANE DOE 0733222111 on " +
            "23/9/26 at 9:02 AM. New M-PESA balance is Ksh3,340.00."

        val result = parser.parse("MPESA", body, Instant.now())

        assertTrue(result is ParseResult.Success)
        val parsed = (result as ParseResult.Success).transaction
        assertEquals(TransactionType.INCOME, parsed.type)
        assertEquals(100000L, parsed.amountMinor)
    }

    @Test
    fun `parses a Fuliza drawdown`() {
        val body = "QFT7XXXXX Confirmed. Fuliza M-PESA amount is Ksh300.00, transaction cost Ksh15.00. " +
            "Total outstanding Fuliza M-PESA balance including charges is Ksh315.00."

        val result = parser.parse("MPESA", body, Instant.now())

        assertTrue(result is ParseResult.Success)
        val parsed = (result as ParseResult.Success).transaction
        assertEquals(TransactionType.FULIZA_DRAWDOWN, parsed.type)
        assertTrue(parsed.isFuliza)
        assertEquals(30000L, parsed.amountMinor)
    }

    @Test
    fun `unrecognized M-PESA template goes to NeedsReview, not silently dropped`() {
        val body = "M-PESA Confirmed. Some brand new template Safaricom hasn't used before."
        val result = parser.parse("MPESA", body, Instant.now())
        assertTrue(result is ParseResult.NeedsReview)
    }

    @Test
    fun `unrelated sender is not matched`() {
        val result = parser.parse("SOMEBANK", "Hello there", Instant.now())
        assertTrue(result is ParseResult.NotMatched)
    }
}
