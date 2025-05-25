package com.example.taskify.presentation.tasks

import com.example.taskify.data.remote.TaskApi
import com.example.taskify.domain.model.signUpModel.ErrorResponse
import com.example.taskify.domain.model.taskModel.SubTaskRequest
import com.example.taskify.domain.model.taskModel.SubtaskResponse
import com.example.taskify.domain.model.taskModel.TaskRequest
import com.example.taskify.domain.model.taskModel.TaskResponse
import com.google.gson.Gson
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val api: TaskApi
) {
    // Task
    suspend fun createTask(request: TaskRequest): Result<TaskResponse> {
        return try {
            val response = api.createTask(request)
            if(response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = Gson().fromJson(errorBody, ErrorResponse::class.java).error
                Result.failure(Exception(errorMessage))
            }
        } catch (e:Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasks(): Result<List<TaskResponse>> {
        return try {
            val response = api.getTasks()
            if (response.isSuccessful) {
                val tasks = response.body() ?: emptyList()
                Result.success(tasks)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = Gson().fromJson(errorBody, ErrorResponse::class.java).error
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(taskId: String, task: TaskRequest): Result<TaskResponse> {
        return try {
            val response = api.updateTask(taskId, task)
            if(response.isSuccessful) {
                val updatedTask = response.body() ?: return Result.failure(Exception("Empty response body"))
                Result.success(updatedTask)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = Gson().fromJson(errorBody, ErrorResponse::class.java).error
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            val response = api.deleteTask(taskId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = Gson().fromJson(errorBody, ErrorResponse::class.java).error
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}