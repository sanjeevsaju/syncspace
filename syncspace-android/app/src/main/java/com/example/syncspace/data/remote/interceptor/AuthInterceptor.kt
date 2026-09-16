package com.example.syncspace.data.remote.interceptor

import com.example.syncspace.data.local.AuthLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class AuthInterceptor @Inject constructor(private val localDataSource: AuthLocalDataSource) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = runBlocking { localDataSource.getToken().first() }

        return if (token != null && request.header("Authorization") == null) {
            chain.proceed(
                request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build(),
            )
        } else {
            chain.proceed(request)
        }
    }
}
