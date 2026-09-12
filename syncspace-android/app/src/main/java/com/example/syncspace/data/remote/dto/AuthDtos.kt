package com.example.syncspace.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    val email: String,
    val username: String,
    val password: String
)

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class AuthResponseDto(
    @SerializedName("userId") val userId: String,
    val username: String,
    val token: String
)

data class ErrorResponseDto(
    val error: String
)