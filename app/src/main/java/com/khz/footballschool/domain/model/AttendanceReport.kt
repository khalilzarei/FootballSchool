package com.khz.footballschool.domain.model

data class AttendanceReport(
    val playerId: Int,
    val playerName: String,
    val totalSessions: Int,
    val presentCount: Int,
    val absentCount: Int,
    val excusedCount: Int,
    val attendanceRate: Double
)