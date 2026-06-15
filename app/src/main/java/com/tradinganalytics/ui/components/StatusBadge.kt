package com.tradinganalytics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tradinganalytics.core.theme.DarkThemeColors

enum class BadgeType { SUCCESS, WARNING, ERROR, INFO }

enum class BadgeSize { SMALL, MEDIUM, LARGE }

data class BadgeColors(
    val background: Color,
    val content: Color,
    val icon: ImageVector
)

private fun badgeColors(type: BadgeType): BadgeColors = when (type) {
    BadgeType.SUCCESS -> BadgeColors(
        background = DarkThemeColors.Success.copy(alpha = 0.15f),
        content = DarkThemeColors.Success,
        icon = Icons.Default.CheckCircle
    )
    BadgeType.WARNING -> BadgeColors(
        background = DarkThemeColors.Warning.copy(alpha = 0.15f),
        content = DarkThemeColors.Warning,
        icon = Icons.Default.Warning
    )
    BadgeType.ERROR -> BadgeColors(
        background = DarkThemeColors.Error.copy(alpha = 0.15f),
        content = DarkThemeColors.Error,
        icon = Icons.Default.Error
    )
    BadgeType.INFO -> BadgeColors(
        background = DarkThemeColors.Info.copy(alpha = 0.15f),
        content = DarkThemeColors.Info,
        icon = Icons.Default.Info
    )
}

private fun badgePadding(size: BadgeSize): Dp = when (size) {
    BadgeSize.SMALL -> 4.dp
    BadgeSize.MEDIUM -> 6.dp
    BadgeSize.LARGE -> 8.dp
}

@Composable
private fun badgeFontSize(size: BadgeSize) = when (size) {
    BadgeSize.SMALL -> MaterialTheme.typography.labelSmall
    BadgeSize.MEDIUM -> MaterialTheme.typography.labelMedium
    BadgeSize.LARGE -> MaterialTheme.typography.labelLarge
}

private fun badgeCornerRadius(size: BadgeSize): Dp = when (size) {
    BadgeSize.SMALL -> 4.dp
    BadgeSize.MEDIUM -> 6.dp
    BadgeSize.LARGE -> 8.dp
}

private fun iconSize(size: BadgeSize): Dp = when (size) {
    BadgeSize.SMALL -> 12.dp
    BadgeSize.MEDIUM -> 14.dp
    BadgeSize.LARGE -> 18.dp
}

@Composable
fun StatusBadge(
    text: String,
    type: BadgeType = BadgeType.INFO,
    size: BadgeSize = BadgeSize.MEDIUM,
    showIcon: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = badgeColors(type)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(badgeCornerRadius(size)))
            .background(colors.background)
            .padding(
                horizontal = badgePadding(size),
                vertical = badgePadding(size) / 2
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (showIcon) {
            Icon(
                imageVector = colors.icon,
                contentDescription = null,
                tint = colors.content,
                modifier = Modifier.size(iconSize(size))
            )
            Spacer(modifier = Modifier.width(badgePadding(size) / 2))
        }
        Text(
            text = text,
            style = badgeFontSize(size),
            fontWeight = FontWeight.Medium,
            color = colors.content
        )
    }
}
