package com.example.taskify.domain.model.signInModel

data class SignInResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

data class User (
    val id: String,
    val username: String,
    val email: String
)