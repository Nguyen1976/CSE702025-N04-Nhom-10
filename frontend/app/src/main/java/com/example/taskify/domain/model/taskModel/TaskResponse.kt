package com.example.taskify.domain.model.taskModel

data class TaskResponse(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val createAt: String,
    val taskDate: String,
    val taskTime: String,
    val type: String,
    val isSuccess: Boolean,
    val subTasks: List<SubtaskResponse>,
)

data class SubtaskResponse(
    val title: String,
    val subtaskDes: String
)