package com.example.taskify.data.remote.authApi

import android.util.Log
import com.example.taskify.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    private val retrofitWithoutInterceptor = Retrofit.Builder()
        .baseUrl("http://192.168.100.211:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    private val authApiServiceWithoutInterceptor = retrofitWithoutInterceptor.create(AuthApi::class.java)

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val accessToken = runBlocking { tokenManager.getAccessToken() }
        Log.d("AccessToken", "$accessToken")

        if (!accessToken.isNullOrEmpty()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }

        val response = chain.proceed(request)

        if (response.code == 401) {
            response.close()

            val refreshToken = runBlocking { tokenManager.getRefreshToken() } ?: return response

            val refreshResponse = runBlocking {
                try {
                    authApiServiceWithoutInterceptor.refreshToken(RefreshTokenRequest(refreshToken))
                } catch (e: Exception) {
                    null
                }
            }

            if (refreshResponse != null && refreshResponse.isSuccessful) {
                val newAccessToken = refreshResponse.body()?.accessToken

                if (!newAccessToken.isNullOrEmpty()) {
                    runBlocking {
                        tokenManager.saveAccessToken(newAccessToken)
                    }

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