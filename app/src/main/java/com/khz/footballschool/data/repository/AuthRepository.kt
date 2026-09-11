package com.khz.footballschool.data.repository

import com.khz.footballschool.core.local.SessionManager
import com.khz.footballschool.core.network.ApiErrorHandler
import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.ChangePasswordRequest
import com.khz.footballschool.data.dto.request.LoginRequest
import com.khz.footballschool.data.dto.response.UserDto
import com.khz.footballschool.data.remote.AuthApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.User
import kotlinx.coroutines.flow.first

class AuthRepository(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) {

    suspend fun login(
        identifier: String,
        password: String,
        rememberMe: Boolean
    ): NetworkResult<User> {
        return try {
            sessionManager.setRememberMe(rememberMe)
            val response = authApi.login(LoginRequest(identifier, password))
            if (response.success && response.data != null) {
                sessionManager.saveToken(response.data.token)
                sessionManager.saveUserInfo(
                    response.data.user.id.toString(),
                    response.data.user.role
                )
                NetworkResult.Success(response.data.user.toDomain())
            } else {
                NetworkResult.Error(response.message ?: "خطا در احراز هویت")
            }
        } catch (e: Exception) {
            // پاک کردن rememberMe در صورت خطا
            sessionManager.setRememberMe(false)
            NetworkResult.Error(ApiErrorHandler.extractMessage(e))
        }
    }

    suspend fun getCurrentUser(): NetworkResult<User> {
        return try {
            val response = authApi.getCurrentUser()
            if (response.success && response.data != null) {
                val userDto = response.data.unwrap<UserDto>("user")
                if (userDto != null) NetworkResult.Success(userDto.toDomain())
                else NetworkResult.Error("خطا در پارس اطلاعات کاربر")
            } else {
                NetworkResult.Error(response.message ?: "خطا در دریافت اطلاعات کاربر")
            }
        } catch (e: Exception) {
            NetworkResult.Error(ApiErrorHandler.extractMessage(e))
        }
    }

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): NetworkResult<Unit> {
        return try {
            val response = authApi.changePassword(
                ChangePasswordRequest(oldPassword, newPassword, confirmPassword)
            )
            if (response.success) NetworkResult.Success(Unit)
            else NetworkResult.Error(response.message ?: "خطا در تغییر رمز")
        } catch (e: Exception) {
            NetworkResult.Error(ApiErrorHandler.extractMessage(e))
        }
    }

    suspend fun logout(): NetworkResult<Unit> {
        return try {
            authApi.logout()
            sessionManager.clearSession()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            sessionManager.clearSession()
            // در logout حتی خطا هم موفق محسوب می‌شود
            NetworkResult.Success(Unit)
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        return try {
            val token = sessionManager.authToken.first()
            !token.isNullOrBlank()
        } catch (e: Exception) {
            false
        }
    }
}