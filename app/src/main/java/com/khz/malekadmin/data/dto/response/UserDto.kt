package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("mobile") val mobile: String? = null,
    @SerializedName("national_code") val nationalCode: String? = null,
    @SerializedName("role") val role: String,
    @SerializedName("status") val status: String,
    @SerializedName("must_change_password") val mustChangePassword: Int? = 0,
    @SerializedName("failed_login_count") val failedLoginCount: Int? = 0,
    @SerializedName("locked_until") val lockedUntil: String? = null,
    @SerializedName("last_login_at") val lastLoginAt: String? = null,
    @SerializedName("created_by") val createdBy: Int? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("deleted_at") val deletedAt: String? = null,
    @SerializedName("deleted_seq") val deletedSeq: Int? = 0,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("avatar_path") val avatarPath: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
)