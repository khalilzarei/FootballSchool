package com.khz.footballschool.domain.model

data class DebtsReport(
    val playerId: Int,
    val playerName: String,
    val totalDebt: Long,
    val overdueDebt: Long,
    val oldestInvoiceDate: String?
)