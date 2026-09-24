package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class DashboardReportDto(
    @SerializedName("report") val report: ReportDataDto
)

data class ReportDataDto(
    @SerializedName("users") val users: UsersStatsDto,
    @SerializedName("active_players") val activePlayers: Int,
    @SerializedName("active_classes") val activeClasses: Int,
    @SerializedName("sessions_today") val sessionsToday: Int,
    @SerializedName("pending_payments") val pendingPayments: Int,
    @SerializedName("total_debt") val totalDebt: Long
)

data class UsersStatsDto(
    @SerializedName("total_users") val totalUsers: Int,
    @SerializedName("total_admins") val totalAdmins: String,
    @SerializedName("total_coaches") val totalCoaches: String,
    @SerializedName("total_guardians") val totalGuardians: String
)