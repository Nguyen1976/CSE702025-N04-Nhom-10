package com.example.taskify.domain.model.taskModel

import com.google.gson.annotations.SerializedName

data class TaskResponse(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val createAt: String,
    val taskDate: String,
    val taskTime: String,
    val type: String,
    @SerializedName("success")
    val isSuccess: Boolean,
    val subTasks: List<SubtaskResponse>,
)

data class SubtaskResponse(
    val title: String,
    val subtaskDes: String
)