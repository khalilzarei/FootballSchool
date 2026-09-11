package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

data class SetMediaAudiencesRequest(
    @SerializedName("audiences") val audiences: List<AudienceItemRequest>
)

data class AudienceItemRequest(
    @SerializedName("audience_type") val audienceType: String,
    @SerializedName("target_id") val targetId: Int?
)

data class CreateNewsRequest(
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("status") val status: String,
    @SerializedName("publish_at") val publishAt: String?,
    @SerializedName("audiences") val audiences: List<AudienceItemRequest>
)

data class UpdateNewsRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("publish_at") val publishAt: String?
)

data class SetNewsAudiencesRequest(
    @SerializedName("audiences") val audiences: List<AudienceItemRequest>
)

data class CreateMatchRequest(
    @SerializedName("title") val title: String,
    @SerializedName("match_type") val matchType: String,
    @SerializedName("class_id") val classId: Int?,
    @SerializedName("age_group_id") val ageGroupId: Int?,
    @SerializedName("opponent_team") val opponentTeam: String?,
    @SerializedName("match_date") val matchDate: String,
    @SerializedName("match_time") val matchTime: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String?
)

data class UpdateMatchRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("match_type") val matchType: String?,
    @SerializedName("class_id") val classId: Int?,
    @SerializedName("age_group_id") val ageGroupId: Int?,
    @SerializedName("opponent_team") val opponentTeam: String?,
    @SerializedName("match_date") val matchDate: String?,
    @SerializedName("match_time") val matchTime: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("notes") val notes: String?
)

data class SetMatchResultRequest(
    @SerializedName("home_score") val homeScore: Int,
    @SerializedName("away_score") val awayScore: Int
)

data class AddMatchPlayerRequest(
    @SerializedName("player_id") val playerId: Int,
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
    @SerializedName("notes") val notes: String?
)

data class UpdateMatchPlayerRequest(
    @SerializedName("invitation_status") val invitationStatus: String?,
    @SerializedName("attendance_status") val attendanceStatus: String?,
    @SerializedName("jersey_number") val jerseyNumber: Int?,
    @SerializedName("position") val position: String?,
    @SerializedName("goals") val goals: Int?,
    @SerializedName("assists") val assists: Int?,
    @SerializedName("yellow_cards") val yellowCards: Int?,
    @SerializedName("red_cards") val redCards: Int?,
    @SerializedName("minutes_played") val minutesPlayed: Int?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("notes") val notes: String?
)