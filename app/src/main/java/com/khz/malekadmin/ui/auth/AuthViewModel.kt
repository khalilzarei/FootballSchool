package com.khz.malekadmin.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val changePasswordState: StateFlow<ChangePasswordState> = _changePasswordState

    fun login(identifier: String, password: String, rememberMe: Boolean) {
        _loginState.value = AuthState.Loading
        viewModelScope.launch {
            when (val loginResult = authRepository.login(identifier, password, rememberMe)) {
                is NetworkResult.Success -> {
                    val user = loginResult.data
                    _loginState.value = if (user.mustChangePassword) {
                        AuthState.RequirePasswordChange
                    } else {
                        AuthState.Success
                    }
                }
                is NetworkResult.Error -> {
                    _loginState.value = AuthState.Error(loginResult.message)
                }
                else -> {}
            }
        }
    }

    suspend fun isUserLoggedIn(): Boolean = authRepository.isUserLoggedIn()

    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String) {
        // اعتبارسنجی سمت کلاینت
        if (newPassword != confirmPassword) {
            _changePasswordState.value = ChangePasswordState.Error("تکرار رمز عبور مطابقت ندارد")
            return
        }
        if (newPassword.length < 6) {
            _changePasswordState.value = ChangePasswordState.Error("رمز عبور باید حداقل ۶ کاراکتر باشد")
            return
        }

        _changePasswordState.value = ChangePasswordState.Loading
        viewModelScope.launch {
            when (val result = authRepository.changePassword(oldPassword, newPassword, confirmPassword)) {
                is NetworkResult.Success -> {
                    _changePasswordState.value = ChangePasswordState.Success
                }
                is NetworkResult.Error -> {
                    _changePasswordState.value = ChangePasswordState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    fun resetChangePasswordState() {
        _changePasswordState.value = ChangePasswordState.Idle
    }
}


sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}