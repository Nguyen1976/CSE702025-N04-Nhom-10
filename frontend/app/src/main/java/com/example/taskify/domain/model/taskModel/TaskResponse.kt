package com.example.taskify.domain.model.taskModel

data class TaskResponse(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val subTasks: List<SubtaskResponse>,
    val createAt: String,
    val dueDate: String,
    val type: String,
    val isSuccess: Boolean
)

data class SubtaskResponse(
    val title: String,
    val subtaskDes: String
)