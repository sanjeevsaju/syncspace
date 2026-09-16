package com.example.syncspace.domain.usecase

import com.example.syncspace.domain.repository.NotificationRepository
import javax.inject.Inject

class DisconnectNotificationsUseCase @Inject constructor(private val repository: NotificationRepository) {
    suspend operator fun invoke() = repository.disconnect()
}
