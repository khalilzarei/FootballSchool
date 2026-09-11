package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class ClassScheduleDto(
    @SerializedName("id") val id: Int,
    @SerializedName("class_id") val classId: Int,
    @SerializedName("weekday") val weekday: Int,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("location") val location: String?,
    @SerializedName("status") val status: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)