package com.example.taskify.data.remote

import com.example.taskify.domain.model.userModel.UserResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("/api/v1/users")
    suspend fun getCurrentUser(): Response<UserResponse>
}