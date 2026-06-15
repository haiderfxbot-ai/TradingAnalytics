package com.tradinganalytics.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradinganalytics.core.utils.SessionManager
import com.tradinganalytics.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginNavigation {
    data object NavigateToHome : LoginNavigation()
}

sealed class LoginError {
    data object InvalidUsername : LoginError()
    data object InvalidPassword : LoginError()
    data object AccountDisabled : LoginError()
    data object AccountLocked : LoginError()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigation = MutableSharedFlow<LoginNavigation>()
    val navigation = _navigation.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnUsernameChange -> {
                _uiState.update { it.copy(username = event.username, error = null) }
            }
            is LoginEvent.OnPasswordChange -> {
                _uiState.update { it.copy(password = event.password, error = null) }
            }
            is LoginEvent.OnTogglePassword -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is LoginEvent.OnToggleRemember -> {
                _uiState.update { it.copy(rememberLogin = !it.rememberLogin) }
                sessionManager.setRememberLogin(_uiState.value.rememberLogin)
            }
            is LoginEvent.OnLogin -> login()
            is LoginEvent.OnDismissError -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun login() {
        val state = _uiState.value

        if (state.isLoading) return

        val username = state.username.trim()
        val password = state.password

        if (username.isBlank()) {
            _uiState.update { it.copy(error = LoginError.InvalidUsername.name) }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(error = LoginError.InvalidPassword.name) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = userRepository.authenticateUser(username, password)

            result.fold(
                onSuccess = { user ->
                    sessionManager.setRememberLogin(state.rememberLogin)
                    sessionManager.saveSession(
                        username = user.username,
                        role = user.role,
                        userId = user.id
                    )
                    _uiState.update { it.copy(isLoading = false) }
                    _navigation.emit(LoginNavigation.NavigateToHome)
                },
                onFailure = { error ->
                    val errorMessage = when (error.message) {
                        "User not found" -> LoginError.InvalidUsername.name
                        "Invalid password" -> LoginError.InvalidPassword.name
                        "Account is disabled" -> LoginError.AccountDisabled.name
                        "Account is locked" -> LoginError.AccountLocked.name
                        else -> error.message ?: "An unexpected error occurred"
                    }
                    _uiState.update {
                        it.copy(isLoading = false, error = errorMessage)
                    }
                }
            )
        }
    }
}
