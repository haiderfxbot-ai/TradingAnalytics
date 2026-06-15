package com.tradinganalytics.ui.backup

import com.tradinganalytics.data.database.entities.BackupEntity

data class BackupUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val backups: List<BackupEntity> = emptyList(),
    val isAutoBackupEnabled: Boolean = false,
    val backupSchedule: String = "daily",
    val isCreating: Boolean = false,
    val isRestoring: Boolean = false,
    val deleteConfirmBackupId: Long? = null,
    val snackbarMessage: String? = null,
    val error: String? = null
)

sealed class BackupEvent {
    data object LoadBackups : BackupEvent()
    data object Refresh : BackupEvent()
    data object CreateBackup : BackupEvent()
    data class RestoreBackup(val backup: BackupEntity) : BackupEvent()
    data class ShowDeleteConfirm(val backupId: Long) : BackupEvent()
    data object DismissDeleteConfirm : BackupEvent()
    data class ConfirmDelete(val backup: BackupEntity) : BackupEvent()
    data class ToggleAutoBackup(val enabled: Boolean) : BackupEvent()
    data class SetSchedule(val schedule: String) : BackupEvent()
    data class ExportBackup(val backup: BackupEntity) : BackupEvent()
    data object ImportBackup : BackupEvent()
    data object DismissError : BackupEvent()
    data object ClearSnackbar : BackupEvent()
}
