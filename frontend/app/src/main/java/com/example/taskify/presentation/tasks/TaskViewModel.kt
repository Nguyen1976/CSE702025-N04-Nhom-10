package com.example.taskify.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskify.domain.model.taskModel.SubTaskRequest
import com.example.taskify.domain.model.taskModel.TaskRequest
import com.example.taskify.domain.model.taskModel.TaskResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
): ViewModel() {
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Task
    private val _taskResult = MutableStateFlow<Result<Unit>?>(null)
    val taskResult = _taskResult.asStateFlow()

    private val _taskList = MutableStateFlow<List<com.example.taskify.domain.model.taskModel.TaskResponse>>(emptyList())
    val taskList = _taskList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _subtaskResult = MutableStateFlow<Result<Unit>?>(null)
    val subtaskResult = _subtaskResult.asStateFlow()

    private val _isSubtaskLoading = MutableStateFlow(false)
    val isSubtaskLoading = _isSubtaskLoading.asStateFlow()


    fun createTask(task: TaskRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createTask(task)
            _taskResult.value = result.map { Unit }
            _isLoading.value = false
        }
    }

    fun getTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getTasks()
            result.onSuccess { tasks ->
                _taskList.value = tasks
            }.onFailure { }
            _isLoading.value = false
        }
    }

    fun updateTask(task: TaskResponse) {
        viewModelScope.launch {
            _isLoading.value = true

            val rawCreateAt = task.createAt

            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)

            val subTasksRequest = task.subTasks.map { subtaskResponse ->
                SubTaskRequest(
                    title = subtaskResponse.title,
                    subTaskDes = subtaskResponse.subtaskDes
                )
            }

            val taskRequest = TaskRequest(
                title = task.title,
                description = task.description,
                subTasks = subTasksRequest,
                taskDate = LocalDate.parse(task.taskDate),
                taskTime = LocalTime.parse(task.taskTime),
                type = task.type,
                isSuccess = task.isSuccess
            )

            val result = repository.updateTask(task._id, taskRequest)
            result.onSuccess {
                val getResult = repository.getTasks()
                getResult.onSuccess { tasks ->
                    _taskList.value = tasks
                }.onFailure {
                    // handle error
                }
                _isLoading.value = false
            }.onFailure {
                // handle error
                _isLoading.value = false
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteTask(taskId)
            result.onSuccess {
                getTasks()
            }.onFailure {
                _isLoading.value = false
            }
        }
    }

    fun resetTaskResult() {
        _taskResult.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}