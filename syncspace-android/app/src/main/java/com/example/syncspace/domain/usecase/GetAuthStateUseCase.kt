package com.example.syncspace.domain.usecase

import com.example.syncspace.domain.repository.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetAuthStateUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(): Flow<Boolean> = repository.isLoggedIn()
}
