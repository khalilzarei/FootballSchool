package com.khz.footballschool.domain.model

data class Evaluation(
    val id: Int,
    val playerId: Int,
    val player: Player?,
    val sessionId: Int?,
    val session: Session?,
    val coachId: Int,
    val coach: Coach?,
    val evaluationType: String,
    val technicalScore: Int?,
    val disciplineScore: Int?,
    val physicalScore: Int?,
    val teamworkScore: Int?,
    val overallScore: Int?,
    val strengths: String?,
    val weaknesses: String?,
    val notes: String?,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?
)