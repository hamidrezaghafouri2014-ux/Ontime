
package com.androlearn.ot.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun OnTimeTheme(
    darkTheme: Boolean,
    appTheme: AppTheme,
    customPrimary: Color,
    customSecondary: Color,
    content: @Composable () -> Unit
) {
    val colors = when (appTheme) {
        AppTheme.EMERALD ->
            if (darkTheme) EmeraldDark else EmeraldLight

        AppTheme.VIOLET ->
            if (darkTheme) VioletDark else VioletLight

        AppTheme.OCEAN ->
            if (darkTheme) OceanDark else OceanLight

        AppTheme.CUSTOM -> {
            if (darkTheme) {
                EmeraldDark.copy(
                    primary = customPrimary,
                    secondary = customSecondary
                )
            } else {
                EmeraldLight.copy(
                    primary = customPrimary,
                    secondary = customSecondary
                )
            }
        }
    }

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.primary,
            onPrimary = Color.White,
            secondary = colors.secondary,
            onSecondary = Color.White,
            background = colors.background,
            onBackground = colors.text,
            surface = colors.surface,
            onSurface = colors.text,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.muted,
            error = colors.error
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            onPrimary = Color.White,
            secondary = colors.secondary,
            onSecondary = Color.White,
            background = colors.background,
            onBackground = colors.text,
            surface = colors.surface,
            onSurface = colors.text,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.muted,
            error = colors.error
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
