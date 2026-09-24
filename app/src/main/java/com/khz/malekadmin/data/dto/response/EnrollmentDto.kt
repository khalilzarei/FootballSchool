package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class EnrollmentDto(
    @SerializedName("id") val id: Int,
    @SerializedName("class_id") val classId: Int,
    // نال‌پذیر: Gson برای کلید غایب مقدار پیش‌فرض کاتلین را اعمال نمی‌کند
    @SerializedName("class") val classItem: ClassDto? = null,
    @SerializedName("player_id") val playerId: Int,
    // نال‌پذیر: ممکن است در برخی پاسخ‌های قدیمی سرور ارسال نشود
    @SerializedName("player") val player: PlayerDto? = null,
    @SerializedName("status") val status: String = "active",
    @SerializedName("is_active") val isActive: Boolean = false,
    @SerializedName("enrolled_at") val enrolledAt: String?,
    @SerializedName("ended_at") val endedAt: String?,
    @SerializedName("monthly_fee_override") val monthlyFeeOverride: Long?,
    @SerializedName("seasonal_fee_override") val seasonalFeeOverride: Long? = null,
    @SerializedName("session_fee_override") val sessionFeeOverride: Long?,
    @SerializedName("registration_fee_override") val registrationFeeOverride: Long?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * نتیجه ثبت‌نام گروهی بر اساس گروه سنی
 * POST /classes/{id}/enroll-age-group
 */
data class EnrollAgeGroupResultDto(
    @SerializedName("age_group_id") val ageGroupId: Int? = null,
    @SerializedName("total_players") val totalPlayers: Int = 0,
    @SerializedName("created") val created: Int = 0,
    @SerializedName("skipped_existing") val skippedExisting: Int = 0,
    @SerializedName("skipped_capacity") val skippedCapacity: Int = 0
)