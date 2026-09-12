package com.example.syncspace.presentation.task

import com.example.syncspace.domain.model.Task

data class TaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null
)
