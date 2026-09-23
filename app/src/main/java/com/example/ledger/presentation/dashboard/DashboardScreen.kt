package com.example.ledger.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ledger.ui.theme.WiseColors
import com.example.ledger.ui.theme.WiseText
import com.example.ledger.util.CurrencyFormatter

/**
 * Main dashboard — README "Main dashboard cards": total balance, income, expenses,
 * net cash flow, Fuliza outstanding, fees, unreviewed count. Laid out on Wise's
 * bright-canvas hero + card-grid pattern from DESIGN-android.md.
 */
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val summary by viewModel.summary.collectAsState()

    LazyColumn(
        Modifier.fillMaxSize().background(WiseColors.Canvas),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            HeroBalanceCard(netCashFlowMinor = summary.netCashFlowMinor)
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryTile("Income", summary.incomeMinor, WiseColors.Success, Modifier.weight(1f))
                SummaryTile("Expenses", summary.expenseMinor, WiseColors.Error, Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryTile("Fuliza outstanding", summary.fulizaOutstandingMinor, WiseColors.Pending, Modifier.weight(1f))
                SummaryTile("Fees (30d)", summary.feesMinor, WiseColors.TextSecondary, Modifier.weight(1f))
            }
        }
        item {
            Text("Last 30 days · charts and unreviewed count wire up once the transactions feed is live", style = WiseText.Caption, color = WiseColors.TextTertiary)
        }
    }
}

@Composable
private fun HeroBalanceCard(netCashFlowMinor: Long) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(WiseColors.Forest)
            .padding(20.dp),
    ) {
        Text("NET CASH FLOW · 30 DAYS", style = WiseText.LabelUpper, color = WiseColors.Bright)
        Spacer(Modifier.height(8.dp))
        Text(CurrencyFormatter.formatSigned(netCashFlowMinor), style = WiseText.Balance, color = androidx.compose.ui.graphics.Color.White)
    }
}

@Composable
private fun SummaryTile(label: String, amountMinor: Long, accent: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(WiseColors.Surface)
            .padding(16.dp),
    ) {
        Text(label, style = WiseText.Meta, color = WiseColors.TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text(CurrencyFormatter.format(amountMinor), style = WiseText.Currency, color = accent)
    }
}
