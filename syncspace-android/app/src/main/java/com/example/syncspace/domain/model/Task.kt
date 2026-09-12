package com.example.syncspace.domain.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val assignees: List<UserProfile> = emptyList()
)
