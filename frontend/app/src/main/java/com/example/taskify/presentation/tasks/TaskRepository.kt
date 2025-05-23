package com.example.taskify.presentation.tasks

import com.example.taskify.data.remote.TaskApi
import com.example.taskify.domain.model.signUpModel.ErrorResponse
import com.example.taskify.domain.model.taskModel.TaskRequest
import com.example.taskify.domain.model.taskModel.TaskResponse
import com.google.gson.Gson
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val api: TaskApi
) {
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
}