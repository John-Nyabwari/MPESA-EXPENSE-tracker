package com.example.ledger.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ledger.domain.usecase.DashboardSummary
import com.example.ledger.domain.usecase.GetDashboardSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getDashboardSummary: GetDashboardSummaryUseCase,
) : ViewModel() {

    private val now = Instant.now()
    private val periodStart = now.truncatedTo(ChronoUnit.DAYS).minus(30, ChronoUnit.DAYS)

    val summary: StateFlow<DashboardSummary> = getDashboardSummary(periodStart.toEpochMilli(), now.toEpochMilli())
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DashboardSummary(0, 0, 0, 0, 0),
        )
}
