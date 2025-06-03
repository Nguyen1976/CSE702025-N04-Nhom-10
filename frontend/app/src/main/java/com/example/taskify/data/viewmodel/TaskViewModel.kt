package com.example.taskify.data.viewmodel

import kotlin.Result
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskify.data.repository.TaskRepository
import com.example.taskify.domain.model.taskModel.SubTaskRequest
import com.example.taskify.domain.model.taskModel.TaskRequest
import com.example.taskify.domain.model.taskModel.TaskResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSubtaskLoading = MutableStateFlow(false)
    val isSubtaskLoading = _isSubtaskLoading.asStateFlow()

    private val _taskList = MutableStateFlow<List<TaskResponse>>(emptyList())
    val taskList = _taskList.asStateFlow()

    private val _createTaskResult = MutableStateFlow<Result<Unit>?>(null)
    val createTaskResult = _createTaskResult.asStateFlow()

    private val _updateTaskResult = MutableStateFlow<Result<TaskResponse>?>(null)
    val updateTaskResult = _updateTaskResult.asStateFlow()

    private val _deleteTaskResult = MutableStateFlow<Result<Unit>?>(null)
    val deleteTaskResult = _deleteTaskResult.asStateFlow()

    private val _isDragDropUpdate = MutableStateFlow(false)
    val isDragDropUpdate = _isDragDropUpdate.asStateFlow()

    fun createTask(task: TaskRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createTask(task)
            _createTaskResult.value = result.map { Unit }
            _isLoading.value = false
        }
    }

    fun getTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getTasks()
            result.onSuccess { tasks ->
                _taskList.value = tasks
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
            _isLoading.value = false
        }
    }

    fun updateTask(task: TaskResponse) {
        viewModelScope.launch {
            _isLoading.value = true

            val subTasksRequest = task.subtasks.map { subtaskResponse ->
                SubTaskRequest(
                    title = subtaskResponse.title,
                    subtaskDes = subtaskResponse.subtaskDes
                )
            }

            val taskRequest = TaskRequest(
                title = task.title,
                description = task.description,
                subtasks = subTasksRequest,
                taskDate = LocalDate.parse(task.taskDate),
                taskTime = LocalTime.parse(task.taskTime),
                type = task.type,
                isSuccess = task.isSuccess
            )

            val updateResult = repository.updateTask(task._id, taskRequest)
            _updateTaskResult.value = updateResult

            updateResult.onSuccess { updatedTask ->
                val currentList = _taskList.value.toMutableList()
                val index = currentList.indexOfFirst { it._id == updatedTask._id }
                if (index != -1) {
                    currentList[index] = updatedTask
                    _taskList.value = currentList
                } else {
                    val getResult = repository.getTasks()
                    getResult.onSuccess { tasks ->
                        _taskList.value = tasks
                    }
                }
            }.onFailure { error ->
                Log.e("TaskViewModel", "Update task failed: ${error.message}")
                _errorMessage.value = error.message
            }

            _isLoading.value = false
        }
    }

    fun updateTaskFromDragDrop(task: TaskResponse) {
        viewModelScope.launch {
            _isDragDropUpdate.value = true
            _isLoading.value = true

            val subTasksRequest = task.subtasks.map { subtaskResponse ->
                SubTaskRequest(
                    title = subtaskResponse.title,
                    subtaskDes = subtaskResponse.subtaskDes
                )
            }

            val taskRequest = TaskRequest(
                title = task.title,
                description = task.description,
                subtasks = subTasksRequest,
                taskDate = LocalDate.parse(task.taskDate),
                taskTime = LocalTime.parse(task.taskTime),
                type = task.type,
                isSuccess = task.isSuccess
            )

            val updateResult = repository.updateTask(task._id, taskRequest)

            _isDragDropUpdate.value = false

            _updateTaskResult.value = updateResult

            updateResult.onSuccess { updatedTask ->
                val currentList = _taskList.value.toMutableList()
                val index = currentList.indexOfFirst { it._id == updatedTask._id }
                if (index != -1) {
                    currentList[index] = updatedTask
                    _taskList.value = currentList
                } else {
                    val getResult = repository.getTasks()
                    getResult.onSuccess { tasks ->
                        _taskList.value = tasks
                    }
                }
            }.onFailure { error ->
                Log.e("TaskViewModel", "Update task failed: ${error.message}")
                _errorMessage.value = error.message
            }

            _isLoading.value = false
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteTask(taskId)
            _deleteTaskResult.value = result

            result.onSuccess {
                getTasks()
            }.onFailure { error ->
                _errorMessage.value = error.message
                _isLoading.value = false
            }
        }
    }

    fun resetCreateTaskResult() {
        _createTaskResult.value = null
    }

    fun resetUpdateTaskResult() {
        _updateTaskResult.value = null
    }

    fun resetDeleteTaskResult() {
        _deleteTaskResult.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun deleteSubtask(task: TaskResponse, subtaskIndex: Int) {
        viewModelScope.launch {
            _isSubtaskLoading.value = true

            val updatedSubtasks = task.subtasks.toMutableList().apply {
                if (subtaskIndex in indices) removeAt(subtaskIndex)
            }

            val subTasksRequest = updatedSubtasks.map { subtaskResponse ->
                SubTaskRequest(
                    title = subtaskResponse.title,
                    subtaskDes = subtaskResponse.subtaskDes
                )
            }

            val updatedTaskRequest = TaskRequest(
                title = task.title,
                description = task.description,
                subtasks = subTasksRequest,
                taskDate = LocalDate.parse(task.taskDate),
                taskTime = LocalTime.parse(task.taskTime),
                type = task.type,
                isSuccess = task.isSuccess
            )

            val result = repository.updateTask(task._id, updatedTaskRequest)
            _updateTaskResult.value = result

            result.onSuccess {
                val getResult = repository.getTasks()
                getResult.onSuccess { tasks ->
                    _taskList.value = tasks
                }
            }.onFailure { error ->
                _errorMessage.value = error.message
            }

            _isSubtaskLoading.value = false
        }
    }
}