package com.khz.footballschool.ui.auth

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object RequirePasswordChange : AuthState()
    data class Error(val message: String) : AuthState()
}