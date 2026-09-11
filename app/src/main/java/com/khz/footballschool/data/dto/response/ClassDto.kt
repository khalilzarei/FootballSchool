package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class ClassDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("age_group_id") val ageGroupId: Int?,
    @SerializedName("age_group") val ageGroup: AgeGroupDto?,
    @SerializedName("coach_id") val coachId: Int?,
    @SerializedName("coach") val coach: CoachDto?,
    @SerializedName("assistant_coach_id") val assistantCoachId: Int?,
    @SerializedName("capacity") val capacity: Int?,
    @SerializedName("status") val status: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("location") val location: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("pricing_type") val pricingType: String?,
    @SerializedName("monthly_fee") val monthlyFee: Long?,
    @SerializedName("session_fee") val sessionFee: Long?,
    @SerializedName("registration_fee") val registrationFee: Long?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("enrolled_count") val enrolledCount: Int,
    @SerializedName("schedules") val schedules: List<ClassScheduleDto> = emptyList()
)