package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class InvoiceItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("invoice_id") val invoiceId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("item_type") val itemType: String,
    @SerializedName("amount") val amount: Long,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("total") val total: Long,
    @SerializedName("description") val description: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)