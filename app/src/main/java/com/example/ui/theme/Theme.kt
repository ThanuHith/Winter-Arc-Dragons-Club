package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WinterArcColorScheme = darkColorScheme(
    primary = DragonGreen,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003817),
    onPrimaryContainer = DragonGreen,
    secondary = CyberCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF00363A),
    onSecondaryContainer = CyberCyan,
    tertiary = ElectricPink,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF3E001B),
    onTertiaryContainer = ElectricPink,
    background = ObsidianVoid,
    onBackground = TextWhite,
    surface = CyberCardBg,
    onSurface = TextWhite,
    surfaceVariant = CyberCardBgTranslucent,
    onSurfaceVariant = TextMuted,
    outline = CyberBorderStroke,
    outlineVariant = Color(0x22FFFFFF)
)

@Composable
fun WinterArcTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WinterArcColorScheme,
        typography = Typography,
        content = content
    )
}
