package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

data class CreateClassRequest(
    @SerializedName("title") val title: String,
    @SerializedName("age_group_id") val ageGroupId: Int?,
    @SerializedName("coach_id") val coachId: Int?,
    @SerializedName("assistant_coach_id") val assistantCoachId: Int?,
    @SerializedName("capacity") val capacity: Int?,
    @SerializedName("status") val status: String,
    @SerializedName("location") val location: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("pricing_type") val pricingType: String?,
    @SerializedName("monthly_fee") val monthlyFee: Long?,
    @SerializedName("session_fee") val sessionFee: Long?,
    @SerializedName("registration_fee") val registrationFee: Long?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?
)

data class UpdateClassRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("age_group_id") val ageGroupId: Int?,
    @SerializedName("coach_id") val coachId: Int?,
    @SerializedName("assistant_coach_id") val assistantCoachId: Int?,
    @SerializedName("capacity") val capacity: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("pricing_type") val pricingType: String?,
    @SerializedName("monthly_fee") val monthlyFee: Long?,
    @SerializedName("session_fee") val sessionFee: Long?,
    @SerializedName("registration_fee") val registrationFee: Long?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?
)

data class CreateScheduleRequest(
    @SerializedName("weekday") val weekday: Int,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("location") val location: String?,
    @SerializedName("status") val status: String
)

data class UpdateScheduleRequest(
    @SerializedName("weekday") val weekday: Int?,
    @SerializedName("start_time") val startTime: String?,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("status") val status: String?
)

data class EnrollPlayerRequest(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("status") val status: String,
    @SerializedName("enrolled_at") val enrolledAt: String?,
    @SerializedName("ended_at") val endedAt: String?,
    @SerializedName("monthly_fee_override") val monthlyFeeOverride: Long?,
    @SerializedName("session_fee_override") val sessionFeeOverride: Long?,
    @SerializedName("registration_fee_override") val registrationFeeOverride: Long?,
    @SerializedName("notes") val notes: String?
)

data class UpdateEnrollmentRequest(
    @SerializedName("status") val status: String?,
    @SerializedName("enrolled_at") val enrolledAt: String?,
    @SerializedName("ended_at") val endedAt: String?,
    @SerializedName("monthly_fee_override") val monthlyFeeOverride: Long?,
    @SerializedName("session_fee_override") val sessionFeeOverride: Long?,
    @SerializedName("registration_fee_override") val registrationFeeOverride: Long?,
    @SerializedName("notes") val notes: String?
)