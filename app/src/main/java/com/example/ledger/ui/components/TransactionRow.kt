package com.example.ledger.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.ledger.domain.model.ReviewStatus
import com.example.ledger.domain.model.Transaction
import com.example.ledger.domain.model.TransactionType
import com.example.ledger.ui.theme.WiseColors
import com.example.ledger.ui.theme.WiseText
import com.example.ledger.util.CurrencyFormatter
import com.example.ledger.util.MaskingUtils

/**
 * Ledger transaction row, styled on Wise's line-item pattern (DESIGN-android.md §3's
 * currency-row Composable). Color legend follows README's Dashboard section: green=income,
 * red=expense, blue=transfer, orange=Fuliza/overdraft, gray=unreviewed/unknown.
 */
@Composable
fun TransactionRow(transaction: Transaction, categoryName: String?, onClick: () -> Unit) {
    val (amountColor, dotColor) = when {
        transaction.isFuliza -> WiseColors.Pending to WiseColors.Pending
        transaction.type == TransactionType.INCOME || transaction.type == TransactionType.TRANSFER_IN -> WiseColors.Success to WiseColors.Success
        transaction.type == TransactionType.TRANSFER_OUT -> WiseColors.TransferBlue to WiseColors.TransferBlue
        transaction.type == TransactionType.UNKNOWN -> WiseColors.UnknownGray to WiseColors.UnknownGray
        else -> WiseColors.Error to WiseColors.Error
    }
    val isCredit = transaction.type == TransactionType.INCOME || transaction.type == TransactionType.TRANSFER_IN

    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .then(Modifier),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.foundation.layout.Box(
                Modifier.size(8.dp).clip(CircleShape).background(dotColor),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    transaction.counterparty?.takeIf { it.isNotBlank() } ?: "Unknown",
                    style = WiseText.Title,
                )
                Spacer(Modifier.padding(top = 2.dp))
                Text(
                    categoryName ?: (if (transaction.reviewStatus == ReviewStatus.NEEDS_REVIEW) "Needs review" else "Uncategorized"),
                    style = WiseText.Meta,
                    color = WiseColors.TextSecondary,
                )
            }
            Text(
                (if (isCredit) "+" else "-") + CurrencyFormatter.format(transaction.amountMinor),
                style = WiseText.Amount,
                color = amountColor,
            )
        }
        Divider(color = WiseColors.Divider, thickness = 1.dp, modifier = Modifier.padding(start = 36.dp))
    }
}
