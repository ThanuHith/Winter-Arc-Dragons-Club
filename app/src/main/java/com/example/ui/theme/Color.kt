package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Obsidian & Dark Space
val ObsidianVoid = Color(0xFF050811)
val CyberCardBg = Color(0xF00B132B)
val CyberCardBgTranslucent = Color(0xCC0D1730)
val CyberInputBg = Color(0x99111D3B)
val CyberBorderStroke = Color(0x3300F0FF)

// Dragon Cyberpunk Neon Highlights
val DragonGreen = Color(0xFF00E676)
val ElectricPink = Color(0xFFFF007F)
val CyberCyan = Color(0xFF00F0FF)
val NeonYellow = Color(0xFFFFEE00)
val NeonPurple = Color(0xFFA855F7)
val NeonOrange = Color(0xFFFF6D00)

// Text Colors
val TextWhite = Color(0xFFF8FAFC)
val TextMuted = Color(0xFF94A3B8)
val TextDark = Color(0xFF64748B)

// Gradients
val NeonMultiGradient = Brush.horizontalGradient(
    listOf(ElectricPink, NeonPurple, CyberCyan, DragonGreen)
)

val DragonGlowGradient = Brush.linearGradient(
    listOf(DragonGreen, CyberCyan)
)

val PinkPurpleGradient = Brush.horizontalGradient(
    listOf(ElectricPink, NeonPurple)
)

val CardGlowBorder = Brush.linearGradient(
    listOf(
        Color(0xFF00F0FF), // Cyber Cyan
        Color(0xFF00E676), // Dragon Green
        Color(0xFFFF007F), // Electric Pink
        Color(0xFFA855F7)  // Neon Purple
    )
)

val MorningGlowBorder = Brush.linearGradient(
    listOf(Color(0xFFFF9100), Color(0xFFFFEA00), Color(0xFF00F0FF))
)

val AfternoonGlowBorder = Brush.linearGradient(
    listOf(Color(0xFF00F0FF), Color(0xFFFF007F))
)

val EveningGlowBorder = Brush.linearGradient(
    listOf(Color(0xFFA855F7), Color(0xFF00F0FF))
)

val NightGlowBorder = Brush.linearGradient(
    listOf(Color(0xFF00F0FF), Color(0xFF3B82F6))
)

val ProgressGlowBorder = Brush.linearGradient(
    listOf(Color(0xFF00E676), Color(0xFF00F0FF))
)

