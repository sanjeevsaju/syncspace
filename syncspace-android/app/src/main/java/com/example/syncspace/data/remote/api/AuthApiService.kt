package com.example.syncspace.data.remote.api

import com.example.syncspace.data.remote.dto.AuthResponseDto
import com.example.syncspace.data.remote.dto.LoginRequestDto
import com.example.syncspace.data.remote.dto.RegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequestDto): Response<AuthResponseDto>

    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): Response<AuthResponseDto>
}
