package com.example.syncspace.data.remote

import com.example.syncspace.data.remote.api.AuthApiService
import com.example.syncspace.data.remote.dto.AuthResponseDto
import com.example.syncspace.data.remote.dto.LoginRequestDto
import com.example.syncspace.data.remote.dto.RegisterRequestDto
import retrofit2.Response
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val apiService: AuthApiService
) {
    suspend fun register(email: String, username: String, password: String): Response<AuthResponseDto> {
        return apiService.register(RegisterRequestDto(email, username, password))
    }

    suspend fun login(email: String, password: String): Response<AuthResponseDto> {
        return apiService.login(LoginRequestDto(email, password))
    }
}