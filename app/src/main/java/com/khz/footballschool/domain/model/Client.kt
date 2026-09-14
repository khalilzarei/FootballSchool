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
    val id: Int,
    val classId: Int,
    val classTitle: String,
    val sessionDate: String,
    val startTime: String,
    val endTime: String,
    val location: String?,
    val status: String,
    val topic: String?,
    val notes: String?
) {
    /** فقط ساعت و دقیقه: "17:00:00" → "17:00" */
    val startTimeShort: String get() = startTime.take(5)
    val endTimeShort: String get() = endTime.take(5)
}

data class MyFinance(
    val playerId: Int,
    val playerName: String,
    val totalInvoiced: Long,
    val totalPaid: Long,
    val balance: Long,
    val pendingPayments: Long,
    val invoices: List<Invoice>
)