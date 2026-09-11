package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class ChatRoomDto(
    @SerializedName("id") val id: Int,
    @SerializedName("room_type") val roomType: String,
    @SerializedName("target_user_id") val targetUserId: Int?,
    @SerializedName("player_id") val playerId: Int?,
    @SerializedName("class_id") val classId: Int?,
    @SerializedName("subject") val subject: String?,
    @SerializedName("last_message") val lastMessage: ChatMessageDto?,
    @SerializedName("unread_count") val unreadCount: Int,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)