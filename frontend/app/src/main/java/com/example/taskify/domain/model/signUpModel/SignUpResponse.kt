package com.example.taskify.domain.model.signUpModel

import java.time.Instant

data class SignUpResponse(
    val user: User,
    val createAt: Instant,
    val updateAt: Instant
)

data class User(
    val _id: String,
    val username: String,
    val email: String
)