package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PulseHabitColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2E1065),
    onPrimaryContainer = Color(0xFFE9D5FF),
    secondary = NeonCyan,
    onSecondary = Color(0xFF082F49),
    secondaryContainer = Color(0xFF0E7490),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = NeonEmerald,
    onTertiary = Color(0xFF022C22),
    background = BgMain,
    onBackground = TextPrimary,
    surface = BgCard,
    onSurface = TextPrimary,
    surfaceVariant = BgCardHover,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    outlineVariant = BorderHighlight
)

@Composable
fun PulseHabitTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PulseHabitColorScheme,
        typography = Typography,
        content = content
    )
}
