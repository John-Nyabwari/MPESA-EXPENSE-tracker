package com.example.ledger.util

import java.util.Locale

/** Formats minor-unit (cents) Long amounts as "KSh 1,234.50", masking not applied here —
 *  UI layer decides when to mask per README's "mask account and phone numbers" requirement. */
object CurrencyFormatter {
    fun format(amountMinor: Long): String {
        val major = amountMinor / 100.0
        return String.format(Locale.US, "KSh %,.2f", major)
    }

    fun formatSigned(amountMinor: Long): String {
        val sign = if (amountMinor < 0) "-" else "+"
        return "$sign${format(kotlin.math.abs(amountMinor))}"
    }
}
