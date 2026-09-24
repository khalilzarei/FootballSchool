package com.khz.malekadmin.domain.model

data class Attendance(
    val id: Int,
    val sessionId: Int,
    val session: Session?,
    val playerId: Int,
    val player: Player?,
    val status: String,
    val isBillable: Boolean,
    val note: String?,
    val recordedAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)