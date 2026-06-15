package com.tradinganalytics.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tradinganalytics.core.theme.DarkThemeColors
import com.tradinganalytics.core.theme.StatusColors

@Composable
fun LinearProgressWithLabel(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier,
    trackColor: Color = DarkThemeColors.SurfaceVariant,
    progressBrush: Brush = StatusColors.primaryGradient,
    height: Dp = 8.dp,
    showPercentage: Boolean = true,
    animDuration: Int = 800
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(animDuration),
        label = "linearProgress"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = DarkThemeColors.OnSurfaceVariant
            )
            if (showPercentage) {
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkThemeColors.OnSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress.coerceIn(0f, 1f))
                    .matchParentSize()
                    .clip(RoundedCornerShape(height / 2))
                    .background(progressBrush)
            )
        }
    }
}

@Composable
fun CircularProgressWithPercentage(
    percentage: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 10.dp,
    trackColor: Color = DarkThemeColors.SurfaceVariant,
    progressBrush: Brush = StatusColors.primaryGradient,
    animDuration: Int = 800
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 100f),
        animationSpec = tween(animDuration),
        label = "circularProgress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val sweepAngle = (animatedPercentage / 100f) * 360f
            val diameter = size.toPx() - strokeWidth.toPx()
            val topLeft = Offset(strokeWidth.toPx() / 2f, strokeWidth.toPx() / 2f)

            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(trackColor, trackColor),
                    center = Offset(diameter / 2 + topLeft.x, diameter / 2 + topLeft.y)
                ),
                radius = diameter / 2 + strokeWidth.toPx() / 2,
                center = Offset(size.toPx() / 2, size.toPx() / 2),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                brush = progressBrush,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${animatedPercentage.toInt()}%",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DarkThemeColors.OnSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GoalProgress(
    current: Double,
    target: Double,
    label: String,
    modifier: Modifier = Modifier,
    progressBrush: Brush = StatusColors.primaryGradient,
    formatValue: (Double) -> String = { "$${String.format("%.2f", it)}" }
) {
    val progress = if (target > 0) (current / target).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800),
        label = "goalProgress"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = DarkThemeColors.OnSurface
            )
            Text(
                text = "${formatValue(current)} / ${formatValue(target)}",
                style = MaterialTheme.typography.labelSmall,
                color = DarkThemeColors.OnSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkThemeColors.SurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress.coerceIn(0f, 1f))
                    .matchParentSize()
                    .clip(RoundedCornerShape(6.dp))
                    .background(progressBrush)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = DarkThemeColors.Primary
            )
            if (progress >= 1f) {
                Text(
                    text = "Goal Reached!",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkThemeColors.Success
                )
            } else {
                val remaining = target - current
                Text(
                    text = "${formatValue(remaining)} remaining",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkThemeColors.OnSurfaceVariant
                )
            }
        }
    }
}
