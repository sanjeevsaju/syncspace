package com.example.syncspace.presentation.auth

sealed class LoginEvent {
    data class EmailChanged(val email: String): LoginEvent()
    data class PasswordChanged(val password: String): LoginEvent()
    data object Submit: LoginEvent()
    data object DismissError: LoginEvent()
}

sealed class RegisterEvent {
    data class EmailChanged(val email: String): RegisterEvent()
    data class UsernameChanged(val username: String): RegisterEvent()
    data class PasswordChanged(val password: String): RegisterEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String): RegisterEvent()
    data object Submit: RegisterEvent()
    data object DismissError: RegisterEvent()
}

