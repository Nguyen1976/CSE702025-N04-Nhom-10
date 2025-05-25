package com.example.taskify.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskify.domain.model.taskModel.TaskRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
): ViewModel() {

    private val _taskResult = MutableStateFlow<Result<Unit>?>(null)
    val taskResult = _taskResult.asStateFlow()

    private val _taskList = MutableStateFlow<List<com.example.taskify.domain.model.taskModel.TaskResponse>>(emptyList())
    val taskList = _taskList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

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

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteTask(taskId)
            result.onSuccess {
                getTasks()
            }.onFailure {
                // Error
            }
            _isLoading.value = false
        }
    }

    fun resetTaskResult() {
        _taskResult.value = null
    }
}