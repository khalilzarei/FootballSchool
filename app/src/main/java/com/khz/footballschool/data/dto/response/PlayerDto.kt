package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class PlayerDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("first_name") val firstName: String = "",
    @SerializedName("last_name") val lastName: String = "",
    @SerializedName("full_name") val fullName: String? = null,       // nullable
    @SerializedName("national_code") val nationalCode: String? = null,
    @SerializedName("birth_date") val birthDate: String? = null,
    @SerializedName("age") val age: Int? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("status") val status: String = "active",
    @SerializedName("medical_notes") val medicalNotes: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("avatar_path") val avatarPath: String? = null,   // ← مهم
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("created_by") val createdBy: Int? = null,        // ← جدید
    @SerializedName("created_at") val createdAt: String? = null,     // ← جدید
    @SerializedName("updated_at") val updatedAt: String? = null,     // ← جدید
    @SerializedName("deleted_at") val deletedAt: String? = null,     // ← جدید
    @SerializedName("deleted_seq") val deletedSeq: Int? = 0,         // ← جدید

    // فیلدهای رابطه‌ای (ممکن است سرور برنگرداند)
    @SerializedName("guardians") val guardians: List<GuardianPlayerDto>? = null,
    @SerializedName("current_class") val currentClass: ClassDto? = null,
    @SerializedName("balance") val balance: PlayerBalanceDto? = null
)