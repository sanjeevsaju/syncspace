package com.example.syncspace.presentation.notification

import com.example.syncspace.domain.model.Notification

data class NotificationUiState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)