package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class AttendanceReportDto(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("player_name") val playerName: String,
    @SerializedName("total_sessions") val totalSessions: Int,
    @SerializedName("present_count") val presentCount: Int,
    @SerializedName("absent_count") val absentCount: Int,
    @SerializedName("excused_count") val excusedCount: Int,
    @SerializedName("attendance_rate") val attendanceRate: Double
)