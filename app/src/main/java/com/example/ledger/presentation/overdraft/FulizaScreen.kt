package com.example.ledger.presentation.overdraft

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ledger.ui.theme.WiseColors
import com.example.ledger.ui.theme.WiseText
import com.example.ledger.util.CurrencyFormatter

/** README §6 "Fuliza Tracking" — outstanding liability tracked separately from normal spend. */
@Composable
fun FulizaScreen(viewModel: FulizaViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(Modifier.fillMaxSize().background(WiseColors.Canvas).padding(16.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WiseColors.SurfaceSunken)
                .padding(20.dp),
        ) {
            Text("FULIZA OUTSTANDING", style = WiseText.LabelUpper, color = WiseColors.Pending)
            Spacer(Modifier.height(8.dp))
            Text(CurrencyFormatter.format(state.outstandingMinor), style = WiseText.Balance, color = WiseColors.Pending)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Drawdowns, repayments and fees detected from M-PESA SMS. History and trend chart wire up once transaction filtering by isFuliza ships in the Transactions screen.",
            style = WiseText.Body,
            color = WiseColors.TextSecondary,
        )
    }
}
