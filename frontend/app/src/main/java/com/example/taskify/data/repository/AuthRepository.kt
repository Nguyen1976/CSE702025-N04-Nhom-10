package com.example.taskify.data.repository

import com.example.taskify.data.local.TokenManager
import com.example.taskify.data.remote.AuthApi
import com.example.taskify.domain.model.signInModel.SignInRequest
import com.example.taskify.domain.model.signInModel.SignInResponse
import com.example.taskify.domain.model.signUpModel.ErrorResponse
import com.example.taskify.domain.model.signUpModel.SignUpRequest
import com.example.taskify.domain.model.signUpModel.SignUpResponse
import com.google.gson.Gson
import javax.inject.Inject

class AuthRepository @Inject constructor (
    private val api: AuthApi,
    private val tokenManager: TokenManager
) {
    suspend fun signUp(request: SignUpRequest): Result<SignUpResponse> {
        return try {
            val response = api.signUp(request)
            if(response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = Gson().fromJson(errorBody, ErrorResponse::class.java).error
                Result.failure(Exception(errorMessage))
            }
        } catch (e:Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(request: SignInRequest): Result<SignInResponse> {
        return try {
            val response = api.signIn(request)
            if(response.isSuccessful) {
                val body = response.body()!!

                tokenManager.saveAccessToken(body.accessToken)
                tokenManager.saveRefreshToken(body.refreshToken)

                Result.success(body)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = Gson().fromJson(errorBody, ErrorResponse::class.java).error
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        return tokenManager.getAccessToken() != null
    }
}