package com.example.ledger.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ledger.domain.model.Transaction
import com.example.ledger.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    repository: TransactionRepository,
) : ViewModel() {

    private val now = Instant.now()
    private val start = now.minus(365, ChronoUnit.DAYS)

    val transactions: StateFlow<List<Transaction>> =
        repository.observeInRange(start.toEpochMilli(), now.toEpochMilli())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
