package com.example.taskify.domain.model.userModel

import java.time.Instant

data class UserResponse(
    val _id: String,
    val email: String,
    val username: String,
    val createAt: Instant,
    val updateAt: Instant
)