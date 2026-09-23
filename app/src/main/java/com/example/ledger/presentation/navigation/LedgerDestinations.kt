package com.example.ledger.presentation.navigation

sealed class LedgerDestination(val route: String) {
    data object Onboarding : LedgerDestination("onboarding")
    data object Dashboard : LedgerDestination("dashboard")
    data object Transactions : LedgerDestination("transactions")
    data object Categories : LedgerDestination("categories")
    data object SmartRules : LedgerDestination("smart_rules")
    data object Fuliza : LedgerDestination("fuliza")
    data object Settings : LedgerDestination("settings")
}
