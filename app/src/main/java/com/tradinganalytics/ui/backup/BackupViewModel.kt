package com.tradinganalytics.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradinganalytics.data.preferences.AppPreferences
import com.tradinganalytics.data.repository.BackupRepository
import com.tradinganalytics.storage.StorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val backupManager: BackupManager,
    private val appPreferences: AppPreferences,
    private val storageManager: StorageManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                backupRepository.getAll().collect { backups ->
                    val autoBackup = appPreferences.isAutoBackupEnabled().first()
                    val schedule = appPreferences.getBackupSchedule().first()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            backups = backups,
                            isAutoBackupEnabled = autoBackup,
                            backupSchedule = schedule
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load backups")
                }
            }
        }
    }

    fun onEvent(event: BackupEvent) {
        when (event) {
            is BackupEvent.LoadBackups -> loadData()
            is BackupEvent.Refresh -> refresh()
            is BackupEvent.CreateBackup -> createBackup()
            is BackupEvent.RestoreBackup -> restoreBackup(event.backup)
            is BackupEvent.ShowDeleteConfirm -> _uiState.update { it.copy(deleteConfirmBackupId = event.backupId) }
            is BackupEvent.DismissDeleteConfirm -> _uiState.update { it.copy(deleteConfirmBackupId = null) }
            is BackupEvent.ConfirmDelete -> deleteBackup(event.backup)
            is BackupEvent.ToggleAutoBackup -> toggleAutoBackup(event.enabled)
            is BackupEvent.SetSchedule -> setSchedule(event.schedule)
            is BackupEvent.ExportBackup -> exportBackup(event.backup)
            is BackupEvent.ImportBackup -> importBackup()
            is BackupEvent.DismissError -> _uiState.update { it.copy(error = null) }
            is BackupEvent.ClearSnackbar -> _uiState.update { it.copy(snackbarMessage = null) }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                val backups = backupRepository.getAll().first()
                _uiState.update { it.copy(isRefreshing = false, backups = backups) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isRefreshing = false, error = e.message ?: "Refresh failed")
                }
            }
        }
    }

    private fun createBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true) }
            try {
                val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val fileName = "backup_$dateStr.db"
                val result = backupManager.createBackup(fileName, "full")
                result.onSuccess { backup ->
                    backupRepository.insert(backup)
                    _uiState.update {
                        it.copy(isCreating = false, snackbarMessage = "Backup created successfully")
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(isCreating = false, error = e.message ?: "Failed to create backup")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isCreating = false, error = e.message ?: "Failed to create backup")
                }
            }
        }
    }

    private fun restoreBackup(backup: com.tradinganalytics.data.database.entities.BackupEntity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRestoring = true) }
            try {
                backupManager.restoreBackup(backup)
                    .onSuccess {
                        _uiState.update {
                            it.copy(isRestoring = false, snackbarMessage = "Backup restored successfully")
                        }
                    }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(isRestoring = false, error = e.message ?: "Failed to restore backup")
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isRestoring = false, error = e.message ?: "Failed to restore backup")
                }
            }
        }
    }

    private fun deleteBackup(backup: com.tradinganalytics.data.database.entities.BackupEntity) {
        viewModelScope.launch {
            try {
                backupManager.deleteBackupFile(backup)
                backupRepository.delete(backup)
                _uiState.update {
                    it.copy(deleteConfirmBackupId = null, snackbarMessage = "Backup deleted")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(deleteConfirmBackupId = null, error = e.message ?: "Failed to delete backup")
                }
            }
        }
    }

    private fun toggleAutoBackup(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setAutoBackupEnabled(enabled)
            _uiState.update { it.copy(isAutoBackupEnabled = enabled) }
        }
    }

    private fun setSchedule(schedule: String) {
        viewModelScope.launch {
            appPreferences.setBackupSchedule(schedule)
            _uiState.update { it.copy(backupSchedule = schedule) }
        }
    }

    private fun exportBackup(backup: com.tradinganalytics.data.database.entities.BackupEntity) {
        viewModelScope.launch {
            try {
                val source = java.io.File(backup.filePath)
                if (!source.exists()) {
                    _uiState.update { it.copy(error = "Backup file not found") }
                    return@launch
                }
                storageManager.exportFile(
                    StorageManager.StorageDirectory.Backups,
                    backup.fileName,
                    java.io.File(
                        storageManager.getDirectory(StorageManager.StorageDirectory.Exports),
                        backup.fileName
                    )
                )
                _uiState.update { it.copy(snackbarMessage = "Backup exported to Exports folder") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Export failed") }
            }
        }
    }

    private fun importBackup() {
        _uiState.update { it.copy(snackbarMessage = "Place backup files in the backups directory") }
    }
}
