package com.example.taskify.data.remote

import com.example.taskify.domain.model.userModel.UserRequest
import com.example.taskify.domain.model.userModel.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserApi {
    @GET("/api/v1/users")
    suspend fun getCurrentUser(): Response<UserResponse>

    @PUT("/api/v1/users")
    suspend fun putUser(@Body user: UserRequest): Response<UserResponse>
}