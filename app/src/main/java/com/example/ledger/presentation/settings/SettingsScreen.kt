package com.example.ledger.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ledger.ui.components.WiseForestButton
import com.example.ledger.ui.theme.WiseColors
import com.example.ledger.ui.theme.WiseText

/** README Security & Privacy: app lock, SMS authorization status, delete-all-data, export. */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    var appLock by remember { mutableStateOf(viewModel.appLockEnabled) }
    var confirmingDelete by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(WiseColors.Canvas).padding(16.dp)) {
        Text("Settings", style = WiseText.Section, color = WiseColors.TextPrimary)
        Row(
            Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("App lock", style = WiseText.Title, color = WiseColors.TextPrimary)
                Text("Require biometrics to open the app", style = WiseText.Meta, color = WiseColors.TextSecondary)
            }
            Switch(checked = appLock, onCheckedChange = { appLock = it; viewModel.appLockEnabled = it })
        }

        Text(
            if (viewModel.isSmsAuthorized()) "SMS access: authorized" else "SMS access: not authorized — grant access to import transactions",
            style = WiseText.Body,
            color = if (viewModel.isSmsAuthorized()) WiseColors.Success else WiseColors.Error,
            modifier = Modifier.padding(vertical = 8.dp),
        )

        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 16.dp))

        if (!confirmingDelete) {
            WiseForestButton(text = "Delete all data", onClick = { confirmingDelete = true }, modifier = Modifier.fillMaxWidth())
        } else {
            Text("This permanently deletes every transaction, raw SMS, category, and rule. This cannot be undone.", style = WiseText.Body, color = WiseColors.Error, modifier = Modifier.padding(bottom = 8.dp))
            WiseForestButton(
                text = "Confirm delete",
                onClick = { viewModel.deleteAllData { confirmingDelete = false } },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
