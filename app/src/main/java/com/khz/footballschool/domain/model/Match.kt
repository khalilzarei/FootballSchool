package com.khz.footballschool.domain.model

data class Match(
    val id: Int,
    val title: String,
    val matchType: String,
    val classId: Int?,
    val classItem: FootballClass?,
    val ageGroupId: Int?,
    val ageGroup: AgeGroup?,
    val opponentTeam: String?,
    val matchDate: String,
    val matchTime: String?,
    val location: String?,
    val status: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val result: String? = null,
    val classTitle: String? = null,
    val ageGroupTitle: String? = null,
    val notes: String?,
    val players: List<MatchPlayer> = emptyList(),
    val createdAt: String?,
    val updatedAt: String?
) {
    val hasResult: Boolean
        get() = status == "finished" || status == "completed" || (homeScore != null && awayScore != null)
    val resultText: String?
        get() = if (homeScore != null && awayScore != null) "$homeScore - $awayScore" else result
}

data class MatchPlayer(
    val id: Int,
    val matchId: Int,
    val playerId: Int,
    val userId: Int,
    val player: Player?,
    val invitationStatus: String?,
    val attendanceStatus: String?,
    val jerseyNumber: Int?,
    val position: String?,
    val goals: Int,
    val assists: Int,
    val yellowCards: Int,
    val redCards: Int,
    val minutesPlayed: Int?,
    val rating: Double?,
    val notes: String?,
    val createdAt: String?,
    val updatedAt: String?
) {
    val isInvited: Boolean get() = invitationStatus != null
}
