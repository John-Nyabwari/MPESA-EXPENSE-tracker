package com.example.ledger.domain.model

enum class TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER_IN,
    TRANSFER_OUT,
    FEE,
    LOAN_DISBURSEMENT,
    LOAN_REPAYMENT,
    FULIZA_DRAWDOWN,
    FULIZA_REPAYMENT,
    FULIZA_FEE,
    UNKNOWN,
}

enum class TransactionSource {
    MPESA, NCBA, KCB, EQUITY, COOP, ABSA, IM, STANBIC, DTB, OTHER, MANUAL
}

enum class ReviewStatus {
    OK, NEEDS_REVIEW, IGNORED
}
