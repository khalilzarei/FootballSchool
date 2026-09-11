package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class EvaluationDto(
    @SerializedName("id") val id: Int,
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("player") val player: PlayerDto?,
    @SerializedName("session_id") val sessionId: Int?,
    @SerializedName("session") val session: SessionDto?,
    @SerializedName("coach_id") val coachId: Int,
    @SerializedName("coach") val coach: CoachDto?,
    @SerializedName("evaluation_type") val evaluationType: String,
    @SerializedName("technical_score") val technicalScore: Int?,
    @SerializedName("discipline_score") val disciplineScore: Int?,
    @SerializedName("physical_score") val physicalScore: Int?,
    @SerializedName("teamwork_score") val teamworkScore: Int?,
    @SerializedName("overall_score") val overallScore: Int?,
    @SerializedName("strengths") val strengths: String?,
    @SerializedName("weaknesses") val weaknesses: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)