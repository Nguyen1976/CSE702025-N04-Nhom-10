package com.example.taskify.domain.model.taskModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
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
    @SerializedName("success")
    val isSuccess: Boolean,
    val subTasks: List<SubtaskResponse>,
)

@Parcelize
data class SubtaskResponse(
    val title: String,
    val subtaskDes: String
) : Parcelable