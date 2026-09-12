package com.example.syncspace.presentation.notification

sealed class NotificationEvent {
    data object DismissError: NotificationEvent()
    data object ClearAll: NotificationEvent()
}