package com.example.syncspace.data.repository

import com.example.syncspace.BuildConfig
import com.example.syncspace.domain.model.Notification
import com.example.syncspace.domain.repository.NotificationRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val okHttpClient: OkHttpClient
): NotificationRepository {

    private val _messages = MutableSharedFlow<Notification>(extraBufferCapacity = 64)
    override fun getNotifications(): Flow<Notification> = _messages.asSharedFlow()

    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var isConnected = false

    override suspend fun connect() {
        if(isConnected) return
        val request = Request.Builder()
            .url("ws://${BuildConfig.Base_URL}:80/api/v1/notifications/wb")
            .build()
        webSocket = okHttpClient.newWebSocket(request, object: WebSocketListener() {
            override fun onOpen(webSocket: okhttp3.WebSocket, response: Response) {
                isConnected = true
                println("SANJU: WebSocket connected")
            }

            override fun onMessage(webSocket: okhttp3.WebSocket, text: String) {
                val notification = Notification(message = text)
                scope.launch {
                    _messages.emit(notification)
                    println("SANJU: onMessage()")
                }
            }

            override fun onFailure(webSocket: okhttp3.WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                println("SANJU: Websocket failure: ${t.message}")
                // Attempt to reconnect after a short delay
                scope.launch {
                    delay(5000.milliseconds)
                    connect()
                }
            }

            override fun onClosed(webSocket: okhttp3.WebSocket, code: Int, reason: String) {
                isConnected = false
                println("SANJU: Websocket closed: $reason")
                // Attempt to reconnect after a short delay
                scope.launch {
                    delay(5000.milliseconds)
                    connect()
                }
            }
        })
    }

    override suspend fun disconnect() {
        webSocket?.close(1000, "SANJU: Client disconnected")
        webSocket = null
        isConnected = false
        scope.cancel()
    }
}