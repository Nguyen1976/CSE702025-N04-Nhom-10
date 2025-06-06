package com.example.taskify.data.repository

import com.example.taskify.data.remote.UserApi
import com.example.taskify.domain.model.userModel.UserRequest
import com.example.taskify.domain.model.userModel.UserResponse
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

    suspend fun updateUser(userRequest: UserRequest): Result<UserResponse> {
        return try {
            val response = api.putUser(userRequest)
            if (response.isSuccessful) {
                response.body()?.let { updatedUser ->
                    Result.success(updatedUser)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to update user: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}