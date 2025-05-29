package com.example.taskify.domain.model.taskModel

import java.time.LocalDate
import java.time.LocalTime

data class TaskRequest(
    val title: String,
    val description: String,
    val taskDate: LocalDate,
    val taskTime: LocalTime,
    val type: String,
    val isSuccess: Boolean,
    val subTasks: List<SubTaskRequest>? = null
)

data class SubTaskRequest (
    val title: String,
    val subTaskDes: String
)