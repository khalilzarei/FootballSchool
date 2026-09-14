package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class InvoiceDto(
    @SerializedName("id") val id: Int,
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("player") val player: PlayerDto?,
    @SerializedName("invoice_type") val invoiceType: String,
    @SerializedName("period_start_date") val periodStartDate: String?,
    @SerializedName("period_end_date") val periodEndDate: String?,
    @SerializedName("due_date") val dueDate: String?,
    @SerializedName("status") val status: String,
    @SerializedName("total_amount") val totalAmount: Long,
    @SerializedName("paid_amount") val paidAmount: Long,
    @SerializedName("remaining_amount") val remainingAmount: Long,
    @SerializedName("notes") val notes: String?,
    // نال‌پذیر: Gson برای کلید غایب مقدار پیش‌فرض کاتلین را اعمال نمی‌کند
    @SerializedName("items") val items: List<InvoiceItemDto>? = null,
    @SerializedName("discounts") val discounts: List<DiscountDto>? = null,
    @SerializedName("installments") val installments: List<InstallmentDto>? = null,
    @SerializedName("payments") val payments: List<PaymentDto>? = null,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)