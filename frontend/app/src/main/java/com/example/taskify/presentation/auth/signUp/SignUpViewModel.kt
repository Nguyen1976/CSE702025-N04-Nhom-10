package com.example.taskify.presentation.auth.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskify.data.repository.AuthRepository
import com.example.taskify.domain.model.signUpModel.SignUpRequest
import com.example.taskify.domain.model.signUpModel.SignUpResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _signUpState = MutableStateFlow<UiState<SignUpResponse>>(UiState.Idle)
    val signUpState: StateFlow<UiState<SignUpResponse>> = _signUpState.asStateFlow()

    fun signUp(email: String, username: String, password: String) {
        viewModelScope.launch {
            _signUpState.value = UiState.Loading
            val result = repository.signUp(SignUpRequest(username, email, password))
            _signUpState.value = when {
                result.isSuccess -> UiState.Success(result.getOrNull()!!)
                else -> UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun resetSignUpState() {
        _signUpState.value = UiState.Idle
    }
}