package com.example.ledger.presentation.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ledger.ui.theme.WiseColors
import com.example.ledger.ui.theme.WiseText

/** README §3 "Transaction categorization" — list of default + custom categories, recolor/delete for custom ones. */
@Composable
fun CategoriesScreen(viewModel: CategoriesViewModel = hiltViewModel()) {
    val categories by viewModel.categories.collectAsState()

    LazyColumn(
        Modifier.fillMaxSize().background(WiseColors.Canvas),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(categories, key = { it.id }) { category ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                androidx.compose.foundation.layout.Box(
                    Modifier.size(14.dp).clip(CircleShape).background(runCatching { Color(android.graphics.Color.parseColor(category.colorHex)) }.getOrDefault(WiseColors.TextTertiary)),
                )
                Text(category.name, style = WiseText.Body, color = WiseColors.TextPrimary)
            }
        }
    }
}
