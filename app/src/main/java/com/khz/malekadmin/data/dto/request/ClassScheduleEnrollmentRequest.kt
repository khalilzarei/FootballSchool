package com.khz.malekadmin.data.dto.request

import com.google.gson.annotations.SerializedName

data class CreateClassRequest(
    @SerializedName("title") val title: String,
    @SerializedName("season_id") val seasonId: Int? = null,
    @SerializedName("age_group_id") val ageGroupId: Int? = null,
    @SerializedName("age_group_ids") val ageGroupIds: List<Int>? = null,
    @SerializedName("coach_id") val coachId: Int? = null,
    @SerializedName("assistant_coach_id") val assistantCoachId: Int? = null,
    @SerializedName("capacity") val capacity: Int? = null,
    @SerializedName("status") val status: String = "active",
    @SerializedName("location") val location: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("pricing_type") val pricingType: String? = null,
    @SerializedName("billing_cycle") val billingCycle: String? = null,
    @SerializedName("monthly_fee") val monthlyFee: Long? = null,
    @SerializedName("seasonal_fee") val seasonalFee: Long? = null,
    @SerializedName("billing_months") val billingMonths: Int? = null,
    @SerializedName("session_fee") val sessionFee: Long? = null,
    @SerializedName("registration_fee") val registrationFee: Long? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null
)

data class UpdateClassRequest(
    @SerializedName("title") val title: String? = null,
    @SerializedName("season_id") val seasonId: Int? = null,
    @SerializedName("age_group_id") val ageGroupId: Int? = null,
    @SerializedName("age_group_ids") val ageGroupIds: List<Int>? = null,
    @SerializedName("coach_id") val coachId: Int? = null,
    @SerializedName("assistant_coach_id") val assistantCoachId: Int? = null,
    @SerializedName("capacity") val capacity: Int? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("pricing_type") val pricingType: String? = null,
    @SerializedName("billing_cycle") val billingCycle: String? = null,
    @SerializedName("monthly_fee") val monthlyFee: Long? = null,
    @SerializedName("seasonal_fee") val seasonalFee: Long? = null,
    @SerializedName("billing_months") val billingMonths: Int? = null,
    @SerializedName("session_fee") val sessionFee: Long? = null,
    @SerializedName("registration_fee") val registrationFee: Long? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null
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
    @SerializedName("seasonal_fee_override") val seasonalFeeOverride: Long? = null,
    @SerializedName("session_fee_override") val sessionFeeOverride: Long?,
    @SerializedName("registration_fee_override") val registrationFeeOverride: Long?,
    @SerializedName("notes") val notes: String?
)

data class UpdateEnrollmentRequest(
    @SerializedName("status") val status: String?,
    @SerializedName("enrolled_at") val enrolledAt: String?,
    @SerializedName("ended_at") val endedAt: String?,
    @SerializedName("monthly_fee_override") val monthlyFeeOverride: Long?,
    @SerializedName("seasonal_fee_override") val seasonalFeeOverride: Long? = null,
    @SerializedName("session_fee_override") val sessionFeeOverride: Long?,
    @SerializedName("registration_fee_override") val registrationFeeOverride: Long?,
    @SerializedName("notes") val notes: String?
)