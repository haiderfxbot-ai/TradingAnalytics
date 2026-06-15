package com.tradinganalytics.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tradinganalytics.core.theme.DarkThemeColors
import kotlinx.coroutines.delay

data class NotificationStyle(
    val icon: ImageVector,
    val backgroundColor: Color,
    val borderColor: Color,
    val iconColor: Color,
    val titleColor: Color,
    val messageColor: Color
)

object NotificationStyles {
    val Success = NotificationStyle(
        icon = Icons.Default.CheckCircle,
        backgroundColor = DarkThemeColors.Success.copy(alpha = 0.1f),
        borderColor = DarkThemeColors.Success.copy(alpha = 0.3f),
        iconColor = DarkThemeColors.Success,
        titleColor = DarkThemeColors.Success,
        messageColor = DarkThemeColors.OnSurface
    )

    val Error = NotificationStyle(
        icon = Icons.Default.Error,
        backgroundColor = DarkThemeColors.Error.copy(alpha = 0.1f),
        borderColor = DarkThemeColors.Error.copy(alpha = 0.3f),
        iconColor = DarkThemeColors.Error,
        titleColor = DarkThemeColors.Error,
        messageColor = DarkThemeColors.OnSurface
    )

    val Warning = NotificationStyle(
        icon = Icons.Default.Warning,
        backgroundColor = DarkThemeColors.Warning.copy(alpha = 0.1f),
        borderColor = DarkThemeColors.Warning.copy(alpha = 0.3f),
        iconColor = DarkThemeColors.Warning,
        titleColor = DarkThemeColors.Warning,
        messageColor = DarkThemeColors.OnSurface
    )

    val Info = NotificationStyle(
        icon = Icons.Default.Info,
        backgroundColor = DarkThemeColors.Info.copy(alpha = 0.1f),
        borderColor = DarkThemeColors.Info.copy(alpha = 0.3f),
        iconColor = DarkThemeColors.Info,
        titleColor = DarkThemeColors.Info,
        messageColor = DarkThemeColors.OnSurface
    )
}

enum class NotificationType { SUCCESS, ERROR, WARNING, INFO }

private fun notificationStyle(type: NotificationType): NotificationStyle = when (type) {
    NotificationType.SUCCESS -> NotificationStyles.Success
    NotificationType.ERROR -> NotificationStyles.Error
    NotificationType.WARNING -> NotificationStyles.Warning
    NotificationType.INFO -> NotificationStyles.Info
}

enum class SnackbarPosition { TOP, BOTTOM }

@Composable
fun NotificationSnackbar(
    visible: Boolean,
    message: String,
    type: NotificationType = NotificationType.INFO,
    title: String? = null,
    autoDismissMs: Long = 3000L,
    position: SnackbarPosition = SnackbarPosition.TOP,
    onDismiss: () -> Unit = {},
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val style = notificationStyle(type)

    if (autoDismissMs > 0) {
        LaunchedEffect(visible) {
            if (visible) {
                delay(autoDismissMs)
                onDismiss()
            }
        }
    }

    val slideIn = slideInVertically(
        initialOffsetY = { if (position == SnackbarPosition.TOP) -it else it }
    )
    val slideOut = slideOutVertically(
        targetOffsetY = { if (position == SnackbarPosition.TOP) -it else it }
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (position == SnackbarPosition.TOP) Alignment.TopCenter else Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideIn,
            exit = slideOut
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(style.backgroundColor)
                    .border(
                        width = 0.5.dp,
                        color = style.borderColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = style.icon,
                        contentDescription = null,
                        tint = style.iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        if (title != null) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = style.titleColor
                            )
                        }
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = style.messageColor
                        )
                        if (onAction != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Action",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = style.iconColor
                            )
                        }
                    }
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = DarkThemeColors.OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
