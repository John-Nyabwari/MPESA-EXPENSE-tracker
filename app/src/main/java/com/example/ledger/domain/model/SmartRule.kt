package com.example.ledger.domain.model

enum class MatchField { SENDER, MESSAGE_TEXT, BOTH }

data class SmartRule(
    val id: Long = 0,
    val name: String,
    val matchField: MatchField,
    val pattern: String,
    val isRegex: Boolean,
    val caseInsensitive: Boolean = true,
    val categoryId: Long?,
    val assignedType: TransactionType?,
    val assignedSource: TransactionSource?,
    val priority: Int = 0,
    val enabled: Boolean = true,
)
