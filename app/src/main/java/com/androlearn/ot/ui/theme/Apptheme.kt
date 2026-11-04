package com.androlearn.ot.ui.theme

import androidx.compose.ui.graphics.Color

data class ThemeColors(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val text: Color,
    val muted: Color,
    val error: Color
)

enum class AppTheme {
    EMERALD,
    VIOLET,
    OCEAN,
    CUSTOM
}