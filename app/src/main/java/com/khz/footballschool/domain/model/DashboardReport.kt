package com.khz.footballschool.domain.model

data class DashboardReport(
    val usersStats: UsersStats,
    val activePlayers: Int,
    val activeClasses: Int,
    val sessionsToday: Int,
    val pendingPayments: Int,
    val totalDebt: Long
)

data class UsersStats(
    val totalUsers: Int,
    val totalAdmins: Int,
    val totalCoaches: Int,
    val totalGuardians: Int
)