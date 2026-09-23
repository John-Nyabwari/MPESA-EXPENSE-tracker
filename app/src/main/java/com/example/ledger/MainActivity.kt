package com.example.ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ledger.presentation.navigation.LedgerNavGraph
import com.example.ledger.security.EncryptedSettingsStore
import com.example.ledger.ui.theme.LedgerTheme
import com.example.ledger.ui.theme.WiseColors
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsStore: EncryptedSettingsStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LedgerApp(startAtOnboarding = !hasCompletedOnboarding())
        }
    }

    // TODO: back by DataStore once onboarding flow use case lands (Phase 1 scaffolding).
    private fun hasCompletedOnboarding(): Boolean = false
}

@Composable
private fun LedgerApp(startAtOnboarding: Boolean) {
    LedgerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = WiseColors.Canvas) {
            LedgerNavGraph(startAtOnboarding = startAtOnboarding)
        }
    }
}
