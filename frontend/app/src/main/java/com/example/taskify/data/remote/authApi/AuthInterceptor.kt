package com.example.taskify.data.remote.authApi

import android.util.Log
import com.example.taskify.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    @Named("withoutInterceptor") private val authApi: AuthApi
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val accessToken = runBlocking { tokenManager.getAccessToken() }

        if (!accessToken.isNullOrEmpty()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }

        val response = chain.proceed(request)

        if (response.code == 401 || response.code == 410) {
            response.close()

            val refreshToken = runBlocking { tokenManager.getRefreshToken() } ?: return response

            val refreshResponse = runBlocking {
                try {
                    authApi.refreshToken(RefreshTokenRequest(refreshToken))
                } catch (e: Exception) {
                    null
                }
            }

            if (refreshResponse?.isSuccessful == true) {
                val newAccessToken = refreshResponse.body()?.accessToken
                if (!newAccessToken.isNullOrEmpty()) {
                    runBlocking { tokenManager.saveAccessToken(newAccessToken) }

                    val newRequest = request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()

                    return chain.proceed(newRequest)
                }
            }

            return response
        }

        return response
    }
}