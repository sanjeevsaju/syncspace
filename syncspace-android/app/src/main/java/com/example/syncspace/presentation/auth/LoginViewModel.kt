package com.example.syncspace.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncspace.domain.usecase.LoginUseCase
import com.example.syncspace.domain.util.DomainResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            LoginEvent.DismissError -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
            is LoginEvent.EmailChanged -> {
                _uiState.value = _uiState.value.copy(
                    email = event.email,
                    error = null,
                )
            }
            is LoginEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    password = event.password,
                    error = null,
                )
            }
            LoginEvent.Submit -> {
                login()
            }
        }
    }

    private fun login() {
        if (!validate()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
            )
            when (val result = loginUseCase(uiState.value.email.trim(), uiState.value.password)) {
                is DomainResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
                }
                is DomainResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
            }
        }
    }

    private fun validate(): Boolean = when {
        uiState.value.email.isBlank() -> {
            _uiState.value = _uiState.value.copy(
                error = "Email is required",
            )
            false
        }
        !uiState.value.email.contains("@") -> {
            _uiState.value = _uiState.value.copy(
                error = "Enter a valid email",
            )
            false
        }
        uiState.value.password.isBlank() -> {
            _uiState.value = _uiState.value.copy(
                error = "Password is required",
            )
            false
        }
        else -> true
    }
}
