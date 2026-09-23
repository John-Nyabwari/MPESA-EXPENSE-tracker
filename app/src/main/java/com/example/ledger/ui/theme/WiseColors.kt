package com.example.ledger.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Ported verbatim from DESIGN-android.md §1 — this app's Wise-styled visual identity.
 * Semantic colors are repurposed for the ledger domain: Success=income, Error=expense,
 * Pending=Fuliza/overdraft activity (kept distinct from plain expense red per README's
 * "Orange for overdraft activity" transaction-list color guidance).
 */
object WiseColors {
    // Canvas & Surfaces
    val Canvas        = Color(0xFFFFFFFF)
    val Surface       = Color(0xFFF7F7F7)
    val SurfaceSunken = Color(0xFFEFEFEF)
    val Divider       = Color(0xFFE5E5E5)
    val Border        = Color(0xFFD2D2D2)

    // Text
    val TextPrimary   = Color(0xFF0E0F0C)
    val TextSecondary = Color(0xFF6B6F66)
    val TextTertiary  = Color(0xFF9A9D95)

    // Brand
    val Bright        = Color(0xFF9FE870)
    val BrightPressed = Color(0xFF8AD45C)
    val BrightTint    = Color(0xFFEAF9DC)
    val Forest        = Color(0xFF163300)
    val ForestHover   = Color(0xFF0E2200)

    // Semantic
    val Success = Color(0xFF2F8F4E) // income
    val Pending = Color(0xFFB5781E) // Fuliza / overdraft
    val Error   = Color(0xFFD4332B) // expense

    // Ledger-domain extras (README transaction-list color legend)
    val TransferBlue = Color(0xFF3AA0C9)
    val UnknownGray   = Color(0xFF9A9D95)
}
