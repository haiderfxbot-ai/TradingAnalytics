package com.tradinganalytics.ui.admin

data class AdminUiState(
    val isLoading: Boolean = false,
    val users: List<UserListItem> = emptyList(),
    val totalUsers: Int = 0,
    val activeSessions: Int = 0,
    val lockedAccounts: Int = 0,
    val disabledAccounts: Int = 0,
    val error: String? = null,
    val loginHistory: List<LoginHistoryDisplay> = emptyList(),
    val showLoginHistory: Boolean = false,
    val loginHistoryUsername: String = "",
    val backupList: List<BackupDisplay> = emptyList(),
    val isCreatingUser: Boolean = false,
    val userFormState: UserFormState = UserFormState(),
    val editingUser: UserListItem? = null,
    val showUserDialog: Boolean = false,
    val showDeleteConfirm: UserListItem? = null,
    val showResetPasswordDialog: UserListItem? = null,
    val resetPasswordValue: String = "",
    val snackbarMessage: String? = null,
    val searchQuery: String = ""
)

data class UserListItem(
    val id: Long,
    val username: String,
    val displayName: String,
    val role: String,
    val status: String,
    val createdAt: Long,
    val lastLogin: Long?,
    val isLoggedIn: Boolean
)

data class UserFormState(
    val username: String = "",
    val password: String = "",
    val displayName: String = "",
    val role: String = "USER",
    val isEdit: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val displayNameError: String? = null
)

data class LoginHistoryDisplay(
    val id: Long,
    val username: String,
    val loginTime: Long,
    val logoutTime: Long?,
    val deviceInfo: String
)

data class BackupDisplay(
    val id: Long,
    val fileName: String,
    val fileSize: Long,
    val backupType: String,
    val createdAt: Long
)

sealed class AdminEvent {
    data object LoadUsers : AdminEvent()
    data class DeleteUser(val userId: Long) : AdminEvent()
    data class DisableUser(val userId: Long) : AdminEvent()
    data class EnableUser(val userId: Long) : AdminEvent()
    data class LockUser(val userId: Long) : AdminEvent()
    data class UnlockUser(val userId: Long) : AdminEvent()
    data class ResetPassword(val userId: Long, val newPassword: String) : AdminEvent()
    data class ShowLoginHistory(val username: String) : AdminEvent()
    data object DismissLoginHistory : AdminEvent()
    data object OpenCreateDialog : AdminEvent()
    data class OpenEditDialog(val user: UserListItem) : AdminEvent()
    data object DismissDialog : AdminEvent()
    data class OnFormUsernameChange(val value: String) : AdminEvent()
    data class OnFormPasswordChange(val value: String) : AdminEvent()
    data class OnFormDisplayNameChange(val value: String) : AdminEvent()
    data class OnFormRoleChange(val value: String) : AdminEvent()
    data object SubmitUserForm : AdminEvent()
    data object LoadBackups : AdminEvent()
    data object CreateBackup : AdminEvent()
    data class DeleteBackup(val backupId: Long) : AdminEvent()
    data class RestoreBackup(val backupId: Long) : AdminEvent()
    data class ShowDeleteConfirm(val user: UserListItem) : AdminEvent()
    data object DismissDeleteConfirm : AdminEvent()
    data class ShowResetPasswordDialog(val user: UserListItem) : AdminEvent()
    data object DismissResetPasswordDialog : AdminEvent()
    data class OnResetPasswordChange(val value: String) : AdminEvent()
    data object SubmitResetPassword : AdminEvent()
    data class OnSearchQueryChange(val query: String) : AdminEvent()
    data object DismissError : AdminEvent()
    data object ClearSnackbar : AdminEvent()
}
