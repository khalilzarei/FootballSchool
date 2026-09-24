package com.khz.malekadmin.domain.model

data class Session(
    val id: Int,
    val classId: Int,
    val classItem: FootballClass?,
    val sessionDate: String,
    val startTime: String?,
    val endTime: String?,
    val location: String?,
    val topic: String?,
    val status: String,
    val notes: String?,
    val createdAt: String?,
    val updatedAt: String?
)