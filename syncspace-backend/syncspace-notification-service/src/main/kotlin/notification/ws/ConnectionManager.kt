package notification.ws

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

object ConnectionManager {
    // Stores active WebSocket sessions mapped to a unique session ID
    private val activeSessions = ConcurrentHashMap<String, DefaultWebSocketServerSession>()

    fun addSession(id: String, session: DefaultWebSocketServerSession) {
        activeSessions[id] = session
        println("Client connected: $id. Total active sessions: ${activeSessions.size}")
    }

    fun removeSession(id: String) {
        activeSessions.remove(id)
        println("Client disconnected: $id. Total active sessions: ${activeSessions.size}")
    }

    suspend fun broadcast(messageJSON: String) = withContext(Dispatchers.IO) {
        activeSessions.values.forEach { session ->
            try {
                session.send(Frame.Text(messageJSON))
            } catch (e: Exception) {
                println("Failed to send message to session: ${e.message}")
            }
        }
    }
}