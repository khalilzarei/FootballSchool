package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class ChatRoomDto(
    @SerializedName("id") val id: Int = 0,

    @SerializedName("is_group") val isGroup: Boolean = false,

    @SerializedName("title") val title: String? = null,

    @SerializedName("image") val image: String? = null,

    @SerializedName("users") val users: List<ChatRoomUserDto> = emptyList(),

    @SerializedName("last_message") val lastMessage: ChatMessageDto? = null,

    @SerializedName("unread_count") val unreadCount: Int = 0,

    @SerializedName("status") val status: String? = null,

    @SerializedName("is_locked") val isLocked: Any? = null,

    @SerializedName("player_id") val playerId: Int? = null,

    @SerializedName("class_id") val classId: Int? = null,

    @SerializedName("subject") val subject: String? = null,

    @SerializedName("created_at") val createdAt: String? = null,

    @SerializedName("updated_at") val updatedAt: String? = null
)

data class ChatRoomUserDto(
    @SerializedName("id") val id: Int = 0,

    @SerializedName("full_name") val fullName: String? = null,

    @SerializedName("mobile") val mobile: String? = null,

    @SerializedName("avatar") val avatar: String? = null,

    @SerializedName("role") val role: String? = null,

    @SerializedName("member_role") val memberRole: String? = null,

    /** آخرین پیام خوانده‌شده‌ی این عضو — برای تیک سبز ✓✓ */
    @SerializedName("last_read_message_id") val lastReadMessageId: Int? = null
)