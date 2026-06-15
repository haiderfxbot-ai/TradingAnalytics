package com.tradinganalytics.ui.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPasswordVisible: Boolean = false,
    val rememberLogin: Boolean = false
)

sealed class LoginEvent {
    data class OnUsernameChange(val username: String) : LoginEvent()
    data class OnPasswordChange(val password: String) : LoginEvent()
    data object OnTogglePassword : LoginEvent()
    data object OnToggleRemember : LoginEvent()
    data object OnLogin : LoginEvent()
    data object OnDismissError : LoginEvent()
}
