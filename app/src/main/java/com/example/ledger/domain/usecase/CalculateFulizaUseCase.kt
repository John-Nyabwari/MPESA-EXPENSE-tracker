package com.example.ledger.domain.usecase

import com.example.ledger.data.local.dao.TransactionDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class FulizaState(val outstandingMinor: Long)

/**
 * Outstanding Fuliza liability = sum(drawdowns) - sum(repayments), streamed live so the
 * overdraft dashboard updates as new SMS come in. Kept separate from normal spend totals
 * per README's "track overdraft services separately" goal.
 */
class CalculateFulizaUseCase @Inject constructor(
    private val transactionDao: TransactionDao,
) {
    operator fun invoke(): Flow<FulizaState> =
        kotlinx.coroutines.flow.map(transactionDao.observeFulizaOutstandingMinor()) { FulizaState(it) }
}
