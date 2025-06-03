package com.example.taskify.domain.model.signInModel

data class SignInResponse(
    val accessToken: String,
    val refreshToken: String,
    val _id: String,
    val email: String,
    val username: String,
    val createdAt: String,
    val updatedAt: String
)