package task.db

import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import task.model.TaskDocument

object TaskRepository {
    private val mongoUri = System.getenv("MONGO_URI") ?: "mongodb://syncspace_admin:root_super_secret_password@localhost:27017"
    private val client = MongoClient.create(mongoUri)
    private val database = client.getDatabase("syncspace_tasks")
    private val collection = database.getCollection<TaskDocument>("tasks")

    suspend fun getAllTasks(): List<TaskDocument> = collection.find().toList()

    suspend fun getTaskById(id: String): TaskDocument? =
        collection.find(eq("_id", id)).firstOrNull()

    suspend fun createTask(title: String, description: String, assigneeIds: List<String>): TaskDocument {
        val task = TaskDocument(title = title, description = description, assigneeIds = assigneeIds)
        collection.insertOne(task)
        return task
    }

    suspend fun deleteTask(id: String): Boolean {
        val result = collection.deleteOne(eq("_id", id))
        return result.deletedCount == 1L
    }
}