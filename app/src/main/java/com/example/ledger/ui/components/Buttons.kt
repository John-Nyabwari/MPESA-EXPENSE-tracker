package com.example.ledger.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.ledger.ui.theme.WiseColors
import com.example.ledger.ui.theme.WiseText

/** Primary CTA — ported from DESIGN-android.md §3. Forest text on bright green, never white. */
@Composable
fun WisePrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, label = "ctaScale")
    val haptics = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (pressed) WiseColors.BrightPressed else WiseColors.Bright)
            .heightIn(min = 52.dp)
            .clickable(interaction, indication = null) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = WiseText.Button, color = WiseColors.Forest)
    }
}

/** Secondary CTA — forest fill, white text. Ported from DESIGN-android.md §3. */
@Composable
fun WiseForestButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    Box(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (pressed) WiseColors.ForestHover else WiseColors.Forest)
            .heightIn(min = 52.dp)
            .clickable(interaction, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = WiseText.Button, color = androidx.compose.ui.graphics.Color.White)
    }
}
