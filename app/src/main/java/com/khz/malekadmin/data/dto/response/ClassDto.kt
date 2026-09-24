package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class ClassDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("season_id") val seasonId: Int? = null,
    @SerializedName("season") val season: SeasonDto? = null,
    @SerializedName("age_group_id") val ageGroupId: Int? = null,
    @SerializedName("age_group") val ageGroup: AgeGroupDto? = null,
    // چند گروه سنی برای هر کلاس
    @SerializedName("age_group_ids") val ageGroupIds: List<Int>? = null,
    @SerializedName("age_groups") val ageGroups: List<AgeGroupDto>? = null,
    @SerializedName("coach_id") val coachId: Int? = null,
    @SerializedName("coach") val coach: CoachDto? = null,
    @SerializedName("assistant_coach_id") val assistantCoachId: Int? = null,
    @SerializedName("assistant_coach") val assistantCoach: CoachDto? = null,
    @SerializedName("capacity") val capacity: Int? = null,
    @SerializedName("status") val status: String = "active",
    @SerializedName("is_active") val isActive: Boolean = false,
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
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("enrolled_count") val enrolledCount: Int = 0,
    // نال‌پذیر: Gson برای کلید غایب مقدار پیش‌فرض کاتلین را اعمال نمی‌کند
    @SerializedName("schedules") val schedules: List<ClassScheduleDto>? = null
)