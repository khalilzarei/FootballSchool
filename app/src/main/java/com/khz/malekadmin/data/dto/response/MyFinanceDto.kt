package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class MyFinanceDto(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("player_name") val playerName: String,
    @SerializedName("total_invoiced") val totalInvoiced: Long,
    @SerializedName("total_paid") val totalPaid: Long,
    @SerializedName("balance") val balance: Long,
    @SerializedName("pending_payments") val pendingPayments: Long,
    @SerializedName("invoices") val invoices: List<InvoiceDto>? = null
)