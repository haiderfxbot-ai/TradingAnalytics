package com.tradinganalytics.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tradinganalytics.core.theme.DarkThemeColors
import com.tradinganalytics.core.theme.GradientDefs
import com.tradinganalytics.core.theme.StatusColors
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToUserManagement: () -> Unit = {},
    onNavigateToSystemSettings: () -> Unit = {},
    onNavigateToBackupManagement: () -> Unit = {},
    onNavigateToDataImportExport: () -> Unit = {},
    onNavigateToSecuritySettings: () -> Unit = {},
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { nav ->
            when (nav) {
                AdminNavigation.NavigateToUserManagement -> onNavigateToUserManagement()
                AdminNavigation.NavigateToSystemSettings -> onNavigateToSystemSettings()
                AdminNavigation.NavigateToBackupManagement -> onNavigateToBackupManagement()
                AdminNavigation.NavigateToDataImportExport -> onNavigateToDataImportExport()
                AdminNavigation.NavigateToSecuritySettings -> onNavigateToSecuritySettings()
            }
        }
    }

    val isDark = MaterialTheme.colorScheme.background == DarkThemeColors.PremiumBackground
    val bgGradient = if (isDark) GradientDefs.backgroundDark else GradientDefs.backgroundLight

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Admin Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(padding)
        ) {
            if (state.isLoading && state.users.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    item {
                        StatsOverviewRow(state)
                    }

                    item {
                        Text(
                            text = "Management",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    item {
                        ManagementCard(
                            icon = Icons.Default.Group,
                            title = "User Management",
                            subtitle = "${state.totalUsers} users, ${state.activeSessions} active",
                            gradient = StatusColors.primaryGradient,
                            onClick = {
                                viewModel.onEvent(AdminEvent.LoadUsers)
                                onNavigateToUserManagement()
                            }
                        )
                    }

                    item {
                        ManagementCard(
                            icon = Icons.Default.Settings,
                            title = "System Settings",
                            subtitle = "Configure application preferences",
                            gradient = StatusColors.infoGradient,
                            onClick = onNavigateToSystemSettings
                        )
                    }

                    item {
                        ManagementCard(
                            icon = Icons.Default.Backup,
                            title = "Backup Management",
                            subtitle = "${state.backupList.size} backups available",
                            gradient = StatusColors.warningGradient,
                            onClick = onNavigateToBackupManagement
                        )
                    }

                    item {
                        ManagementCard(
                            icon = Icons.Default.FileDownload,
                            title = "Data Import/Export",
                            subtitle = "Import or export trading data",
                            gradient = StatusColors.successGradient,
                            onClick = onNavigateToDataImportExport
                        )
                    }

                    item {
                        ManagementCard(
                            icon = Icons.Default.Security,
                            title = "Security Settings",
                            subtitle = "Manage security policies",
                            gradient = StatusColors.errorGradient,
                            onClick = onNavigateToSecuritySettings
                        )
                    }

                    item {
                        ManagementCard(
                            icon = Icons.Default.History,
                            title = "Login History",
                            subtitle = "View user login activity",
                            gradient = StatusColors.premiumGradient,
                            onClick = {
                                if (state.users.isNotEmpty()) {
                                    viewModel.onEvent(
                                        AdminEvent.ShowLoginHistory(state.users.first().username)
                                    )
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsOverviewRow(state: AdminUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            label = "Total Users",
            value = state.totalUsers.toString(),
            gradient = StatusColors.primaryGradient,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Active",
            value = state.activeSessions.toString(),
            gradient = StatusColors.successGradient,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Locked",
            value = state.lockedAccounts.toString(),
            gradient = StatusColors.errorGradient,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Disabled",
            value = state.disabledAccounts.toString(),
            gradient = StatusColors.warningGradient,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    gradient: Brush,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient, RoundedCornerShape(16.dp), alpha = 0.15f)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ManagementCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradient: Brush,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x1AFFFFFF),
                            Color(0x08FFFFFF)
                        )
                    ),
                    RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(gradient, RoundedCornerShape(12.dp), alpha = 0.2f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
