package com.example.taskify.di

import android.content.Context
import com.example.taskify.common.Constants
import com.example.taskify.data.local.TokenManager
import com.example.taskify.data.local.UserPreferences
import com.example.taskify.data.remote.TaskApi
import com.example.taskify.data.remote.UserApi
import com.example.taskify.data.remote.authApi.AuthApi
import com.example.taskify.data.remote.authApi.AuthInterceptor
import com.example.taskify.data.repository.UserRepository
import com.example.taskify.data.repository.TaskRepository
import com.example.taskify.util.LocalDateAdapter
import com.example.taskify.util.LocalTimeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = Constants.BASE_URL

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context)

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor =
        AuthInterceptor(tokenManager)

    @Provides
    @Singleton
    @Named("withInterceptor")
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("withoutInterceptor")
    fun provideOkHttpClientWithoutInterceptor(): OkHttpClient =
        OkHttpClient.Builder().build()

    @Provides
    @Singleton
    @Named("withInterceptor")
    fun provideRetrofit(
        @Named("withInterceptor") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("withoutInterceptor")
    fun provideRetrofitWithoutInterceptor(
        @Named("withoutInterceptor") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("withInterceptor")
    fun provideAuthApi(@Named("withInterceptor") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    @Named("withoutInterceptor")
    fun provideAuthApiWithoutInterceptor(@Named("withoutInterceptor") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideTaskApi(@Named("withInterceptor") retrofit: Retrofit): TaskApi =
        retrofit.create(TaskApi::class.java)

    @Provides
    @Singleton
    fun provideTaskRepository(api: TaskApi, gson: Gson): TaskRepository =
        TaskRepository(api, gson)

    @Provides
    @Singleton
    fun provideUserApi(@Named("withInterceptor") retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideUserRepository(api: UserApi): UserRepository =
        UserRepository(api)

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences =
        UserPreferences(context)

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter())
            .create()
    }
}