package com.example.syncspace.domain.repository

import com.example.syncspace.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun connect()
    suspend fun disconnect()
    fun getNotifications(): Flow<Notification>
}