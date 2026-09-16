package com.example.syncspace.domain.usecase

import com.example.syncspace.domain.model.Notification
import com.example.syncspace.domain.repository.NotificationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetNotificationsUseCase @Inject constructor(private val repository: NotificationRepository) {
    operator fun invoke(): Flow<Notification> = repository.getNotifications()
}
