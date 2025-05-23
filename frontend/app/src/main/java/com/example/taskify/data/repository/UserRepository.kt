package com.example.taskify.data.repository

import com.example.taskify.data.remote.UserApi
import com.example.taskify.domain.model.signInModel.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: UserApi
) {
    suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = api.getCurrentUser()
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    Result.success(user)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to fetch user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}