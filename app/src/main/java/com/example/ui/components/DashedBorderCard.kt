package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCardBgTranslucent
import com.example.ui.theme.ElectricPink

@Composable
fun DashedBorderCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    strokeWidth: Dp = 2.dp,
    dashLength: Dp = 8.dp,
    gapLength: Dp = 6.dp,
    borderColor: Color = ElectricPink,
    backgroundColor: Color = CyberCardBgTranslucent,
    @DrawableRes backdropRes: Int? = null,
    backdropAlpha: Float = 0.65f,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val baseBg = if (backdropRes != null) Color(0x44080D1A) else backgroundColor

    Box(
        modifier = modifier
            // Outer neon glowing halo
            .drawBehind {
                val glowRadius = 3.dp.toPx()
                drawRoundRect(
                    color = borderColor.copy(alpha = 0.35f),
                    topLeft = androidx.compose.ui.geometry.Offset(-glowRadius, -glowRadius),
                    size = androidx.compose.ui.geometry.Size(
                        size.width + glowRadius * 2,
                        size.height + glowRadius * 2
                    ),
                    cornerRadius = CornerRadius((cornerRadius + 3.dp).toPx(), (cornerRadius + 3.dp).toPx())
                )

                // Crisp glowing dashed border
                val stroke = Stroke(
                    width = strokeWidth.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(dashLength.toPx(), gapLength.toPx()),
                        0f
                    )
                )
                val halfStroke = strokeWidth.toPx() / 2
                drawRoundRect(
                    color = borderColor,
                    topLeft = androidx.compose.ui.geometry.Offset(halfStroke, halfStroke),
                    size = androidx.compose.ui.geometry.Size(
                        size.width - strokeWidth.toPx(),
                        size.height - strokeWidth.toPx()
                    ),
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
                    style = stroke
                )
            }
            .clip(shape)
            .background(baseBg, shape)
    ) {
        if (backdropRes != null) {
            Image(
                painter = painterResource(id = backdropRes),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .alpha(backdropAlpha),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x55080D1A),
                                Color(0x77060A14),
                                Color(0xAA03050B)
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
