package com.example.taskify.data.viewmodel

import android.util.Log
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

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    fun setUsername(value: String) {
        _username.value = value
    }

    private val _updateUserState = MutableStateFlow<UpdateUsernameState>(UpdateUsernameState.Idle)
    val updateUserState: StateFlow<UpdateUsernameState> = _updateUserState

    private val _oldPassword = MutableStateFlow("")
    val oldPassword: StateFlow<String> = _oldPassword

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword

    private val _updatePasswordState = MutableStateFlow<UpdatePasswordState>(UpdatePasswordState.Idle)
    val updatePasswordState: StateFlow<UpdatePasswordState> = _updatePasswordState

    fun setOldPassword(value: String) {
        _oldPassword.value = value
    }

    fun setNewPassword(value: String) {
        _newPassword.value = value
    }

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

    fun updateUsername() {
        viewModelScope.launch {
            _updateUserState.value = UpdateUsernameState.Loading

            val request = UserRepository.UsernameUpdateRequest(username = _username.value)
            val result = userRepository.updateUsername(request)

            if (result.isSuccess) {
                val updatedUser = result.getOrNull()
                _userState.value = updatedUser
                _error.value = null
                updatedUser?.let { userPreferences.saveUser(it) }

                _updateUserState.value = UpdateUsernameState.Success
            } else {
                val errorMsg = result.exceptionOrNull()?.message
                _error.value = errorMsg
                _updateUserState.value = UpdateUsernameState.Error(errorMsg)
            }
        }
    }

    fun updatePassword() {
        viewModelScope.launch {
            _updatePasswordState.value = UpdatePasswordState.Loading

            val request = UserRepository.PasswordUpdateRequest(
                oldPassword = _oldPassword.value,
                password = _newPassword.value
            )

            val result = userRepository.updatePassword(request)

            if (result.isSuccess) {
                val updatedUser = result.getOrNull()
                _userState.value = updatedUser
                _error.value = null
                updatedUser?.let { userPreferences.saveUser(it) }
                _updatePasswordState.value = UpdatePasswordState.Success
            } else {
                val errorMsg = result.exceptionOrNull()?.message
                _error.value = errorMsg
                _updatePasswordState.value = UpdatePasswordState.Error(errorMsg)
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

sealed class UpdateUsernameState {
    object Idle : UpdateUsernameState()
    object Loading : UpdateUsernameState()
    object Success : UpdateUsernameState()
    data class Error(val message: String?) : UpdateUsernameState()
}

sealed class UpdatePasswordState {
    object Idle : UpdatePasswordState()
    object Loading : UpdatePasswordState()
    object Success : UpdatePasswordState()
    data class Error(val message: String?) : UpdatePasswordState()
}