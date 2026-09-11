package com.khz.footballschool.domain.model

data class ClassesReport(
    val classId: Int,
    val classTitle: String,
    val coachName: String?,
    val enrolledCount: Int,
    val capacity: Int?,
    val occupancyRate: Double,
    val totalRevenue: Long
)