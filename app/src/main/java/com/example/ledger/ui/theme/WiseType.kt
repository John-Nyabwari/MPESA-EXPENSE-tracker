package com.example.ledger.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Wise Sans is proprietary — falls back to the system font family (Roboto). Drop TTFs in
// res/font/ and swap FontFamily.Default for a real FontFamily(...) to match brand exactly.
private val WiseSans = FontFamily.Default
private const val TNUM = "tnum"

object WiseText {
    val Balance    = TextStyle(WiseSans, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp, lineHeight = 42.sp, letterSpacing = (-0.6).sp, fontFeatureSettings = TNUM)
    val TitleLarge = TextStyle(WiseSans, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, lineHeight = 35.sp, letterSpacing = (-0.5).sp)
    val Section    = TextStyle(WiseSans, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, lineHeight = 26.sp, letterSpacing = (-0.3).sp)
    val Currency   = TextStyle(WiseSans, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, lineHeight = 24.sp, letterSpacing = (-0.2).sp, fontFeatureSettings = TNUM)
    val Subsection = TextStyle(WiseSans, fontWeight = FontWeight.SemiBold,  fontSize = 18.sp, lineHeight = 23.sp, letterSpacing = (-0.1).sp)
    val Amount     = TextStyle(WiseSans, fontWeight = FontWeight.SemiBold,  fontSize = 16.sp, lineHeight = 19.sp, fontFeatureSettings = TNUM)
    val Title      = TextStyle(WiseSans, fontWeight = FontWeight.SemiBold,  fontSize = 16.sp, lineHeight = 21.sp)
    val Body       = TextStyle(WiseSans, fontWeight = FontWeight.Normal,    fontSize = 15.sp, lineHeight = 22.sp)
    val Button     = TextStyle(WiseSans, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, lineHeight = 20.sp)
    val Meta       = TextStyle(WiseSans, fontWeight = FontWeight.Normal,    fontSize = 13.sp, lineHeight = 17.sp)
    val LabelUpper = TextStyle(WiseSans, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, lineHeight = 13.sp, letterSpacing = 0.6.sp)
    val Tab        = TextStyle(WiseSans, fontWeight = FontWeight.SemiBold,  fontSize = 11.sp, lineHeight = 13.sp, letterSpacing = 0.1.sp)
    val Caption    = TextStyle(WiseSans, fontWeight = FontWeight.Normal,    fontSize = 11.sp, lineHeight = 14.sp)
}

val WiseTypography = Typography(
    headlineLarge = WiseText.TitleLarge,
    headlineSmall = WiseText.Section,
    titleMedium   = WiseText.Title,
    bodyMedium    = WiseText.Body,
    labelSmall    = WiseText.Tab,
)
