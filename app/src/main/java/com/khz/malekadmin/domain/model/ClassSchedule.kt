package com.khz.malekadmin.domain.model

data class ClassSchedule(
    val id: Int,
    val classId: Int,
    val weekday: String,
    val startTime: String,
    val endTime: String,
    val location: String?,
    val status: String,
    val isActive: Boolean,
    val createdAt: String?,
    val updatedAt: String?
) {
    val weekdayLabel: String
        get() = when (weekday.lowercase()) {
            "saturday", "شنبه" -> "شنبه"
            "sunday", "یکشنبه" -> "یکشنبه"
            "monday", "دوشنبه" -> "دوشنبه"
            "tuesday", "سه‌شنبه" -> "سه‌شنبه"
            "wednesday", "چهارشنبه" -> "چهارشنبه"
            "thursday", "پنج‌شنبه" -> "پنج‌شنبه"
            "friday", "جمعه" -> "جمعه"
            else -> weekday
        }

    val timeRange: String
        get() = "$startTime - $endTime"
}