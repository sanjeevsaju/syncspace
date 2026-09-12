package com.example.syncspace.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncspace.domain.usecase.RegisterUseCase
import com.example.syncspace.domain.util.DomainResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when(event) {
            is RegisterEvent.ConfirmPasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    confirmPassword = event.confirmPassword
                )
            }
            RegisterEvent.DismissError -> {
                _uiState.value = _uiState.value.copy(
                    error = null
                )
            }
            is RegisterEvent.EmailChanged -> {
                _uiState.value = _uiState.value.copy(
                    email = event.email,
                    error = null
                )
            }
            is RegisterEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    password = event.password,
                    error = null
                )
            }
            RegisterEvent.Submit -> {
                register()
            }
            is RegisterEvent.UsernameChanged -> {
                _uiState.value = _uiState.value.copy(
                    username = event.username,
                    error = null
                )
            }
        }
    }

    private fun register() {
        if(!validate()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when(val result = registerUseCase(
                uiState.value.email.trim(),
                uiState.value.username.trim(),
                uiState.value.password.trim())) {
                is DomainResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
                is DomainResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    private fun validate(): Boolean {
        return when {
            uiState.value.email.isBlank() -> {
                _uiState.value = _uiState.value.copy(
                    error = "Email is blank"
                )
                false
            }
            !uiState.value.email.contains("@") -> {
                _uiState.value = _uiState.value.copy(
                    error = "Enter a valid email"
                )
                false
            }
            uiState.value.username.isBlank() -> {
                _uiState.value = _uiState.value.copy(
                    error = "Username is blank"
                )
                false
            }
            uiState.value.username.length < 3 -> {
                _uiState.value = _uiState.value.copy(
                    error = "Username is too short"
                )
                false
            }

            uiState.value.password.length < 6 -> {
                _uiState.value = _uiState.value.copy(
                    error = "Password is too short"
                )
                false
            }

            uiState.value.password != uiState.value.confirmPassword -> {
                _uiState.value = _uiState.value.copy(
                    error = "Passwords do not match"
                )
                false
            }
            else -> true
        }
    }
}