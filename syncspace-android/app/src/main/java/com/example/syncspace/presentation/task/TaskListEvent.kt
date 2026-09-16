package com.example.syncspace.presentation.task

import com.example.syncspace.domain.model.Task

sealed class TaskListEvent {
    data class DeleteTask(val task: Task) : TaskListEvent()
    data object Logout : TaskListEvent()
    data object Refresh : TaskListEvent()
    data object DismissError : TaskListEvent()
}
