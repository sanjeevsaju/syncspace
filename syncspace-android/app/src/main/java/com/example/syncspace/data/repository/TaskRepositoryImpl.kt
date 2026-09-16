package com.example.syncspace.data.repository

import com.example.syncspace.data.remote.TaskRemoteDataSource
import com.example.syncspace.domain.model.Task
import com.example.syncspace.domain.model.UserProfile
import com.example.syncspace.domain.repository.TaskRepository
import com.example.syncspace.domain.util.DomainResult
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(private val remoteDataSource: TaskRemoteDataSource) : TaskRepository {
    override suspend fun getTasks(): DomainResult<List<Task>> = try {
        val response = remoteDataSource.getTasks().execute()
        if (response.hasErrors()) {
            val errors = response.errors?.joinToString { it.message } ?: "Unknown Error"
            DomainResult.Error(errors)
        } else {
            val tasks = response.data?.tasks?.map { task ->
                Task(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    status = task.status,
                    assignees = task.assignees.map { assignee ->
                        UserProfile(
                            userId = assignee.userId,
                            username = assignee.username,
                            displayName = assignee.displayName,
                            avatarUrl = assignee.avatarUrl,
                        )
                    },
                )
            } ?: emptyList()
            DomainResult.Success(tasks)
        }
    } catch (e: Exception) {
        DomainResult.Error("Network Error: ${e.message}")
    }

    override suspend fun createTask(
        title: String,
        description: String,
        assigneeIds: List<String>,
    ): DomainResult<Task> = try {
        val response = remoteDataSource.createTask(title, description, assigneeIds).execute()

        if (response.exception != null) {
            DomainResult.Error("Request failed: ${response.exception!!.message}")
        }

        // Log everything
        android.util.Log.d(
            "TaskRepo",
            """
        Response:
        hasErrors: ${response.hasErrors()}
        data: ${response.data}
        errors: ${response.errors}
        exception: ${response.exception}
        raw body? (can't get easily, but exception might reveal parsing)
            """.trimIndent(),
        )

        if (response.hasErrors()) {
            val errors = response.errors?.joinToString { it.message } ?: "Unknown Error"
            DomainResult.Error(errors)
        } else {
            val task = response.data?.createTask?.let {
                Task(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    status = it.status,
                    assignees = it.assignees.map { assignee ->
                        UserProfile(
                            userId = assignee.userId,
                            username = assignee.username,
                            displayName = assignee.displayName,
                            avatarUrl = assignee.avatarUrl,
                        )
                    },
                )
            }
            if (task != null) {
                DomainResult.Success(task)
            } else {
                DomainResult.Error("Empty Response")
            }
        }
    } catch (e: Exception) {
        DomainResult.Error("Network Error: ${e.message}")
    }

    override suspend fun deleteTask(id: String): DomainResult<Unit> = try {
        val response = remoteDataSource
            .deleteTask(id)
            .execute()

        if (response.hasErrors()) {
            val errors = response.errors?.joinToString { it.message } ?: "Unknown Error"
            DomainResult.Error(errors)
        } else {
            val deleted = response.data?.deleteTask ?: false
            if (deleted) {
                DomainResult.Success(Unit)
            } else {
                DomainResult.Error("Task not found or could not be deleted")
            }
        }
    } catch (e: Exception) {
        DomainResult.Error("Network Error: ${e.message}")
    }
}
