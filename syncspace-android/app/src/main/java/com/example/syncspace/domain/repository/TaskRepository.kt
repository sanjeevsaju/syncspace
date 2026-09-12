package com.example.syncspace.domain.repository

import com.example.syncspace.domain.model.Task
import com.example.syncspace.domain.util.DomainResult

interface TaskRepository {
    suspend fun getTasks(): DomainResult<List<Task>>
    suspend fun createTask(title: String, description: String, assigneeIds: List<String>): DomainResult<Task>

    suspend fun deleteTask(id: String): DomainResult<Unit>
}