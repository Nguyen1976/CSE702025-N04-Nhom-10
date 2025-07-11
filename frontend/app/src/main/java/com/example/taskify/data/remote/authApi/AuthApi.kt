package com.example.taskify.data.remote.authApi

import com.example.taskify.domain.model.signInModel.SignInRequest
import com.example.taskify.domain.model.signInModel.SignInResponse
import com.example.taskify.domain.model.signUpModel.SignUpRequest
import com.example.taskify.domain.model.signUpModel.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LogoutResponse(val message: String)

data class LogoutRequest(
    val accessToken: String,
    val refreshToken: String
)

data class RefreshTokenRequest(val refreshToken: String)

data class RefreshTokenResponse(val accessToken: String)

interface AuthApi {
    @POST("/api/v1/users/register")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>

    @POST("/api/v1/users/login")
    suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>

    @POST("/api/v1/users/logout")
    suspend fun signOut(@Body logoutRequest: LogoutRequest): Response<LogoutResponse>

    @POST("/api/v1/users/refresh_token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>
}