package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class InstallmentDto(
    @SerializedName("id") val id: Int,
    @SerializedName("invoice_id") val invoiceId: Int,
    @SerializedName("amount") val amount: Long,
    @SerializedName("paid_amount") val paidAmount: Long,
    @SerializedName("due_date") val dueDate: String?,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)