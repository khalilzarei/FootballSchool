package com.khz.footballschool.domain.model

data class PlayerBalance(
    val playerId: Int,
    val totalInvoiced: Long,
    val totalPaid: Long,
    val balance: Long,
    val pendingPayments: Long
)