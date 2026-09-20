package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class ChatMessageDto(
    @SerializedName("id") val id: Int = 0,

    @SerializedName("room_id") val roomId: Int? = null,

    @SerializedName("chat_room_id") val chatRoomId: Int? = null,

    @SerializedName("sender_id") val senderId: Int = 0,

    @SerializedName("sender") val sender: UserDto? = null,

    @SerializedName("sender_name") val senderName: String? = null,

    @SerializedName("sender_role") val senderRole: String? = null,

    @SerializedName("message_type") val messageType: String = "text",

    @SerializedName("body") val body: String? = null,

    @SerializedName("media_id") val mediaId: Int? = null,

    @SerializedName("media") val media: MediaDto? = null,

    @SerializedName("is_read") val isRead: Boolean = false,

    @SerializedName("read_at") val readAt: String? = null,

    @SerializedName("sent_at") val sentAt: String? = null,

    @SerializedName("created_at") val createdAt: String? = null
) {
    val effectiveRoomId: Int
        get() = roomId
                ?: chatRoomId
                ?: 0

    val effectiveSenderName: String?
        get() = sender?.fullName
                ?: senderName

    val effectiveSenderRole: String?
        get() = sender?.role
                ?: senderRole

    val effectiveCreatedAt: String?
        get() = createdAt
                ?: sentAt
}