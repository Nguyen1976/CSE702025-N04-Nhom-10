package com.example.taskify.data.repository

import android.util.Log
import com.example.taskify.data.remote.UserApi
import com.example.taskify.domain.model.userModel.UserResponse
import org.json.JSONObject
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: UserApi
) {
    suspend fun getCurrentUser(): Result<UserResponse> {
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

    data class UsernameUpdateRequest (
        val username: String
    )

    data class PasswordUpdateRequest (
        val oldPassword: String,
        val password: String
    )

    suspend fun updateUsername(usernameRequest: UsernameUpdateRequest): Result<UserResponse> {
        return try {
            val response = api.putUsername(usernameRequest)
            if (response.isSuccessful) {
                response.body()?.let { updatedUsername ->
                    Result.success(updatedUsername)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to update username: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(passwordRequest: PasswordUpdateRequest): Result<UserResponse> {
        return try {
            val response = api.putPassword(passwordRequest)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val errorMsg = response.errorBody()?.string()?.let { errorBody ->
                    val jsonObj = JSONObject(errorBody)
                    jsonObj.getString("message")
                } ?: "Failed to update password: ${response.code()} ${response.message()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}