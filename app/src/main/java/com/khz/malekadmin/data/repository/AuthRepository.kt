package com.khz.malekadmin.data.repository

import com.khz.malekadmin.core.local.SessionManager
import com.khz.malekadmin.core.network.ApiErrorHandler
import com.khz.malekadmin.core.network.JsonParser.unwrap
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.dto.request.ChangePasswordRequest
import com.khz.malekadmin.data.dto.request.LoginRequest
import com.khz.malekadmin.data.dto.response.UserDto
import com.khz.malekadmin.data.remote.AuthApi
import com.khz.malekadmin.domain.mapper.toDomain
import com.khz.malekadmin.domain.model.User
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