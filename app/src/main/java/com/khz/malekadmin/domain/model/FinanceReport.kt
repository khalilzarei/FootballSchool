package com.khz.malekadmin.domain.model

data class FinanceReport(
    val totalInvoiced: Long,
    val totalPaid: Long,
    val totalPending: Long,
    val totalDebt: Long,
    val monthlyRevenue: Long,
    val dailyRevenue: Long
)