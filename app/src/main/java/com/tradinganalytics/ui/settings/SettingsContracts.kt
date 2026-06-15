package com.tradinganalytics.ui.settings

import com.tradinganalytics.core.constants.AppConstants

enum class ThemeOption(val value: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromValue(value: String): ThemeOption = entries.find { it.value == value } ?: SYSTEM
    }
}

data class SettingsUiState(
    val themeOption: ThemeOption = ThemeOption.SYSTEM,
    val isAppLockEnabled: Boolean = false,
    val appLockPin: String = "",
    val databaseSize: String = "Calculating...",
    val backupSize: String = "Calculating...",
    val appVersion: String = AppConstants.VERSION_NAME,
    val isLoading: Boolean = false,
    val showPinDialog: Boolean = false,
    val pinInput: String = "",
    val confirmPinInput: String = "",
    val pinStep: PinStep = PinStep.CREATE,
    val snackbarMessage: String? = null,
    val isLoggingOut: Boolean = false,
    val error: String? = null
)

sealed class SettingsEvent {
    data object LoadSettings : SettingsEvent()
    data class SetTheme(val option: ThemeOption) : SettingsEvent()
    data object ToggleAppLock : SettingsEvent()
    data object OpenPinDialog : SettingsEvent()
    data object DismissPinDialog : SettingsEvent()
    data class OnPinInputChange(val value: String) : SettingsEvent()
    data class OnConfirmPinInputChange(val value: String) : SettingsEvent()
    data object SavePin : SettingsEvent()
    data object ClearData : SettingsEvent()
    data object ClearHistory : SettingsEvent()
    data object ExportData : SettingsEvent()
    data object CreateBackup : SettingsEvent()
    data object Logout : SettingsEvent()
    data object DismissError : SettingsEvent()
    data object ClearSnackbar : SettingsEvent()
}

enum class PinStep {
    CREATE, CONFIRM
}
