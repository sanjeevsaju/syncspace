package com.example.syncspace.domain.model

import java.util.Date

data class Notification(
    val id: String = System.currentTimeMillis().toString(),
    val message: String,
    val type: String? = null,
    val timeStamp: Date = Date()
)
