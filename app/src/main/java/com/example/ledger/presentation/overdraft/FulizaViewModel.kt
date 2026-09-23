package com.example.ledger.presentation.overdraft

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ledger.domain.usecase.CalculateFulizaUseCase
import com.example.ledger.domain.usecase.FulizaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FulizaViewModel @Inject constructor(
    calculateFuliza: CalculateFulizaUseCase,
) : ViewModel() {
    val state: StateFlow<FulizaState> = calculateFuliza()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FulizaState(0))
}
