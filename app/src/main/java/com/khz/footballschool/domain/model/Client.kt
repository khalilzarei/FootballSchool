package com.khz.footballschool.domain.model

data class MyChild(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val birthDate: String?,
    val age: Int?,
    val currentClass: FootballClass?,
    val balance: PlayerBalance?
)

data class MyScheduleItem(
    val classId: Int,
    val classTitle: String,
    val weekday: Int,
    val startTime: String,
    val endTime: String,
    val location: String?,
    val coachName: String?
)

data class MyFinance(
    val playerId: Int,
    val playerName: String,
    val totalInvoiced: Long,
    val totalPaid: Long,
    val balance: Long,
    val pendingPayments: Long,
    val invoices: List<Invoice>
)