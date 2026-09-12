package com.example.syncspace.presentation.task

sealed class CreateTaskEvent {
    data class TitleChanged(val title: String): CreateTaskEvent()
    data class DescriptionChanged(val description: String): CreateTaskEvent()
    data object Submit: CreateTaskEvent()
    data object DismissError: CreateTaskEvent()
}