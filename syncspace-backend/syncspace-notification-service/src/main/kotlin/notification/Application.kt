package notification

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import java.util.UUID
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.channels.consumeEach
import notification.messaging.RabbitMQConsumer
import notification.ws.ConnectionManager

fun main() {
    // Start RabbitMQ background listener
    RabbitMQConsumer.startListening()

    embeddedServer(Netty, 8080, "0.0.0.0") {
        module()
    }.start(true)
}

fun Application.module() {
    install(WebSockets) {
        pingPeriod = 60.seconds
        timeout = 60.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/api/v1/notifications/wb") {
            val sessionId = UUID.randomUUID().toString()
            ConnectionManager.addSession(sessionId, this)
            try {
                // Keep the socket open and listen for incoming client frames/pings
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        println("Received message from client [$sessionId]: $text")
                    }
                }
            } catch (e: Exception) {
                println("Websocket session error [$sessionId]: ${e.message}")
            } finally {
                ConnectionManager.removeSession(sessionId)
            }
        }
    }
}
