package com.example.ledger.util

/** Masks account/phone identifiers in the UI, e.g. "0722111222" -> "0722***222". */
object MaskingUtils {
    fun maskPhoneOrAccount(value: String?): String {
        if (value.isNullOrBlank()) return "—"
        val digits = value.filter { it.isDigit() }
        if (digits.length < 6) return value
        return digits.take(4) + "***" + digits.takeLast(3)
    }
}
