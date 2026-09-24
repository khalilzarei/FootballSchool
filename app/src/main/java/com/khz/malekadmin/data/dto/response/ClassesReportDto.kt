package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class ClassesReportDto(
    @SerializedName("class_id") val classId: Int,
    @SerializedName("class_title") val classTitle: String,
    @SerializedName("coach_name") val coachName: String?,
    @SerializedName("enrolled_count") val enrolledCount: Int,
    @SerializedName("capacity") val capacity: Int?,
    @SerializedName("occupancy_rate") val occupancyRate: Double,
    @SerializedName("total_revenue") val totalRevenue: Long
)