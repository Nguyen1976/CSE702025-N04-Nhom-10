package com.example.taskify.domain.model.taskModel

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class TaskRequest(
    val title: String,
    val description: String,
    val createAt: LocalDateTime,
    val taskDate: LocalDate,
    val taskTime: LocalTime,
    val type: String,
    val isSuccess: Boolean
)

data class SubTaskRequest (
    val title: String,
    val subTaskDes: String
)