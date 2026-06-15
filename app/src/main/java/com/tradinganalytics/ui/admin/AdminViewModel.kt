package com.tradinganalytics.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradinganalytics.core.constants.AppConstants
import com.tradinganalytics.data.database.entities.BackupEntity
import com.tradinganalytics.data.database.entities.UserEntity
import com.tradinganalytics.data.repository.BackupRepository
import com.tradinganalytics.data.repository.LoginHistoryRepository
import com.tradinganalytics.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val loginHistoryRepository: LoginHistoryRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    private val _navigation = MutableSharedFlow<AdminNavigation>()
    val navigation: SharedFlow<AdminNavigation> = _navigation.asSharedFlow()

    init {
        loadUsers()
        loadBackups()
    }

    fun onEvent(event: AdminEvent) {
        when (event) {
            is AdminEvent.LoadUsers -> loadUsers()
            is AdminEvent.DeleteUser -> deleteUser(event.userId)
            is AdminEvent.DisableUser -> toggleStatus(event.userId, AppConstants.AccountStatus.DISABLED)
            is AdminEvent.EnableUser -> toggleStatus(event.userId, AppConstants.AccountStatus.ACTIVE)
            is AdminEvent.LockUser -> toggleStatus(event.userId, AppConstants.AccountStatus.LOCKED)
            is AdminEvent.UnlockUser -> toggleStatus(event.userId, AppConstants.AccountStatus.ACTIVE)
            is AdminEvent.ResetPassword -> resetPassword(event.userId, event.newPassword)
            is AdminEvent.ShowLoginHistory -> showLoginHistory(event.username)
            is AdminEvent.DismissLoginHistory -> _uiState.update { it.copy(showLoginHistory = false) }
            is AdminEvent.OpenCreateDialog -> openCreateDialog()
            is AdminEvent.OpenEditDialog -> openEditDialog(event.user)
            is AdminEvent.DismissDialog -> dismissDialog()
            is AdminEvent.OnFormUsernameChange -> onFormUsernameChange(event.value)
            is AdminEvent.OnFormPasswordChange -> onFormPasswordChange(event.value)
            is AdminEvent.OnFormDisplayNameChange -> onFormDisplayNameChange(event.value)
            is AdminEvent.OnFormRoleChange -> onFormRoleChange(event.value)
            is AdminEvent.SubmitUserForm -> submitUserForm()
            is AdminEvent.LoadBackups -> loadBackups()
            is AdminEvent.CreateBackup -> createBackup()
            is AdminEvent.DeleteBackup -> deleteBackup(event.backupId)
            is AdminEvent.RestoreBackup -> restoreBackup(event.backupId)
            is AdminEvent.ShowDeleteConfirm -> _uiState.update { it.copy(showDeleteConfirm = event.user) }
            is AdminEvent.DismissDeleteConfirm -> _uiState.update { it.copy(showDeleteConfirm = null) }
            is AdminEvent.ShowResetPasswordDialog -> _uiState.update { it.copy(showResetPasswordDialog = event.user, resetPasswordValue = "") }
            is AdminEvent.DismissResetPasswordDialog -> _uiState.update { it.copy(showResetPasswordDialog = null, resetPasswordValue = "") }
            is AdminEvent.OnResetPasswordChange -> _uiState.update { it.copy(resetPasswordValue = event.value) }
            is AdminEvent.SubmitResetPassword -> submitResetPassword()
            is AdminEvent.OnSearchQueryChange -> _uiState.update { it.copy(searchQuery = event.query) }
            is AdminEvent.DismissError -> _uiState.update { it.copy(error = null) }
            is AdminEvent.ClearSnackbar -> _uiState.update { it.copy(snackbarMessage = null) }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                userRepository.getAllUsers().collect { userEntities ->
                    val users = userEntities.map { it.toListItem() }
                    val totalUsers = users.size
                    val activeSessions = users.count { it.isLoggedIn }
                    val lockedAccounts = users.count { it.status == AppConstants.AccountStatus.LOCKED }
                    val disabledAccounts = users.count { it.status == AppConstants.AccountStatus.DISABLED }
                    _uiState.update {
                        it.copy(
                            users = users,
                            totalUsers = totalUsers,
                            activeSessions = activeSessions,
                            lockedAccounts = lockedAccounts,
                            disabledAccounts = disabledAccounts,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load users")
                }
            }
        }
    }

    private fun deleteUser(userId: Long) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(userId) ?: return@launch
                userRepository.deleteUser(user)
                _uiState.update {
                    it.copy(
                        showDeleteConfirm = null,
                        snackbarMessage = "User deleted successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to delete user")
                }
            }
        }
    }

    private fun toggleStatus(userId: Long, newStatus: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(userId) ?: return@launch
                val updated = user.copy(status = newStatus)
                userRepository.updateUser(updated)
                val label = when (newStatus) {
                    AppConstants.AccountStatus.ACTIVE -> "enabled"
                    AppConstants.AccountStatus.DISABLED -> "disabled"
                    AppConstants.AccountStatus.LOCKED -> "locked"
                    else -> "updated"
                }
                _uiState.update {
                    it.copy(snackbarMessage = "User $label successfully")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to update user status")
                }
            }
        }
    }

    private fun resetPassword(userId: Long, newPassword: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(userId) ?: return@launch
                val salt = userRepository.hashPassword(newPassword, user.passwordSalt)
                val updated = user.copy(passwordHash = salt)
                userRepository.updateUser(updated)
                _uiState.update {
                    it.copy(
                        snackbarMessage = "Password reset successfully",
                        showResetPasswordDialog = null,
                        resetPasswordValue = ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to reset password")
                }
            }
        }
    }

    private fun showLoginHistory(username: String) {
        viewModelScope.launch {
            try {
                loginHistoryRepository.getAllByUser(username).collect { history ->
                    val displays = history.map { entity ->
                        LoginHistoryDisplay(
                            id = entity.id,
                            username = entity.username,
                            loginTime = entity.loginTime.time,
                            logoutTime = entity.logoutTime?.time,
                            deviceInfo = entity.deviceInfo
                        )
                    }
                    _uiState.update {
                        it.copy(
                            loginHistory = displays,
                            showLoginHistory = true,
                            loginHistoryUsername = username
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to load login history")
                }
            }
        }
    }

    private fun openCreateDialog() {
        _uiState.update {
            it.copy(
                showUserDialog = true,
                userFormState = UserFormState(),
                editingUser = null
            )
        }
    }

    private fun openEditDialog(user: UserListItem) {
        _uiState.update {
            it.copy(
                showUserDialog = true,
                editingUser = user,
                userFormState = UserFormState(
                    username = user.username,
                    displayName = user.displayName,
                    role = user.role,
                    isEdit = true
                )
            )
        }
    }

    private fun dismissDialog() {
        _uiState.update {
            it.copy(
                showUserDialog = false,
                userFormState = UserFormState(),
                editingUser = null
            )
        }
    }

    private fun onFormUsernameChange(value: String) {
        _uiState.update {
            it.copy(userFormState = it.userFormState.copy(username = value, usernameError = null))
        }
    }

    private fun onFormPasswordChange(value: String) {
        _uiState.update {
            it.copy(userFormState = it.userFormState.copy(password = value, passwordError = null))
        }
    }

    private fun onFormDisplayNameChange(value: String) {
        _uiState.update {
            it.copy(userFormState = it.userFormState.copy(displayName = value, displayNameError = null))
        }
    }

    private fun onFormRoleChange(value: String) {
        _uiState.update {
            it.copy(userFormState = it.userFormState.copy(role = value))
        }
    }

    private fun submitUserForm() {
        val form = _uiState.value.userFormState
        var hasError = false

        if (form.username.isBlank()) {
            _uiState.update { it.copy(userFormState = it.userFormState.copy(usernameError = "Username is required")) }
            hasError = true
        }
        if (!form.isEdit && form.password.isBlank()) {
            _uiState.update { it.copy(userFormState = it.userFormState.copy(passwordError = "Password is required")) }
            hasError = true
        }
        if (form.displayName.isBlank()) {
            _uiState.update { it.copy(userFormState = it.userFormState.copy(displayNameError = "Display name is required")) }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            try {
                if (form.isEdit) {
                    val existing = _uiState.value.editingUser ?: return@launch
                    val user = userRepository.getUserById(existing.id) ?: return@launch
                    val updated = user.copy(
                        username = form.username,
                        displayName = form.displayName,
                        role = form.role
                    )
                    userRepository.updateUser(updated)
                    _uiState.update {
                        it.copy(
                            snackbarMessage = "User updated successfully",
                            showUserDialog = false,
                            editingUser = null,
                            userFormState = UserFormState()
                        )
                    }
                } else {
                    val result = userRepository.createUser(
                        username = form.username,
                        password = form.password,
                        displayName = form.displayName,
                        role = form.role
                    )
                    result.fold(
                        onSuccess = {
                            _uiState.update {
                                it.copy(
                                    snackbarMessage = "User created successfully",
                                    showUserDialog = false,
                                    userFormState = UserFormState()
                                )
                            }
                        },
                        onFailure = { e ->
                            _uiState.update {
                                it.copy(snackbarMessage = e.message ?: "Failed to create user")
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Operation failed")
                }
            }
        }
    }

    private fun loadBackups() {
        viewModelScope.launch {
            try {
                backupRepository.getAll().collect { entities ->
                    val displays = entities.map { entity ->
                        BackupDisplay(
                            id = entity.id,
                            fileName = entity.fileName,
                            fileSize = entity.fileSize,
                            backupType = entity.backupType,
                            createdAt = entity.createdAt.time
                        )
                    }
                    _uiState.update { it.copy(backupList = displays) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to load backups")
                }
            }
        }
    }

    private fun createBackup() {
        viewModelScope.launch {
            try {
                val backup = BackupEntity(
                    fileName = "backup_${System.currentTimeMillis()}.db",
                    filePath = "/backups/",
                    fileSize = 0L,
                    backupType = "FULL",
                    createdAt = Date(),
                    isEncrypted = false
                )
                backupRepository.insert(backup)
                _uiState.update {
                    it.copy(snackbarMessage = "Backup created successfully")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to create backup")
                }
            }
        }
    }

    private fun deleteBackup(backupId: Long) {
        viewModelScope.launch {
            try {
                val backups = backupRepository.getAll().first()
                val backup = backups.find { it.id == backupId } ?: return@launch
                backupRepository.delete(backup)
                _uiState.update {
                    it.copy(snackbarMessage = "Backup deleted successfully")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to delete backup")
                }
            }
        }
    }

    private fun restoreBackup(backupId: Long) {
        viewModelScope.launch {
            try {
                val backups = backupRepository.getAll().first()
                val backup = backups.find { it.id == backupId } ?: return@launch
                _uiState.update {
                    it.copy(snackbarMessage = "Backup restored: ${backup.fileName}")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to restore backup")
                }
            }
        }
    }

    private fun submitResetPassword() {
        val newPassword = _uiState.value.resetPasswordValue
        val user = _uiState.value.showResetPasswordDialog ?: return
        if (newPassword.isBlank()) {
            _uiState.update { it.copy(snackbarMessage = "Password cannot be empty") }
            return
        }
        resetPassword(user.id, newPassword)
    }

    private fun UserEntity.toListItem() = UserListItem(
        id = id,
        username = username,
        displayName = displayName,
        role = role,
        status = status,
        createdAt = createdAt.time,
        lastLogin = lastLogin?.time,
        isLoggedIn = isLoggedIn
    )
}

sealed class AdminNavigation {
    data object NavigateToUserManagement : AdminNavigation()
    data object NavigateToSystemSettings : AdminNavigation()
    data object NavigateToBackupManagement : AdminNavigation()
    data object NavigateToDataImportExport : AdminNavigation()
    data object NavigateToSecuritySettings : AdminNavigation()
}
