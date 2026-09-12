package com.example.syncspace.domain.usecase

import com.example.syncspace.domain.model.User
import com.example.syncspace.domain.repository.AuthRepository
import com.example.syncspace.domain.util.DomainResult
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        username: String,
        password: String
    ): DomainResult<User> {
        return repository.register(
            email = email,
            username = username,
            password = password
        )
    }
}