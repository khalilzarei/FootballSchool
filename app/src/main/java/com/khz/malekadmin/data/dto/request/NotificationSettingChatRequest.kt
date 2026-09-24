package com.khz.malekadmin.data.dto.request

import com.google.gson.annotations.SerializedName

data class SendNotificationRequest(
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("type") val type: String,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("role") val role: String?,
    @SerializedName("data") val data: Map<String, Any>?
)

data class SettingItemRequest(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String,
    @SerializedName("value_type") val valueType: String,
    @SerializedName("description") val description: String?
)

data class UpdateSettingsRequest(
    @SerializedName("settings") val settings: List<SettingItemRequest>
)



data class MarkChatReadRequest(
    @SerializedName("last_read_message_id") val lastReadMessageId: Int
)