package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class MatchDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("match_type") val matchType: String,
    @SerializedName("class_id") val classId: Int?,
    @SerializedName("class") val classItem: ClassDto?,
    @SerializedName("age_group_id") val ageGroupId: Int?,
    @SerializedName("age_group") val ageGroup: AgeGroupDto?,
    @SerializedName("opponent_team") val opponentTeam: String?,
    @SerializedName("match_date") val matchDate: String,
    @SerializedName("match_time") val matchTime: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("status") val status: String,
    @SerializedName("home_score") val homeScore: Int?,
    @SerializedName("away_score") val awayScore: Int?,
    @SerializedName("result") val result: String? = null,
    @SerializedName("class_title") val classTitle: String? = null,
    @SerializedName("age_group_title") val ageGroupTitle: String? = null,
    @SerializedName("notes") val notes: String?,
    @SerializedName("players") val players: List<MatchPlayerDto>? = null,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
