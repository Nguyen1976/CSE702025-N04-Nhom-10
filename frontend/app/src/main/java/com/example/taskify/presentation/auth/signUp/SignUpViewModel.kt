package com.example.taskify.presentation.auth.signUp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskify.data.repository.AuthRepository
import com.example.taskify.domain.model.signUpModel.SignUpRequest
import kotlinx.coroutines.launch

class SignUpViewModel (
    private val repository: AuthRepository
) : ViewModel() {
    var email by mutableStateOf("")
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var errorText by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var success by mutableStateOf(false)

    fun signUp() {
        viewModelScope.launch {
            isLoading = true
            val result = repository.signUp(SignUpRequest(username, email, password))
            isLoading = false
            result.onSuccess {
                success = true
                errorText = null
                // Lưu token nếu cần
            }.onFailure {
                errorText = it.message
            }
        }
    }
}