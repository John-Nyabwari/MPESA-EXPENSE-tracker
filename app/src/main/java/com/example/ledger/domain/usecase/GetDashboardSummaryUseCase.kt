package com.example.ledger.domain.usecase

import com.example.ledger.data.local.dao.TransactionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class DashboardSummary(
    val incomeMinor: Long,
    val expenseMinor: Long,
    val feesMinor: Long,
    val netCashFlowMinor: Long,
    val fulizaOutstandingMinor: Long,
)

class GetDashboardSummaryUseCase @javax.inject.Inject constructor(
    private val transactionDao: TransactionDao,
) {
    operator fun invoke(fromMillis: Long, toMillis: Long): Flow<DashboardSummary> =
        combine(
            transactionDao.observeIncomeTotal(fromMillis, toMillis),
            transactionDao.observeExpenseTotal(fromMillis, toMillis),
            transactionDao.observeFeesTotal(fromMillis, toMillis),
            transactionDao.observeFulizaOutstandingMinor(),
        ) { income, expense, fees, fuliza ->
            DashboardSummary(
                incomeMinor = income,
                expenseMinor = expense,
                feesMinor = fees,
                netCashFlowMinor = income - expense - fees,
                fulizaOutstandingMinor = fuliza,
            )
        }
}
