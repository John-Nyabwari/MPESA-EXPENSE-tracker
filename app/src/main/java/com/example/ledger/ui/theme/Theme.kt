package com.example.ledger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Wise is light-first and brand-locked — Material You dynamic color is intentionally
// NOT wired up (see DESIGN-android.md §8: "do not enable dynamicLightColorScheme()").
private val WiseScheme = lightColorScheme(
    primary          = WiseColors.Bright,
    onPrimary        = WiseColors.Forest,
    primaryContainer = WiseColors.BrightTint,
    secondary        = WiseColors.Forest,
    onSecondary      = Color.White,
    background       = WiseColors.Canvas,
    onBackground     = WiseColors.TextPrimary,
    surface          = WiseColors.Canvas,
    onSurface        = WiseColors.TextPrimary,
    surfaceVariant   = WiseColors.Surface,
    outline          = WiseColors.Border,
    outlineVariant   = WiseColors.Divider,
    error            = WiseColors.Error,
)

@Composable
fun LedgerTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = WiseScheme, typography = WiseTypography, content = content)
