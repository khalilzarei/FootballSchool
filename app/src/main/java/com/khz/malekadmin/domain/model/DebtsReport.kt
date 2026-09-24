package com.khz.malekadmin.domain.model

data class DebtsReport(
    val playerId: Int,
    val playerName: String,
    val totalDebt: Long,
    val total: Long = 0,
    val paid: Long = 0,
    val overdueDebt: Long = 0,
    val oldestInvoiceDate: String? = null,
    val classDebts: List<PlayerClassDebt> = emptyList(),
    val isDebtor: Boolean = true
)
