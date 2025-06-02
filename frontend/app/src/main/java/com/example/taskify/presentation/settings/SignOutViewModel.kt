package com.example.taskify.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskify.data.local.TokenManager
import com.example.taskify.data.local.UserPreferences
import com.example.taskify.data.remote.authApi.AuthApi
import com.example.taskify.data.remote.authApi.LogoutRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SignOutViewModel @Inject constructor(
    @Named("withInterceptor") private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState.asStateFlow()

    sealed class LogoutState {
        object Idle : LogoutState()
        object Loading : LogoutState()
        data class Success(val message: String) : LogoutState()
        data class Error(val error: String) : LogoutState()
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading
            try {
                val accessToken = tokenManager.getAccessToken() ?: ""
                val refreshToken = tokenManager.getRefreshToken() ?: ""

                if (accessToken.isEmpty() || refreshToken.isEmpty()) {
                    _logoutState.value = LogoutState.Error("Tokens missing")
                    clearLocalData()
                    return@launch
                }

                val response = authApi.signOut(LogoutRequest(accessToken, refreshToken))
                if (response.isSuccessful) {
                    val logoutResponse = response.body()
                    clearLocalData()
                    _logoutState.value = LogoutState.Success(logoutResponse?.message ?: "Log out successful")
                } else {
                    _logoutState.value = LogoutState.Error("Logout failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _logoutState.value = LogoutState.Error("Error: ${e.message}")
            }
        }
    }

    private suspend fun clearLocalData() {
        tokenManager.clearTokens()
        userPreferences.clearUser()
    }
}