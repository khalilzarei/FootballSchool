package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class SettingDto(
    @SerializedName("id") val id: Int,
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String,
    @SerializedName("value_type") val valueType: String,
    @SerializedName("description") val description: String?,
    @SerializedName("updated_at") val updatedAt: String?
)