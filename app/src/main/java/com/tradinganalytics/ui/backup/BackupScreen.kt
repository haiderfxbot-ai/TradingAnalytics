package com.tradinganalytics.ui.backup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tradinganalytics.core.theme.DarkThemeColors
import com.tradinganalytics.data.database.entities.BackupEntity
import com.tradinganalytics.ui.components.SkeletonCard

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    uiState: BackupUiState,
    onEvent: (BackupEvent) -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { onEvent(BackupEvent.Refresh) }
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Backup Management",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            if (uiState.isLoading) {
                BackupSkeleton()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        CreateBackupButton(
                            isCreating = uiState.isCreating,
                            onCreateClick = { onEvent(BackupEvent.CreateBackup) }
                        )
                    }

                    item {
                        AutoBackupSettings(
                            isEnabled = uiState.isAutoBackupEnabled,
                            schedule = uiState.backupSchedule,
                            onToggle = { onEvent(BackupEvent.ToggleAutoBackup(it)) },
                            onScheduleChange = { onEvent(BackupEvent.SetSchedule(it)) }
                        )
                    }

                    item {
                        ImportExportButtons(
                            onImportClick = { onEvent(BackupEvent.ImportBackup) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Backups (${uiState.backups.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkThemeColors.OnSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    if (uiState.backups.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Storage,
                                        contentDescription = null,
                                        tint = DarkThemeColors.OnSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No backups yet",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = DarkThemeColors.OnSurfaceVariant
                                    )
                                    Text(
                                        text = "Create your first backup to safeguard your data",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkThemeColors.OnSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    } else {
                        items(
                            items = uiState.backups,
                            key = { it.id }
                        ) { backup ->
                            BackupCard(
                                backup = backup,
                                onRestore = { onEvent(BackupEvent.RestoreBackup(backup)) },
                                onDelete = { onEvent(BackupEvent.ShowDeleteConfirm(backup.id)) },
                                onExport = { onEvent(BackupEvent.ExportBackup(backup)) }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = DarkThemeColors.SurfaceDark,
                contentColor = DarkThemeColors.Primary
            )
        }
    }

    if (uiState.deleteConfirmBackupId != null) {
        val backup = uiState.backups.find { it.id == uiState.deleteConfirmBackupId }
        if (backup != null) {
            AlertDialog(
                onDismissRequest = { onEvent(BackupEvent.DismissDeleteConfirm) },
                containerColor = DarkThemeColors.SurfaceDark,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Text(
                        text = "Delete Backup",
                        fontWeight = FontWeight.SemiBold,
                        color = DarkThemeColors.OnSurface
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete ${backup.fileName}? This action cannot be undone.",
                        color = DarkThemeColors.OnSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { onEvent(BackupEvent.ConfirmDelete(backup)) },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkThemeColors.Error)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(BackupEvent.DismissDeleteConfirm) }) {
                        Text("Cancel", color = DarkThemeColors.OnSurfaceVariant)
                    }
                }
            )
        }
    }
}

@Composable
private fun CreateBackupButton(isCreating: Boolean, onCreateClick: () -> Unit) {
    Button(
        onClick = onCreateClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isCreating,
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkThemeColors.Primary
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isCreating) "Creating Backup..." else "Create Backup",
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AutoBackupSettings(
    isEnabled: Boolean,
    schedule: String,
    onToggle: (Boolean) -> Unit,
    onScheduleChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkThemeColors.GlassBackground,
                        DarkThemeColors.GlassHighlight
                    )
                )
            )
            .border(0.5.dp, DarkThemeColors.GlassBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Auto Backup",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkThemeColors.OnSurface
                    )
                    Text(
                        text = "Automatically backup your data",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkThemeColors.OnSurfaceVariant
                    )
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DarkThemeColors.Primary,
                        checkedTrackColor = DarkThemeColors.Primary.copy(alpha = 0.3f)
                    )
                )
            }

            AnimatedVisibility(
                visible = isEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Schedule",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkThemeColors.OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ScheduleSelector(
                        selected = schedule,
                        onSelect = onScheduleChange
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleSelector(selected: String, onSelect: (String) -> Unit) {
    val options = listOf("daily" to "Daily", "weekly" to "Weekly", "monthly" to "Monthly")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        Box(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkThemeColors.SurfaceVariant)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clickable { expanded = true }
        ) {
            Text(
                text = options.first { it.first == selected }.second,
                color = DarkThemeColors.OnSurface
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = DarkThemeColors.SurfaceDark
        ) {
            options.forEach { (value, label) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            color = if (value == selected) DarkThemeColors.Primary else DarkThemeColors.OnSurface
                        )
                    },
                    onClick = {
                        onSelect(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ImportExportButtons(onImportClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onImportClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkThemeColors.Secondary),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkThemeColors.Secondary.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FileUpload,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Import", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun BackupCard(
    backup: BackupEntity,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
    onExport: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkThemeColors.GlassBackground,
                        DarkThemeColors.GlassHighlight
                    )
                )
            )
            .border(0.5.dp, DarkThemeColors.GlassBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = backup.fileName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkThemeColors.OnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = formatDate(backup.createdAt.time),
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkThemeColors.OnSurfaceVariant
                        )
                        Text(
                            text = formatSize(backup.fileSize),
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkThemeColors.OnSurfaceVariant
                        )
                        Text(
                            text = backup.backupType.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkThemeColors.Primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onRestore) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Restore",
                        tint = DarkThemeColors.Secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onExport) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Export",
                        tint = DarkThemeColors.OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = DarkThemeColors.Error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BackupSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonCard(height = 52.dp)
        SkeletonCard(height = 120.dp)
        SkeletonCard(height = 52.dp)
        repeat(4) {
            SkeletonCard(height = 100.dp)
        }
    }
}

private fun formatDate(millis: Long): String {
    val sdf = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.US)
    return sdf.format(java.util.Date(millis))
}

private fun formatSize(bytes: Long): String {
    return when {
        bytes >= 1_073_741_824 -> "%.2f GB".format(bytes / 1_073_741_824.0)
        bytes >= 1_048_576 -> "%.2f MB".format(bytes / 1_048_576.0)
        bytes >= 1_024 -> "%.2f KB".format(bytes / 1_024.0)
        else -> "$bytes B"
    }
}
