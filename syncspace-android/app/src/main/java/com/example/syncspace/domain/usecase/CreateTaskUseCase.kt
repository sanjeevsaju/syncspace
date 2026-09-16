package com.example.syncspace.domain.usecase

import com.example.syncspace.domain.model.Task
import com.example.syncspace.domain.repository.TaskRepository
import com.example.syncspace.domain.util.DomainResult
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(private val repository: TaskRepository) {
    suspend operator fun invoke(
        title: String,
        description: String,
        assigneeIds: List<String> = emptyList(),
    ): DomainResult<Task> = repository.createTask(title, description, assigneeIds)
}
