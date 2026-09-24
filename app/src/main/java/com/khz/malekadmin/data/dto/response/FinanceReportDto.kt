package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class FinanceReportDto(
    @SerializedName("total_invoiced") val totalInvoiced: Long,
    @SerializedName("total_paid") val totalPaid: Long,
    @SerializedName("total_pending") val totalPending: Long,
    @SerializedName("total_debt") val totalDebt: Long,
    @SerializedName("monthly_revenue") val monthlyRevenue: Long,
    @SerializedName("daily_revenue") val dailyRevenue: Long
)