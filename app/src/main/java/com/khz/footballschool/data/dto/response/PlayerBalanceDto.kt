package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class PlayerBalanceDto(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("total_invoiced") val totalInvoiced: Long,
    @SerializedName("total_paid") val totalPaid: Long,
    @SerializedName("balance") val balance: Long,
    @SerializedName("pending_payments") val pendingPayments: Long
)