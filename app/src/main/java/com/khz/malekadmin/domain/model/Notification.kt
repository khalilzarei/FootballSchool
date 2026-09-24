package com.khz.malekadmin.domain.model

data class Notification(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val type: String,
    val isRead: Boolean,
    val data: Map<String, Any>?,
    val readAt: String?,
    val createdAt: String?
)

data class UnreadCount(
    val unreadCount: Int
)