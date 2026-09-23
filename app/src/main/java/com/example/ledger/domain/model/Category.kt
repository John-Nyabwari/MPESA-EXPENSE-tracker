package com.example.ledger.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val isCustom: Boolean = false,
)

object DefaultCategories {
    val names = listOf(
        "Food", "Transport", "Rent", "Utilities", "Airtime and data", "Shopping",
        "School fees", "Healthcare", "Salary", "Business income", "Transfers",
        "Bank fees", "M-Pesa fees", "Loan repayment", "Fuliza repayment",
        "Entertainment", "Uncategorized",
    )
}
