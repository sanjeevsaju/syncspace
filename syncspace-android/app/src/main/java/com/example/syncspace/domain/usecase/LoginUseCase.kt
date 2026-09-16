package com.example.syncspace.domain.usecase

import com.example.syncspace.domain.model.User
import com.example.syncspace.domain.repository.AuthRepository
import com.example.syncspace.domain.util.DomainResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): DomainResult<User> = repository.login(
        email = email,
        password = password,
    )
}
