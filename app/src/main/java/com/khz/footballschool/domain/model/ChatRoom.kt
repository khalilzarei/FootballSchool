package com.khz.footballschool.domain.model

data class ChatRoom(
    val id: Int,
    val roomType: String,
    val targetUserId: Int?,
    val playerId: Int?,
    val classId: Int?,
    val subject: String?,
    val lastMessage: ChatMessage?,
    val unreadCount: Int,
    val createdAt: String?,
    val updatedAt: String?
)

