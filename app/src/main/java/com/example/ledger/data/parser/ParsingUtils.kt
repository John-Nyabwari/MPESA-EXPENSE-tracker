package com.example.ledger.data.parser

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

/** Shared helpers used by every sender-specific parser. */
object ParsingUtils {

    private val zone: ZoneId = ZoneId.of("Africa/Nairobi")

    // "Ksh1,234.50" / "KES 1,234.50" / "1,234" -> minor units (cents), rounded to nearest.
    fun parseAmountToMinor(raw: String): Long? {
        val cleaned = raw
            .replace(Regex("(?i)ksh|kes|/=|,"), "")
            .trim()
        val value = cleaned.toDoubleOrNull() ?: return null
        return Math.round(value * 100)
    }

    // M-Pesa style: "23/9/26" or "23/09/2026" combined with "10:14 AM"
    private val mpesaDateFormatters = listOf(
        DateTimeFormatterBuilder().appendPattern("d/M/yy").toFormatter(Locale.ENGLISH),
        DateTimeFormatterBuilder().appendPattern("d/M/yyyy").toFormatter(Locale.ENGLISH),
    )
    private val mpesaTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)

    fun parseMpesaDateTime(datePart: String, timePart: String): java.time.Instant? {
        val date = mpesaDateFormatters.firstNotNullOfOrNull { fmt ->
            runCatching { LocalDate.parse(datePart.trim(), fmt) }.getOrNull()
        } ?: return null
        val time = runCatching { LocalTime.parse(timePart.trim().uppercase(Locale.ENGLISH), mpesaTimeFormatter) }
            .getOrElse { LocalTime.MIDNIGHT }
        return LocalDateTime.of(date, time).atZone(zone).toInstant()
    }

    // Bank style: "23-09-2026 10:14:00" or "23-Sep-2026"
    private val bankFormatters = listOf(
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH),
    )

    fun parseBankDateTime(raw: String): java.time.Instant? {
        for (fmt in bankFormatters) {
            runCatching {
                return LocalDateTime.parse(raw.trim(), fmt).atZone(zone).toInstant()
            }
            runCatching {
                return LocalDate.parse(raw.trim(), fmt).atStartOfDay(zone).toInstant()
            }
        }
        return null
    }
}
