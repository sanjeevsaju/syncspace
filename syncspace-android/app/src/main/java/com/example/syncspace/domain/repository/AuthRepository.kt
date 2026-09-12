package com.example.syncspace.domain.repository

import com.example.syncspace.domain.model.User
import com.example.syncspace.domain.util.DomainResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(email: String, username: String, password: String): DomainResult<User>
    suspend fun login(email: String, password: String): DomainResult<User>
    suspend fun logout(): DomainResult<Unit>
    fun getCurrentUser(): Flow<User?>
    fun isLoggedIn(): Flow<Boolean>
    fun getToken(): Flow<String?>
}