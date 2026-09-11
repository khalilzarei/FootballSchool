package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class ChatMessageDto(
    @SerializedName("id") val id: Int,
    @SerializedName("room_id") val roomId: Int,
    @SerializedName("sender_id") val senderId: Int,
    @SerializedName("sender") val sender: UserDto?,
    @SerializedName("message_type") val messageType: String,
    @SerializedName("body") val body: String?,
    @SerializedName("media_id") val mediaId: Int?,
    @SerializedName("media") val media: MediaDto?,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("read_at") val readAt: String?,
    @SerializedName("created_at") val createdAt: String?
)