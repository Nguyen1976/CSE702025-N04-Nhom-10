package com.example.taskify.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskify.data.local.UserPreferences
import com.example.taskify.data.repository.UserRepository
import com.example.taskify.domain.model.userModel.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _userState = MutableStateFlow<UserResponse?>(null)
    val userState: StateFlow<UserResponse?> = _userState

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadCurrentUser() {
        viewModelScope.launch {
            val result = userRepository.getCurrentUser()
            if (result.isSuccess) {
                val user = result.getOrNull()
                _userState.value = user
                _error.value = null

                // Save on DataStore
                user?.let { userPreferences.saveUser(it) }
            } else {
                _error.value = result.exceptionOrNull()?.message
                _userState.value = null
            }
        }
    }

    fun getUserFromLocal() {
        viewModelScope.launch {
            userPreferences.getUser().collect {
                _userState.value = it
            }
        }
    }

    fun clearUser() {
        viewModelScope.launch {
            userPreferences.clearUser()
            _userState.value = null
        }
    }
}