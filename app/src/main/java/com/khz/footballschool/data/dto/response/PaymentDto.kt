package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class PaymentDto(
    @SerializedName("id") val id: Int,
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("player") val player: PlayerDto?,
    @SerializedName("invoice_id") val invoiceId: Int?,
    @SerializedName("invoice") val invoice: InvoiceDto?,
    @SerializedName("installment_id") val installmentId: Int?,
    @SerializedName("amount") val amount: Long,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("status") val status: String,
    @SerializedName("receipt_media_id") val receiptMediaId: Int?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("approved_at") val approvedAt: String?,
    @SerializedName("rejected_at") val rejectedAt: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)