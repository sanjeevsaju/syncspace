package com.example.syncspace.data.repository

import com.example.syncspace.data.local.AuthLocalDataSource
import com.example.syncspace.data.remote.AuthRemoteDataSource
import com.example.syncspace.data.remote.dto.ErrorResponseDto
import com.example.syncspace.domain.model.User
import com.example.syncspace.domain.repository.AuthRepository
import com.example.syncspace.domain.util.DomainResult
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val local: AuthLocalDataSource,
    private val remote: AuthRemoteDataSource,
) : AuthRepository {
    private val gson = Gson()
    override suspend fun register(
        email: String,
        username: String,
        password: String,
    ): DomainResult<User> = try {
        val response = remote.register(email, username, password)
        if (response.isSuccessful) {
            val body = response.body()!!
            val user = User(body.userId, body.username, email, body.token)
            local.saveUser(user)
            DomainResult.Success(user)
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMsg = try {
                gson.fromJson(errorBody, ErrorResponseDto::class.java).error
            } catch (e: Exception) {
                errorBody ?: "Registration failed"
            }
            DomainResult.Error(errorMsg)
        }
    } catch (e: Exception) {
        DomainResult.Error("Network Error: ${e.message}")
    }

    override suspend fun login(
        email: String,
        password: String,
    ): DomainResult<User> = try {
        val response = remote.login(email, password)
        if (response.isSuccessful) {
            val body = response.body()!!
            val user = User(body.userId, body.username, email, body.token)
            local.saveUser(user)
            DomainResult.Success(user)
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMsg = try {
                gson.fromJson(errorBody, ErrorResponseDto::class.java).error
            } catch (e: Exception) {
                errorBody ?: "Login Failed"
            }
            DomainResult.Error(errorMsg)
        }
    } catch (e: Exception) {
        DomainResult.Error("Network Error: ${e.message}")
    }

    override suspend fun logout(): DomainResult<Unit> {
        local.clear()
        return DomainResult.Success(Unit)
    }

    override fun getCurrentUser(): Flow<User?> = local.getUser()
    override fun isLoggedIn(): Flow<Boolean> = local.getToken().map { it != null }
    override fun getToken(): Flow<String?> = local.getToken()
}
