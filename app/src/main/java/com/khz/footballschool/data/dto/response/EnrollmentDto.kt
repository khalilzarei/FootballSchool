package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class EnrollmentDto(
    @SerializedName("id") val id: Int,
    @SerializedName("class_id") val classId: Int,
    @SerializedName("class") val classItem: ClassDto,
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("player") val player: PlayerDto,
    @SerializedName("status") val status: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("enrolled_at") val enrolledAt: String?,
    @SerializedName("ended_at") val endedAt: String?,
    @SerializedName("monthly_fee_override") val monthlyFeeOverride: Long?,
    @SerializedName("session_fee_override") val sessionFeeOverride: Long?,
    @SerializedName("registration_fee_override") val registrationFeeOverride: Long?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)