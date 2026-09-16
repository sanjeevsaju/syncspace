package com.example.syncspace.di

import com.example.syncspace.data.repository.AuthRepositoryImpl
import com.example.syncspace.data.repository.NotificationRepositoryImpl
import com.example.syncspace.data.repository.TaskRepositoryImpl
import com.example.syncspace.domain.repository.AuthRepository
import com.example.syncspace.domain.repository.NotificationRepository
import com.example.syncspace.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository
}
