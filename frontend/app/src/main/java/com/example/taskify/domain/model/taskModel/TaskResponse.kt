package com.example.taskify.domain.model.taskModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class TaskResponse(
    val _id: String,
    val userId: String,
    val title: String,
    val description: String,
    val createAt: String?,
    val taskDate: String,
    val taskTime: String,
    val type: String,
    val isSuccess: Boolean,
    val subtasks: List<SubtaskResponse>,
)

@Parcelize
data class SubtaskResponse(
    val title: String,
    val subtaskDes: String
) : Parcelable