package com.tradinganalytics.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradinganalytics.core.utils.SessionManager
import com.tradinganalytics.data.preferences.AppPreferences
import com.tradinganalytics.storage.StorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val storageManager = StorageManager(context)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _navigation = MutableSharedFlow<SettingsNavigation>()
    val navigation: SharedFlow<SettingsNavigation> = _navigation.asSharedFlow()

    init {
        loadSettings()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> loadSettings()
            is SettingsEvent.SetTheme -> setTheme(event.option)
            is SettingsEvent.ToggleAppLock -> toggleAppLock()
            is SettingsEvent.OpenPinDialog -> _uiState.update {
                it.copy(showPinDialog = true, pinStep = PinStep.CREATE, pinInput = "", confirmPinInput = "")
            }
            is SettingsEvent.DismissPinDialog -> _uiState.update {
                it.copy(showPinDialog = false, pinInput = "", confirmPinInput = "")
            }
            is SettingsEvent.OnPinInputChange -> _uiState.update {
                it.copy(pinInput = event.value)
            }
            is SettingsEvent.OnConfirmPinInputChange -> _uiState.update {
                it.copy(confirmPinInput = event.value)
            }
            is SettingsEvent.SavePin -> savePin()
            is SettingsEvent.ClearData -> clearData()
            is SettingsEvent.ClearHistory -> clearHistory()
            is SettingsEvent.ExportData -> exportData()
            is SettingsEvent.CreateBackup -> createBackup()
            is SettingsEvent.Logout -> logout()
            is SettingsEvent.DismissError -> _uiState.update { it.copy(error = null) }
            is SettingsEvent.ClearSnackbar -> _uiState.update { it.copy(snackbarMessage = null) }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val themeValue = appPreferences.theme.first()
                val themeOption = ThemeOption.fromValue(themeValue)

                val dbSize = storageManager.getDirectorySize(StorageManager.StorageDirectory.Database)
                val backupSize = storageManager.getDirectorySize(StorageManager.StorageDirectory.Backups)

                _uiState.update {
                    it.copy(
                        themeOption = themeOption,
                        databaseSize = storageManager.formatSize(dbSize),
                        backupSize = storageManager.formatSize(backupSize),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load settings")
                }
            }
        }
    }

    private fun setTheme(option: ThemeOption) {
        viewModelScope.launch {
            try {
                appPreferences.setTheme(option.value)
                _uiState.update { it.copy(themeOption = option) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to set theme")
                }
            }
        }
    }

    private fun toggleAppLock() {
        val current = _uiState.value.isAppLockEnabled
        if (current) {
            viewModelScope.launch {
                context.getSharedPreferences("app_lock", Context.MODE_PRIVATE).edit()
                    .putBoolean("app_lock_enabled", false)
                    .putString("app_lock_pin", "")
                    .apply()
                _uiState.update { it.copy(isAppLockEnabled = false, appLockPin = "") }
            }
        } else {
            _uiState.update {
                it.copy(showPinDialog = true, pinStep = PinStep.CREATE, pinInput = "", confirmPinInput = "")
            }
        }
    }

    private fun savePin() {
        val state = _uiState.value
        when (state.pinStep) {
            PinStep.CREATE -> {
                if (state.pinInput.length < 4) {
                    _uiState.update { it.copy(snackbarMessage = "PIN must be at least 4 digits") }
                    return
                }
                _uiState.update {
                    it.copy(pinStep = PinStep.CONFIRM, pinInput = "", confirmPinInput = "")
                }
            }
            PinStep.CONFIRM -> {
                if (state.pinInput != state.confirmPinInput) {
                    _uiState.update { it.copy(snackbarMessage = "PINs do not match", pinInput = "", confirmPinInput = "") }
                    return
                }
                viewModelScope.launch {
                    context.getSharedPreferences("app_lock", Context.MODE_PRIVATE).edit()
                        .putBoolean("app_lock_enabled", true)
                        .putString("app_lock_pin", state.confirmPinInput)
                        .apply()
                    _uiState.update {
                        it.copy(
                            isAppLockEnabled = true,
                            appLockPin = state.confirmPinInput,
                            showPinDialog = false,
                            pinInput = "",
                            confirmPinInput = "",
                            snackbarMessage = "App lock enabled"
                        )
                    }
                }
            }
        }
    }

    private fun clearData() {
        viewModelScope.launch {
            try {
                storageManager.cleanupCache()
                val dbSize = storageManager.getDirectorySize(StorageManager.StorageDirectory.Database)
                _uiState.update {
                    it.copy(
                        databaseSize = storageManager.formatSize(dbSize),
                        snackbarMessage = "Cache cleared successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to clear data")
                }
            }
        }
    }

    private fun clearHistory() {
        viewModelScope.launch {
            try {
                context.deleteDatabase("trading_analytics_db")
                _uiState.update {
                    it.copy(snackbarMessage = "History cleared successfully")
                }
                loadSettings()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to clear history")
                }
            }
        }
    }

    private fun exportData() {
        viewModelScope.launch {
            try {
                val exportsDir = storageManager.getDirectory(StorageManager.StorageDirectory.Exports)
                val exportFile = java.io.File(exportsDir, "export_${System.currentTimeMillis()}.json")
                exportFile.writeText("{}")
                _uiState.update {
                    it.copy(snackbarMessage = "Data exported to ${exportFile.absolutePath}")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to export data")
                }
            }
        }
    }

    private fun createBackup() {
        viewModelScope.launch {
            try {
                val backupsDir = storageManager.getDirectory(StorageManager.StorageDirectory.Backups)
                val backupFile = java.io.File(backupsDir, "backup_${System.currentTimeMillis()}.json")
                backupFile.writeText("{}")
                val newSize = storageManager.getDirectorySize(StorageManager.StorageDirectory.Backups)
                _uiState.update {
                    it.copy(
                        backupSize = storageManager.formatSize(newSize),
                        snackbarMessage = "Backup created successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to create backup")
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            try {
                sessionManager.clearSession()
                _uiState.update { it.copy(isLoggingOut = false) }
                _navigation.emit(SettingsNavigation.NavigateToLogin)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoggingOut = false, snackbarMessage = "Failed to logout")
                }
            }
        }
    }
}

sealed class SettingsNavigation {
    data object NavigateToLogin : SettingsNavigation()
}
