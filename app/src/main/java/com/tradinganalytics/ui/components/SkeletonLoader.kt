package com.tradinganalytics.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tradinganalytics.core.theme.DarkThemeColors

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1200,
    shimmerColors: List<Color> = listOf(
        DarkThemeColors.SurfaceVariant,
        DarkThemeColors.CardElevated,
        DarkThemeColors.SurfaceVariant
    )
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation - widthOfShadowBrush, y = 0.0f),
        end = Offset(x = translateAnimation, y = angleOfAxisY)
    )

    Box(
        modifier = modifier.background(brush)
    )
}

@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    height: Dp = 120.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
    ) {
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}

@Composable
fun SkeletonText(
    modifier: Modifier = Modifier,
    widthFraction: Float = 0.7f,
    height: Dp = 16.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth(fraction = widthFraction)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
    ) {
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun SkeletonCircle(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .width(size)
            .height(size)
            .clip(CircleShape)
    ) {
        ShimmerEffect(
            modifier = Modifier
                .width(size)
                .height(size)
                .clip(CircleShape)
        )
    }
}

@Composable
fun SkeletonList(
    itemCount: Int = 5,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 72.dp,
    showAvatar: Boolean = true,
    showSubtitle: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(itemCount) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showAvatar) {
                    SkeletonCircle(size = 40.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    SkeletonText(widthFraction = 0.6f, height = 14.dp)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (showSubtitle) {
                        SkeletonText(widthFraction = 0.4f, height = 12.dp)
                    }
                }
            }
        }
    }
}
