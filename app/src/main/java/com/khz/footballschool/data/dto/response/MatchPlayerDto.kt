package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class MatchPlayerDto(
    @SerializedName("id") val id: Int,
    @SerializedName("match_id") val matchId: Int,
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("player") val player: PlayerDto?,
    // fallback flat fields from server (for backward compat)
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("invitation_status") val invitationStatus: String?,
    @SerializedName("attendance_status") val attendanceStatus: String?,
    @SerializedName("jersey_number") val jerseyNumber: Int?,
    @SerializedName("position") val position: String?,
    @SerializedName("goals") val goals: Int,
    @SerializedName("assists") val assists: Int,
    @SerializedName("yellow_cards") val yellowCards: Int,
    @SerializedName("red_cards") val redCards: Int,
    @SerializedName("minutes_played") val minutesPlayed: Int?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)