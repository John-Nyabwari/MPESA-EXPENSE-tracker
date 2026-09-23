package com.example.ledger.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ledger.ui.components.TransactionRow
import com.example.ledger.ui.theme.WiseColors
import com.example.ledger.ui.theme.WiseText

/** README "Transaction list" — provider, counterparty, category, date, amount, type, review status. */
@Composable
fun TransactionListScreen(viewModel: TransactionListViewModel = hiltViewModel()) {
    val transactions by viewModel.transactions.collectAsState()

    if (transactions.isEmpty()) {
        Box(Modifier.fillMaxSize().background(WiseColors.Canvas), contentAlignment = Alignment.Center) {
            Text("No transactions yet — import SMS from Settings to get started.", style = WiseText.Body, color = WiseColors.TextSecondary)
        }
        return
    }

    LazyColumn(
        Modifier.fillMaxSize().background(WiseColors.Canvas),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        items(transactions, key = { it.id }) { transaction ->
            TransactionRow(transaction = transaction, categoryName = null, onClick = { })
        }
    }
}
