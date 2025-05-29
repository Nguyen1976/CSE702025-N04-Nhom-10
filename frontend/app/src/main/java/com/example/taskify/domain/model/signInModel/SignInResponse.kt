package com.example.taskify.domain.model.signInModel

import java.time.Instant

data class SignInResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User,
    val createAt: Instant,
    val updateAt: Instant
)

data class User (
    val _id: String,
    val username: String,
    val email: String
)