package com.khz.footballschool.domain.model

data class Enrollment(
    val id: Int,
    val classId: Int,
    val classItem: FootballClass,
    val playerId: Int,
    val player: Player,
    val status: String,
    val isActive: Boolean,
    val enrolledAt: String?,
    val endedAt: String?,
    val monthlyFeeOverride: Long?,
    val sessionFeeOverride: Long?,
    val registrationFeeOverride: Long?,
    val notes: String?,
    val createdAt: String?,
    val updatedAt: String?
)