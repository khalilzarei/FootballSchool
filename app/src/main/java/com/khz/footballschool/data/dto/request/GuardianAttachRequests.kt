package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

data class AttachGuardianToPlayerRequest(
    @SerializedName("guardian_id") val guardianId: Int,
    @SerializedName("relation") val relation: String,
    @SerializedName("is_primary") val isPrimary: Boolean = false,
    @SerializedName("can_view_reports") val canViewReports: Boolean = true,
    @SerializedName("can_pay") val canPay: Boolean = true
)

data class AttachPlayerToGuardianRequest(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("relation") val relation: String,
    @SerializedName("is_primary") val isPrimary: Boolean = false,
    @SerializedName("can_view_reports") val canViewReports: Boolean = true,
    @SerializedName("can_pay") val canPay: Boolean = true
)