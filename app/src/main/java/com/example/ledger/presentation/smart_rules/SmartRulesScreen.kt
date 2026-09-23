package com.example.ledger.presentation.smart_rules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ledger.ui.theme.WiseColors
import com.example.ledger.ui.theme.WiseText

/**
 * README §4 "Smart SMS rules" — sender/keyword matching rules that auto-categorize future
 * transactions, ordered by priority (highest first, matching ApplySmartRulesUseCase).
 */
@Composable
fun SmartRulesScreen(viewModel: SmartRulesViewModel = hiltViewModel()) {
    val rules by viewModel.rules.collectAsState()

    if (rules.isEmpty()) {
        androidx.compose.foundation.layout.Box(Modifier.fillMaxSize().background(WiseColors.Canvas), contentAlignment = Alignment.Center) {
            Text("No smart rules yet. Create one from any transaction to auto-categorize future messages.", style = WiseText.Body, color = WiseColors.TextSecondary, modifier = Modifier.padding(32.dp))
        }
        return
    }

    LazyColumn(Modifier.fillMaxSize().background(WiseColors.Canvas), contentPadding = PaddingValues(16.dp)) {
        items(rules, key = { it.id }) { rule ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(rule.name, style = WiseText.Title, color = WiseColors.TextPrimary)
                    Text("${rule.matchField} · \"${rule.pattern}\"", style = WiseText.Meta, color = WiseColors.TextSecondary)
                }
                Switch(checked = rule.enabled, onCheckedChange = { viewModel.toggleEnabled(rule) })
            }
        }
    }
}
