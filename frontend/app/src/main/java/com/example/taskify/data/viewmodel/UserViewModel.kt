package com.example.taskify.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskify.data.repository.UserRepository
import com.example.taskify.domain.model.signInModel.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadCurrentUser() {
        viewModelScope.launch {
            val result = userRepository.getCurrentUser()
            if (result.isSuccess) {
                _userState.value = result.getOrNull()
                _error.value = null
            } else {
                _error.value = result.exceptionOrNull()?.message
                _userState.value = null
            }
        }
    }
}