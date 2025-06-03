package com.example.taskify.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.taskify.domain.model.userModel.UserResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userDataStore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {
    private val gson = Gson()
    private val USER_KEY = stringPreferencesKey("user_json")

    suspend fun saveUser(user: UserResponse) {
        val json = gson.toJson(user)
        context.userDataStore.edit { prefs ->
            prefs[USER_KEY] = json
        }
    }

    fun getUser(): Flow<UserResponse?> = context.userDataStore.data.map { prefs ->
        prefs[USER_KEY]?.let { Gson().fromJson(it, UserResponse::class.java) }
    }

    suspend fun clearUser() {
        context.userDataStore.edit { prefs ->
            prefs.remove(USER_KEY)
        }
    }
}
