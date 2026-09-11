package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

data class CreateInvoiceRequest(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("invoice_type") val invoiceType: String,
    @SerializedName("period_start_date") val periodStartDate: String?,
    @SerializedName("period_end_date") val periodEndDate: String?,
    @SerializedName("due_date") val dueDate: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("items") val items: List<InvoiceItemRequest>,
    @SerializedName("discounts") val discounts: List<Int>
)

data class InvoiceItemRequest(
    @SerializedName("title") val title: String,
    @SerializedName("item_type") val itemType: String,
    @SerializedName("amount") val amount: Long,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("description") val description: String?
)

data class AddInvoiceItemRequest(
    @SerializedName("title") val title: String,
    @SerializedName("item_type") val itemType: String,
    @SerializedName("amount") val amount: Long,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("description") val description: String?
)

data class UpdateInvoiceItemRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("amount") val amount: Long?,
    @SerializedName("quantity") val quantity: Int?,
    @SerializedName("description") val description: String?
)

data class ApplyDiscountToInvoiceRequest(
    @SerializedName("discount_id") val discountId: Int,
    @SerializedName("description") val description: String?
)

data class AddInstallmentRequest(
    @SerializedName("amount") val amount: Long,
    @SerializedName("due_date") val dueDate: String?,
    @SerializedName("notes") val notes: String?
)

data class CreateDiscountRequest(
    @SerializedName("title") val title: String,
    @SerializedName("discount_type") val discountType: String,
    @SerializedName("value") val value: Double,
    @SerializedName("applies_to") val appliesTo: String,
    @SerializedName("auto_apply") val autoApply: Boolean,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("status") val status: String,
    @SerializedName("description") val description: String?
)

data class UpdateDiscountRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("discount_type") val discountType: String?,
    @SerializedName("value") val value: Double?,
    @SerializedName("applies_to") val appliesTo: String?,
    @SerializedName("auto_apply") val autoApply: Boolean?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("description") val description: String?
)

data class CreatePaymentRequest(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("invoice_id") val invoiceId: Int?,
    @SerializedName("installment_id") val installmentId: Int?,
    @SerializedName("amount") val amount: Long,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("status") val status: String,
    @SerializedName("receipt_media_id") val receiptMediaId: Int?,
    @SerializedName("notes") val notes: String?
)