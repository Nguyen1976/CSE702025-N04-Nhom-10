package com.example.taskify.data.remote

import com.example.taskify.domain.model.taskModel.TaskRequest
import com.example.taskify.domain.model.taskModel.TaskResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TaskApi {
    @POST("/api/tasks")
    suspend fun createTask(@Body request: TaskRequest): Response<TaskResponse>

    @GET("/api/tasks")
    suspend fun getTasks(): Response<List<TaskResponse>>
}