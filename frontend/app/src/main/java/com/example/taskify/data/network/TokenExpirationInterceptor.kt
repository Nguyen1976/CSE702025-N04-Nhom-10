package com.example.taskify.data.network

import okhttp3.Interceptor
import okhttp3.Response

object TokenExpirationHandler {
    private val listeners = mutableListOf<() -> Unit>()

    fun addListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun notifyTokenExpired() {
        listeners.forEach { it.invoke() }
    }
}

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if(response.code == 401) {
            TokenExpirationHandler.notifyTokenExpired()
        }

        return response
    }
}