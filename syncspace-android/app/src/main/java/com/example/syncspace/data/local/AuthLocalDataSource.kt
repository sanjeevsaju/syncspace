package com.example.syncspace.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.syncspace.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val TOKEN = stringPreferencesKey("jwt_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val EMAIL = stringPreferencesKey("email")
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { prefs ->
            prefs[TOKEN] = user.token
            prefs[USER_ID] = user.id
            prefs[USERNAME] = user.username
            prefs[EMAIL] = user.email
        }
    }

    fun getUser(): Flow<User?> = dataStore.data
        .catch { exception ->
            if(exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            val token = prefs[TOKEN] ?: return@map null
            val id = prefs[USER_ID] ?: return@map null
            val username = prefs[USERNAME] ?: return@map null
            val email = prefs[EMAIL] ?: return@map null
            User(id, username, email, token)
        }

    fun getToken(): Flow<String?> = dataStore.data
        .catch { exception ->
            if(exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { it[TOKEN] }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}