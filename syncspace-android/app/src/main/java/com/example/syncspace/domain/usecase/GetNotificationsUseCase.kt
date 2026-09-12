package com.example.syncspace.domain.usecase

import com.example.syncspace.domain.model.Notification
import com.example.syncspace.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(): Flow<Notification> = repository.getNotifications()
}