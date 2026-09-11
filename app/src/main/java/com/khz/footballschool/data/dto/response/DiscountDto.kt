package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class DiscountDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("discount_type") val discountType: String,
    @SerializedName("value") val value: Double,
    @SerializedName("applies_to") val appliesTo: String,
    @SerializedName("auto_apply") val autoApply: Boolean,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("status") val status: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("description") val description: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)