package com.khz.footballschool.domain.model

data class ChatRoom(
    val id: Int,
    val isGroup: Boolean,
    val title: String,
    val image: String?,
    val users: List<ChatRoomUser>,
    val lastMessage: ChatMessage?,
    val unreadCount: Int,
    val status: String?,
    val isLocked: Boolean,
    val playerId: Int?,
    val classId: Int?,
    val subject: String?,
    val createdAt: String?,
    val updatedAt: String?
) {
    val isPrivate: Boolean
        get() = !isGroup
}

data class ChatRoomUser(
    val id: Int,
    val fullName: String,
    val avatar: String?,
    val role: String?,
    val memberRole: String?
)