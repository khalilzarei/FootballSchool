package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("password") val password: String
)

data class ChangePasswordRequest(
    @SerializedName("old_password") val oldPassword: String,
    @SerializedName("new_password") val newPassword: String,
    @SerializedName("new_password_confirmation") val newPasswordConfirmation: String
)

data class CreateUserRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("national_code") val nationalCode: String?,
    @SerializedName("role") val role: String,
    @SerializedName("status") val status: String,
    @SerializedName("password") val password: String
)


data class ResetPasswordRequest(
    @SerializedName("password") val password: String?
)