package com.example.syncspace.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncspace.domain.usecase.ConnectNotificationsUseCase
import com.example.syncspace.domain.usecase.DisconnectNotificationsUseCase
import com.example.syncspace.domain.usecase.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val connectUseCase: ConnectNotificationsUseCase,
    private val disconnectUseCase: DisconnectNotificationsUseCase,
    private val getNotificationsUseCase: GetNotificationsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState(isLoading = true))
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        // Connect to WebSocket when the ViewModel is created
        viewModelScope.launch {
            connectUseCase()
        }

        // Collect incoming notifications.
        viewModelScope.launch {
            getNotificationsUseCase()
                .collect { notification ->
                    _uiState.update { state ->
                        state.copy(
                            notifications = (state.notifications + notification)
                                .takeLast(100),
                        )
                    }
                }
        }

        // Set loading false after initial connection (or we can set after first message).
        _uiState.update { it.copy(isLoading = false) }
    }

    override fun onCleared() {
        viewModelScope.launch {
            disconnectUseCase()
        }
    }

    fun onEvent(event: NotificationEvent) {
        when (event) {
            NotificationEvent.ClearAll -> {
                _uiState.update { it.copy(error = null) }
            }
            NotificationEvent.DismissError -> {
                _uiState.update { it.copy(notifications = emptyList()) }
            }
        }
    }
}
