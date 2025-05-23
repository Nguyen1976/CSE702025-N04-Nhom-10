package com.example.taskify.data.remote

import com.example.taskify.domain.model.signInModel.User
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("/api/user/me")
    suspend fun getCurrentUser(): Response<User>
}