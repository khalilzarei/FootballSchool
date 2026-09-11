package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class DebtsReportDto(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("player_name") val playerName: String,
    @SerializedName("total_debt") val totalDebt: Long,
    @SerializedName("overdue_debt") val overdueDebt: Long,
    @SerializedName("oldest_invoice_date") val oldestInvoiceDate: String?
)