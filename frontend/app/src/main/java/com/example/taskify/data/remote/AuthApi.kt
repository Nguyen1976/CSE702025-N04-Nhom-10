package com.example.taskify.data.remote

import com.example.taskify.domain.model.signUpModel.SignUpRequest
import com.example.taskify.domain.model.signUpModel.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>
}