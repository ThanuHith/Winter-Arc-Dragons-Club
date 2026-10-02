package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CardGlowBorder
import com.example.ui.theme.CyberCardBgTranslucent

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    hasGradientGlow: Boolean = true,
    glowBrush: Brush? = null,
    borderWidth: Dp = 1.5.dp,
    backgroundColor: Color = CyberCardBgTranslucent,
    @DrawableRes backdropRes: Int? = null,
    backdropAlpha: Float = 0.65f,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val effectiveGlowBrush = glowBrush ?: if (hasGradientGlow) {
        CardGlowBorder
    } else {
        Brush.linearGradient(
            listOf(
                Color(0x9900F0FF),
                Color(0x6600E676)
            )
        )
    }

    val baseBg = if (backdropRes != null) Color(0x44080D1A) else backgroundColor

    Box(
        modifier = modifier
            // Outer neon glowing aura / halo
            .drawBehind {
                val glowRadius = 3.dp.toPx()
                drawRoundRect(
                    brush = effectiveGlowBrush,
                    topLeft = androidx.compose.ui.geometry.Offset(-glowRadius, -glowRadius),
                    size = androidx.compose.ui.geometry.Size(
                        size.width + glowRadius * 2,
                        size.height + glowRadius * 2
                    ),
                    cornerRadius = CornerRadius((cornerRadius + 3.dp).toPx(), (cornerRadius + 3.dp).toPx()),
                    alpha = 0.32f
                )
            }
            .clip(shape)
            .background(baseBg, shape)
            .border(width = borderWidth, brush = effectiveGlowBrush, shape = shape)
    ) {
        if (backdropRes != null) {
            // Vibrant graffiti artwork backdrop
            Image(
                painter = painterResource(id = backdropRes),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .alpha(backdropAlpha),
                contentScale = ContentScale.Crop
            )

            // Balanced obsidian gradient scrim that keeps graffiti colors vivid while maintaining sharp text readability
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x55080D1A), // ~33% dark overlay
                                Color(0x77060A14), // ~47% dark overlay
                                Color(0xAA03050B)  // ~67% dark overlay
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
