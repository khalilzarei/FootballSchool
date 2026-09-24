package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class InvoiceDto(
    @SerializedName("id") val id: Int,
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("player") val player: PlayerDto?,
    @SerializedName("invoice_number") val invoiceNumber: String? = null,
    @SerializedName("invoice_type") val invoiceType: String,
    @SerializedName("period_start_date") val periodStartDate: String?,
    @SerializedName("period_end_date") val periodEndDate: String?,
    @SerializedName("due_date") val dueDate: String?,
    @SerializedName("status") val status: String,
    // بک‌اند جدید: subtotal, discount_total, paid_total, remaining_total
    @SerializedName("subtotal") val subtotal: Long? = null,
    @SerializedName("discount_total") val discountTotal: Long? = null,
    @SerializedName("paid_total") val paidTotal: Long? = null,
    @SerializedName("remaining_total") val remainingTotal: Long? = null,
    // سازگاری قدیمی
    @SerializedName("total_amount") val totalAmount: Long? = null,
    @SerializedName("paid_amount") val paidAmount: Long? = null,
    @SerializedName("remaining_amount") val remainingAmount: Long? = null,
    @SerializedName("is_debtor") val isDebtor: Boolean? = null,
    @SerializedName("notes") val notes: String?,
    @SerializedName("items") val items: List<InvoiceItemDto>? = null,
    @SerializedName("discounts") val discounts: List<DiscountDto>? = null,
    @SerializedName("installments") val installments: List<InstallmentDto>? = null,
    @SerializedName("payments") val payments: List<PaymentDto>? = null,
    @SerializedName("class_debts") val classDebts: List<InvoiceClassDebtDto>? = null,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class InvoiceClassDebtDto(
    @SerializedName("class_id") val classId: Int?,
    @SerializedName("class_title") val classTitle: String?,
    @SerializedName("age_group_title") val ageGroupTitle: String?,
    @SerializedName("total") val total: Long? = null,
    @SerializedName("paid") val paid: Long? = null,
    @SerializedName("remaining") val remaining: Long? = null,
    @SerializedName("items_count") val itemsCount: Int? = null
)
