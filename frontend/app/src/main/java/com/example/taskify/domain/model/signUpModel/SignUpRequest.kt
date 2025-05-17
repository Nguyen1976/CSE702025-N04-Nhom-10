package com.example.taskify.domain.model.signUpModel

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String
)