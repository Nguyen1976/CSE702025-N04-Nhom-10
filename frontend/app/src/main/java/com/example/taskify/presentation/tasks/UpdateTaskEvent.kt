package com.example.taskify.presentation.tasks

import com.example.taskify.domain.model.taskModel.TaskResponse

sealed class UpdateTaskEvent {
    data class Normal(val result: Result<TaskResponse>) : UpdateTaskEvent()
    data class DragDrop(val result: Result<TaskResponse>) : UpdateTaskEvent()
}