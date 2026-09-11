package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class AttendanceDto(
    @SerializedName("id") val id: Int,
    @SerializedName("session_id") val sessionId: Int,
    @SerializedName("session") val session: SessionDto?,
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("player") val player: PlayerDto?,
    @SerializedName("status") val status: String,
    @SerializedName("is_billable") val isBillable: Boolean,
    @SerializedName("note") val note: String?,
    @SerializedName("recorded_at") val recordedAt: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)