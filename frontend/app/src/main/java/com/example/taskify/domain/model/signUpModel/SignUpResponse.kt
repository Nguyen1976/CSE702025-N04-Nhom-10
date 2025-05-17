package com.example.taskify.domain.model.signUpModel

data class SignUpResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val username: String,
    val email: String
)
