package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("type") val type: String,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("data") val data: Map<String, Any>?,
    @SerializedName("read_at") val readAt: String?,
    @SerializedName("created_at") val createdAt: String?
)