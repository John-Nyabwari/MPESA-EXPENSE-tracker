package com.example.ledger.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ledger.ui.components.WisePrimaryButton
import com.example.ledger.ui.theme.WiseColors
import com.example.ledger.ui.theme.WiseText

enum class ImportChoice(val label: String) {
    ALL("Import all available SMS"),
    LAST_30("Last 30 days"),
    LAST_90("Last 90 days"),
    LAST_180("Last 180 days"),
    LAST_365("Last 365 days"),
    SKIP("Skip — track only future messages"),
}

/**
 * README "Date-based history import" flow, steps 1–3: explain why SMS access is needed,
 * request authorization (handled by the caller via SmsPermissionManager + the system
 * permission dialog / default-SMS-app flow — not shown here), then let the user pick a
 * starting point. Step 4 onward (scan + progress + report) is driven by SmsImportWorker
 * in MODE_HISTORICAL, wired up once this screen calls WorkManager with the chosen cutoff.
 */
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var choice by remember { mutableStateOf(ImportChoice.LAST_90) }

    Column(
        Modifier.fillMaxSize().background(WiseColors.Canvas).padding(24.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text("Welcome", style = WiseText.TitleLarge, color = WiseColors.TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(
            "We read financial SMS from M-Pesa and your banks to build your transaction ledger, " +
                "entirely on this device. Nothing is uploaded unless you explicitly export it.",
            style = WiseText.Body,
            color = WiseColors.TextSecondary,
        )
        Spacer(Modifier.height(24.dp))
        Text("Start importing from", style = WiseText.Subsection, color = WiseColors.TextPrimary)
        Spacer(Modifier.height(8.dp))

        ImportChoice.entries.forEach { option ->
            androidx.compose.foundation.layout.Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .selectable(selected = choice == option, onClick = { choice = option }),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = choice == option, onClick = { choice = option })
                Text(option.label, style = WiseText.Body, color = WiseColors.TextPrimary)
            }
        }

        Spacer(Modifier.height(24.dp))
        WisePrimaryButton(text = "Continue", onClick = onFinished, modifier = Modifier.fillMaxWidth())
    }
}
