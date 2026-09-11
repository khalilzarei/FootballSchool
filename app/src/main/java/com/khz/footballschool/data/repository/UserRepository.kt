package com.khz.footballschool.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.khz.footballschool.core.network.ApiErrorHandler
import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.network.PaginatedResponse
import com.khz.footballschool.core.util.MultipartHelper
import com.khz.footballschool.data.dto.request.ResetPasswordRequest
import com.khz.footballschool.data.dto.request.UpdateUserRequest
import com.khz.footballschool.data.dto.response.UserDto
import com.khz.footballschool.data.remote.UserApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.User
import okhttp3.MultipartBody

class UserRepository(private val api: UserApi) {

    companion object {
        private const val TAG = "UserRepository"
    }

    // ═════════════════════════════════════════════
    // List & Detail
    // ═════════════════════════════════════════════
    suspend fun getUsers(
        page: Int = 1,
        perPage: Int = 20,
        role: String? = null,
        status: String? = null,
        query: String? = null
    ): NetworkResult<PaginatedResponse<User>> = try {
        val r = api.getUsers(page, perPage, role, status, query)
        if (r.success && r.data != null) {
            NetworkResult.Success(
                PaginatedResponse(
                    items = r.data.items.map { it.toDomain() },
                    total = r.data.total,
                    page = r.data.page,
                    perPage = r.data.perPage
                )
            )
        } else {
            NetworkResult.Error(r.message ?: "خطا در دریافت لیست کاربران")
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun getUser(id: Int): NetworkResult<User> = try {
        val r = api.getUser(id)
        val dto = r.data.unwrap<UserDto>("user")
        if (r.success && dto != null) {
            NetworkResult.Success(dto.toDomain())
        } else {
            NetworkResult.Error(r.message ?: "خطا در دریافت اطلاعات کاربر")
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Create User (multipart با آواتار اختیاری)
    // ═════════════════════════════════════════════
    suspend fun createUser(
        fullName: String,
        mobile: String,
        nationalCode: String?,
        role: String,
        password: String,
        status: String = "active",
        avatarUri: Uri? = null,
        context: Context
    ): NetworkResult<User> = try {
        val parts = mutableListOf<MultipartBody.Part>()

        MultipartHelper.textPart("full_name", fullName)?.let { parts.add(it) }
        MultipartHelper.textPart("mobile", mobile)?.let { parts.add(it) }
        MultipartHelper.textPart("national_code", nationalCode)?.let { parts.add(it) }
        MultipartHelper.textPart("role", role)?.let { parts.add(it) }
        MultipartHelper.textPart("password", password)?.let { parts.add(it) }
        MultipartHelper.textPart("status", status)?.let { parts.add(it) }

        if (avatarUri != null) {
            MultipartHelper.filePart(context, avatarUri, "avatar")?.let { parts.add(it) }
        }

        Log.d(TAG, "createUser: ${parts.size} parts")

        val r = api.createUser(parts)
        val dto = r.data.unwrap<UserDto>("user")
        if (r.success && dto != null) {
            NetworkResult.Success(dto.toDomain())
        } else {
            NetworkResult.Error(r.message ?: "خطا در ایجاد کاربر")
        }
    } catch (e: Exception) {
        Log.e(TAG, "createUser error: ${e.message}", e)
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Update User (multipart با آواتار اختیاری)
    // ═════════════════════════════════════════════
    suspend fun updateUser(
        id: Int,
        fullName: String,
        mobile: String,
        nationalCode: String?,
        status: String,
        avatarUri: Uri? = null,
        context: Context
    ): NetworkResult<User> = try {
        // ─── مرحله ۱: آپدیت اطلاعات با JSON (سریع و قابل اعتماد) ───
        val request = UpdateUserRequest(
            fullName = fullName,
            mobile = mobile,
            nationalCode = nationalCode,
            status = status
        )

        Log.d(TAG, "updateUser: updating info for id=$id")
        val updateResult = api.updateUser(id, request)

        if (!updateResult.success) {
            return NetworkResult.Error(updateResult.message ?: "خطا در به‌روزرسانی کاربر")
        }

        // ─── مرحله ۲: آپلود آواتار اگر انتخاب شده ───
        if (avatarUri != null) {
            Log.d(TAG, "updateUser: uploading avatar for id=$id")
            val part = MultipartHelper.filePart(context, avatarUri, "avatar")
            if (part != null) {
                val avatarResult = api.uploadUserAvatar(id, part)
                if (!avatarResult.success) {
                    Log.w(TAG, "Avatar upload failed (but user updated): ${avatarResult.message}")
                    // آواتار شکست خورد ولی آپدیت موفق بود - ادامه می‌دهیم
                }
            } else {
                Log.w(TAG, "updateUser: cannot read avatar file")
            }
        }

        // ─── مرحله ۳: دریافت اطلاعات به‌روز کاربر ───
        getUser(id)

    } catch (e: Exception) {
        Log.e(TAG, "updateUser error: ${e.message}", e)
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Avatar Management
    // ═════════════════════════════════════════════
    suspend fun uploadAvatar(userId: Int, imageUri: Uri, context: Context): NetworkResult<User> = try {
        val part = MultipartHelper.filePart(context, imageUri, "avatar")
            ?: return NetworkResult.Error("خطا در خواندن تصویر")

        val r = api.uploadUserAvatar(userId, part)
        val dto = r.data.unwrap<UserDto>("user")
        if (r.success && dto != null) {
            NetworkResult.Success(dto.toDomain())
        } else {
            NetworkResult.Error(r.message ?: "خطا در آپلود آواتار")
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun deleteAvatar(userId: Int): NetworkResult<User> = try {
        val r = api.deleteUserAvatar(userId)
        if (r.success) getUser(userId)
        else NetworkResult.Error(r.message ?: "خطا در حذف آواتار")
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Self Avatar
    // ═════════════════════════════════════════════
    suspend fun uploadMyAvatar(imageUri: Uri, context: Context): NetworkResult<User> = try {
        val part = MultipartHelper.filePart(context, imageUri, "avatar")
            ?: return NetworkResult.Error("خطا در خواندن تصویر")

        val r = api.uploadMyAvatar(part)
        val dto = r.data.unwrap<UserDto>("user")
        if (r.success && dto != null) {
            NetworkResult.Success(dto.toDomain())
        } else {
            NetworkResult.Error(r.message ?: "خطا در آپلود آواتار")
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun deleteMyAvatar(): NetworkResult<Unit> = try {
        val r = api.deleteMyAvatar()
        if (r.success) NetworkResult.Success(Unit)
        else NetworkResult.Error(r.message ?: "خطا در حذف آواتار")
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Status Management
    // ═════════════════════════════════════════════
    suspend fun toggleStatus(id: Int, activate: Boolean): NetworkResult<Unit> = try {
        val r = if (activate) api.activateUser(id) else api.deactivateUser(id)
        if (r.success) {
            NetworkResult.Success(Unit)
        } else {
            NetworkResult.Error(r.message ?: "خطا در تغییر وضعیت")
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Password Reset
    // ═════════════════════════════════════════════
    suspend fun resetPassword(id: Int, password: String? = null): NetworkResult<String?> = try {
        val r = api.resetPassword(id, ResetPasswordRequest(password))
        if (r.success) {
            val newPassword = r.data.unwrap<com.khz.footballschool.data.dto.response.ResetPasswordResultDto>()?.password
            NetworkResult.Success(newPassword)
        } else {
            NetworkResult.Error(r.message ?: "خطا در ریست رمز")
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }
}