package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MrbColorScheme = darkColorScheme(
    primary = MrbGold,
    onPrimary = MrbTextDark,
    primaryContainer = MrbGoldDark,
    onPrimaryContainer = MrbTextWhite,
    secondary = MrbTextWhite,
    onSecondary = MrbTextDark,
    background = MrbBackground,
    onBackground = MrbTextWhite,
    surface = MrbCardBackground,
    onSurface = MrbTextWhite,
    surfaceVariant = MrbSurfaceVariant,
    onSurfaceVariant = MrbTextMuted,
    outline = MrbGoldOutline
)

@Composable
fun MrbTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MrbColorScheme,
        typography = Typography,
        content = content
    )
}

