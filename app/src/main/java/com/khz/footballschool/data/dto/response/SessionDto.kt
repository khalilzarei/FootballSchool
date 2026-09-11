package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class SessionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("class_id") val classId: Int,
    @SerializedName("class") val classItem: ClassDto?,
    @SerializedName("session_date") val sessionDate: String,
    @SerializedName("start_time") val startTime: String?,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("topic") val topic: String?,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)