package task.graphql

import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import org.slf4j.LoggerFactory
import task.client.ProfileGrpcClient
import task.db.TaskRepository
import task.messaging.RabbitMQPublisher
import task.model.TaskResponse

// 1. GraphQL Queries
class TaskQuery : Query {
    suspend fun tasks(): List<TaskResponse> {
        val tasks = TaskRepository.getAllTasks()
        return tasks.map { doc ->
            TaskResponse(
                id = doc.id,
                title = doc.title,
                description = doc.description,
                status = doc.status,
                // Call gRPC to enrich the Task with real user names
                assignees = ProfileGrpcClient.fetchProfiles(doc.assigneeIds),
            )
        }
    }

    suspend fun taskById(id: String): TaskResponse? {
        val doc = TaskRepository.getTaskById(id = id) ?: return null
        return TaskResponse(
            id = doc.id,
            title = doc.title,
            description = doc.description,
            status = doc.status,
            assignees = ProfileGrpcClient.fetchProfiles(doc.assigneeIds),
        )
    }
}

// 2. GraphQL Mutations
class TaskMutation : Mutation {
    suspend fun createTask(
        title: String,
        description: String,
        assigneeIds: List<String>,
    ): TaskResponse {
        val logger = LoggerFactory.getLogger(TaskMutation::class.java)

        return try {
            val created = TaskRepository.createTask(title, description, assigneeIds)

            // Fire-and-forget async event to RabbitMQ.
            RabbitMQPublisher.publishTaskCreated(created.id, created.title)

            TaskResponse(
                id = created.id,
                title = created.title,
                description = created.description,
                status = created.status,
                assignees = ProfileGrpcClient.fetchProfiles(created.assigneeIds),
//                assignees = emptyList()
            )
        } catch (e: Exception) {
            logger.error("createTask failed", e)
            throw RuntimeException("Failed to create task: ${e.message}", e)
        }
    }

    suspend fun deleteTask(id: String): Boolean {
        val logger = LoggerFactory.getLogger(TaskMutation::class.java)

        return try {
            TaskRepository.deleteTask(id)
        } catch (e: Exception) {
            logger.error("delete Task failed", e)
            throw RuntimeException("Failed to delete task: ${e.message}", e)
        }
    }
}
