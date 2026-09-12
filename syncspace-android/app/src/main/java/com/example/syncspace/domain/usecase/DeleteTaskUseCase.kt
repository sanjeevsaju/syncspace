package com.example.syncspace.domain.usecase

import com.example.syncspace.domain.repository.TaskRepository
import com.example.syncspace.domain.util.DomainResult
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(
        taskId: String
    ): DomainResult<Unit> {
        return repository.deleteTask(taskId)
    }
}