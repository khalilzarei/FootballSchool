package com.khz.malekadmin.data.dto.request

import com.google.gson.annotations.SerializedName

data class CreateFootballClassRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("coach_id") val coachId: Int? = null,
    @SerializedName("age_group_id") val ageGroupId: Int? = null,
    @SerializedName("capacity") val capacity: Int? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("schedule") val schedule: String? = null,
    @SerializedName("status") val status: String = "active"
)