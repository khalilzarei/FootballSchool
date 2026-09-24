package com.khz.malekadmin.domain.model

data class ChatMessage(
    val id: Int,
    val roomId: Int,
    val senderId: Int,
    val senderName: String?,
    val sender: User?,
    val senderAvatar: String? = null,
    val messageType: String,
    val body: String?,
    val mediaId: Int?,
    val media: Media?,
    val isRead: Boolean,
    val readAt: String?,
    val createdAt: String?
)