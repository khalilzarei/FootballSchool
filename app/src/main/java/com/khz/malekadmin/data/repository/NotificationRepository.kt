package com.khz.malekadmin.data.repository

import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.core.network.safeCall
import com.khz.malekadmin.data.dto.request.SendNotificationRequest
import com.khz.malekadmin.data.remote.NotificationApi
import com.khz.malekadmin.domain.mapper.toDomain
import com.khz.malekadmin.domain.model.Notification

class NotificationRepository(private val api: NotificationApi) {
    suspend fun getNotifications(page: Int = 1, perPage: Int = 20, unreadOnly: Boolean? = null, type: String? = null): NetworkResult<List<Notification>> =
        safeCall({ api.getNotifications(page, perPage, unreadOnly, type) }) { it.items.map { d -> d.toDomain() } }

    suspend fun getUnreadCount(): NetworkResult<Int> =
        safeCall({ api.getUnreadCount() }) { it.unreadCount }

    suspend fun markAsRead(id: Int): NetworkResult<Unit> = safeCall({ api.markAsRead(id) }) { }
    suspend fun markAllAsRead(): NetworkResult<Unit> = safeCall({ api.markAllAsRead() }) { }
    suspend fun send(request: SendNotificationRequest): NetworkResult<Unit> = safeCall({ api.send(request) }) { }
}