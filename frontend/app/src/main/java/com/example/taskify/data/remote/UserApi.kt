package com.example.taskify.data.remote

import com.example.taskify.data.repository.UserRepository
import com.example.taskify.domain.model.userModel.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @GET("/api/v1/users")
    suspend fun getCurrentUser(): Response<UserResponse>

    @POST("/api/v1/users")
    suspend fun putUsername(@Body usernameRequest: UserRepository.UsernameUpdateRequest): Response<UserResponse>

    @POST("/api/v1/users/update-password")
    suspend fun putPassword(@Body passwordRequest: UserRepository.PasswordUpdateRequest): Response<UserResponse>
}