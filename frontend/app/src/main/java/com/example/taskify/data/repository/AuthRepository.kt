package com.example.taskify.data.repository

import com.example.taskify.data.remote.AuthApi
import com.example.taskify.domain.model.signUpModel.ErrorResponse
import com.example.taskify.domain.model.signUpModel.SignUpRequest
import com.example.taskify.domain.model.signUpModel.SignUpResponse
import com.google.gson.Gson

class AuthRepository (private val api: AuthApi) {
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
}