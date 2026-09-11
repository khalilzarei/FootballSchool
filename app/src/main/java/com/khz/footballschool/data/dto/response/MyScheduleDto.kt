package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class MyScheduleDto(
    @SerializedName("class_id") val classId: Int,
    @SerializedName("class_title") val classTitle: String,
    @SerializedName("weekday") val weekday: Int,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("location") val location: String?,
    @SerializedName("coach_name") val coachName: String?
)