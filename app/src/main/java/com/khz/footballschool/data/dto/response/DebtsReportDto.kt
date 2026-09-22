package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class DebtsReportDto(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("player_name") val playerName: String,
    @SerializedName("total_debt") val totalDebt: Long,
    @SerializedName("total") val total: Long? = null,
    @SerializedName("paid") val paid: Long? = null,
    @SerializedName("debt") val debt: Long? = null,
    @SerializedName("overdue_debt") val overdueDebt: Long? = null,
    @SerializedName("oldest_invoice_date") val oldestInvoiceDate: String? = null,
    @SerializedName("oldest_due_date") val oldestDueDate: String? = null,
    @SerializedName("class_debts") val classDebts: List<ClassDebtDto>? = null,
    @SerializedName("is_debtor") val isDebtor: Boolean? = null
)
