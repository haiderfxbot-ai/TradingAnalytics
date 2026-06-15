package com.tradinganalytics.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.tradinganalytics.core.theme.DarkThemeColors

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

object BottomNavItems {
    val userItems = listOf(
        BottomNavItem("Dashboard", "dashboard", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Analytics", "analytics", Icons.Filled.Analytics, Icons.Outlined.Analytics),
        BottomNavItem("History", "history", Icons.Filled.History, Icons.Outlined.History),
        BottomNavItem("Settings", "settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    val adminItems = listOf(
        BottomNavItem("Dashboard", "dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
        BottomNavItem("Admin", "admin_dashboard", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings),
        BottomNavItem("Users", "user_management", Icons.Filled.Group, Icons.Outlined.Group),
        BottomNavItem("Analytics", "analytics", Icons.Filled.Analytics, Icons.Outlined.Analytics),
        BottomNavItem("Backup", "backup_management", Icons.Filled.Backup, Icons.Outlined.Backup)
    )
}

@Composable
fun AppBottomBar(
    currentRoute: String?,
    isAdmin: Boolean,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = if (isAdmin) BottomNavItems.adminItems else BottomNavItems.userItems

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(DarkThemeColors.GlassBackground)
            .border(
                width = 0.5.dp,
                color = DarkThemeColors.GlassBorder,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) DarkThemeColors.Primary else DarkThemeColors.OnSurfaceVariant,
                animationSpec = tween(200),
                label = "navColor"
            )
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) DarkThemeColors.Primary.copy(alpha = 0.15f) else Color.Transparent,
                animationSpec = tween(200),
                label = "navBg"
            )

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .clickable { onItemSelected(item.route) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.label,
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor
                )
            }
        }
    }
}
