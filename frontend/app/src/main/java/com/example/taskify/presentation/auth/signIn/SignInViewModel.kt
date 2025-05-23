package com.example.taskify.presentation.auth.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskify.data.repository.AuthRepository
import com.example.taskify.domain.model.signInModel.SignInRequest
import com.example.taskify.domain.model.signInModel.SignInResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _signInState = MutableStateFlow<UiState<SignInResponse>>(UiState.Idle)
    val signInState: StateFlow<UiState<SignInResponse>> = _signInState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _signInState.value = UiState.Loading

            val request = SignInRequest(email, password)
            val result = repository.signIn(request)

            _signInState.value = when {
                result.isSuccess -> {
                    val response = result.getOrThrow()
                    UiState.Success(response)
                }

                result.isFailure -> UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                else -> UiState.Error("Unknown error")
            }
        }
    }
}

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}