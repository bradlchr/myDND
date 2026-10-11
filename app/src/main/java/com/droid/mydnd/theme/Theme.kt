package com.droid.mydnd.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DndColorScheme = darkColorScheme(
    primary = DndGoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = DndGoldDark,
    onPrimaryContainer = DndGoldLight,
    secondary = DndCrimsonLight,
    onSecondary = Color.White,
    secondaryContainer = DndCrimsonSecondary,
    onSecondaryContainer = Color.White,
    tertiary = DndArcaneCyan,
    onTertiary = Color.Black,
    background = DndObsidianBg,
    onBackground = DndTextPrimary,
    surface = DndCardSurface,
    onSurface = DndTextPrimary,
    surfaceVariant = DndCardSurfaceVariant,
    onSurfaceVariant = DndTextSecondary,
    outline = DndBorder,
    error = DndDamageRed,
    onError = Color.White
)

@Composable
fun MyDNDTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DndColorScheme,
        typography = Typography,
        content = content
    )
}
