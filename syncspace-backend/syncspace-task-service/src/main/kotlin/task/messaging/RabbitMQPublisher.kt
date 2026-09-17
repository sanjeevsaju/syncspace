package task.messaging

import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class TaskEvent(
    val eventType: String,
    val taskId: String,
    val title: String,
    val timeStamp: Long = System.currentTimeMillis(),
)

object RabbitMQPublisher {
    private val host = System.getenv("RABBITMQ_HOST") ?: "rabbitmq"
    private val exchangeName = System.getenv("syncspace.events") ?: "syncspace.events"

    private val connectionFactory =
        ConnectionFactory().apply {
            this.host = RabbitMQPublisher.host
            this.username = System.getenv("RABBITMQ_USER") ?: "syncspace_admin"
            this.password = System.getenv("RABBITMQ_PASS") ?: "root_super_secret_password"
        }

    private val connection by lazy { connectionFactory.newConnection() }
    private val channel by lazy {
        connection.createChannel().apply {
            // Fanout exchange broadcasts to all bound notification queues
            exchangeDeclare(exchangeName, "fanout", true)
        }
    }

    suspend fun publishTaskCreated(
        taskId: String,
        title: String,
    ) = withContext(Dispatchers.IO) {
        try {
            val event =
                TaskEvent(
                    eventType = "TASK_CREATED",
                    taskId = taskId,
                    title = title,
                )
            val payload = Json.encodeToString(event).toByteArray(Charsets.UTF_8)
            channel.basicPublish(exchangeName, "", null, payload)
        } catch (e: Exception) {
            println("failed to publish RabbitMQ event: ${e.message}")
        }
    }
}
