package notification.messaging

import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import notification.ws.ConnectionManager

object RabbitMQConsumer {
    private val host = System.getenv("RABBITMQ_HOST") ?: "rabbitmq"
    private val exchangeName = "syncspace.events"
    private val queueName = "notification.task.queue"

    fun startListening() {
        val factory = ConnectionFactory().apply {
            this.host = RabbitMQConsumer.host
            this.username = System.getenv("RABBITMQ_USER") ?: "syncspace_admin"
            this.password = System.getenv("RABBITMQ_PASS") ?: "root_super_secret_password"
            // Automatic reconnect logic
            this.isAutomaticRecoveryEnabled = true
        }

        val connection = factory.newConnection()
        val channel = connection.createChannel()

        // Ensure exchange and durable queue exist, then bind them
        channel.exchangeDeclare(exchangeName, "fanout", true)
        channel.queueDeclare(queueName, true, false, false, null)
        channel.queueBind(queueName, exchangeName, "")

        println("RabbitMQ Consumer listening on queue: $queueName")

        val deliverCallback = DeliverCallback { _, delivery ->
            val message = String(delivery.body, Charsets.UTF_8)
            println("Received event from RabbitMQ")

            // Broadcast to all connected Android WebSocket clients
            CoroutineScope(Dispatchers.IO).launch {
                ConnectionManager.broadcast(message)
            }
        }

        channel.basicConsume(queueName, true, deliverCallback, CancelCallback {})
    }
}
