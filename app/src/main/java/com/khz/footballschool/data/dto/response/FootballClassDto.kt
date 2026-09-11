package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class FootballClassDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("coach_id") val coachId: Int? = null,
    @SerializedName("coach_name") val coachName: String? = null,
    @SerializedName("age_group_id") val ageGroupId: Int? = null,
    @SerializedName("age_group_title") val ageGroupTitle: String? = null,
    @SerializedName("capacity") val capacity: Int? = null,
    @SerializedName("current_count") val currentCount: Int = 0,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("schedule") val schedule: String? = null,
    @SerializedName("status") val status: String = "active",
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)