package com.example.syncspace.domain.usecase

import com.example.syncspace.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<String?> = repository.getToken()
}