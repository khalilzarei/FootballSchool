package com.khz.footballschool.data.repository

import android.util.Log
import com.khz.footballschool.core.network.ApiErrorHandler
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.CreateChatRoomRequest
import com.khz.footballschool.data.dto.request.MarkChatReadRequest
import com.khz.footballschool.data.dto.request.SendChatMessageRequest
import com.khz.footballschool.data.remote.ChatApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.ChatMessage
import com.khz.footballschool.domain.model.ChatRoom

class ChatRepository(
    private val api: ChatApi
) {

    companion object {
        private const val TAG = "ChatRepository"
    }

    // ═════════════════════════════════════════════
    // Rooms
    // ═════════════════════════════════════════════

    suspend fun getRooms(): NetworkResult<List<ChatRoom>> = try {
        Log.d(
            TAG,
            "getRooms"
        )

        val response = api.getRooms()

        if (response.success && response.data != null) {
            NetworkResult.Success(
                response.data.rooms.orEmpty()
                    .map { it.toDomain() })
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در دریافت اتاق‌ها"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "getRooms error: ${e.message}",
            e
        )

        NetworkResult.Error(
            ApiErrorHandler.extractMessage(e)
        )
    }

    suspend fun getRoom(
        roomId: Int
    ): NetworkResult<ChatRoom> = try {
        Log.d(
            TAG,
            "getRoom: roomId=$roomId"
        )

        val response = api.getRoom(roomId)

        if (response.success && response.data?.room != null) {
            NetworkResult.Success(
                response.data.room.toDomain()
            )
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در دریافت اتاق"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "getRoom error: ${e.message}",
            e
        )

        NetworkResult.Error(
            ApiErrorHandler.extractMessage(e)
        )
    }

    /**
     * ایجاد یا دریافت اتاق خصوصی.
     *
     * کاربر جاری توسط Token در سرور شناسایی می‌شود.
     */
    suspend fun createPrivateRoom(
        targetUserId: Int
    ): NetworkResult<ChatRoom> = try {
        Log.d(
            TAG,
            "createPrivateRoom: targetUserId=$targetUserId"
        )

        val request = CreateChatRoomRequest(
            targetUserId = targetUserId
        )

        val response = api.createRoom(request)

        if (response.success && response.data?.room != null) {
            NetworkResult.Success(
                response.data.room.toDomain()
            )
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در ایجاد اتاق خصوصی"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "createPrivateRoom error: ${e.message}",
            e
        )

        NetworkResult.Error(
            ApiErrorHandler.extractMessage(e)
        )
    }

    /**
     * ایجاد اتاق با تمام پارامترهای موردنیاز سرور.
     */
    suspend fun createRoom(
        request: CreateChatRoomRequest
    ): NetworkResult<ChatRoom> = try {
        Log.d(
            TAG,
            "createRoom: targetUserId=${request.targetUserId}"
        )

        val response = api.createRoom(request)

        if (response.success && response.data?.room != null) {
            NetworkResult.Success(
                response.data.room.toDomain()
            )
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در ایجاد اتاق"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "createRoom error: ${e.message}",
            e
        )

        NetworkResult.Error(
            ApiErrorHandler.extractMessage(e)
        )
    }

    // ═════════════════════════════════════════════
    // Messages
    // ═════════════════════════════════════════════

    suspend fun getMessages(
        roomId: Int,
        limit: Int = 50,
        before: Int? = null
    ): NetworkResult<List<ChatMessage>> = try {
        Log.d(
            TAG,
            "getMessages: roomId=$roomId, limit=$limit, before=$before"
        )

        val response = api.getMessages(
            roomId = roomId,
            limit = limit,
            before = before
        )

        if (response.success && response.data != null) {
            NetworkResult.Success(
                response.data.messages.orEmpty()
                    .map { it.toDomain() })
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در دریافت پیام‌ها"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "getMessages error: ${e.message}",
            e
        )

        NetworkResult.Error(
            ApiErrorHandler.extractMessage(e)
        )
    }

    suspend fun sendMessage(
        roomId: Int,
        body: String
    ): NetworkResult<ChatMessage> = try {
        val message = body.trim()

        if (message.isEmpty()) {
            return NetworkResult.Error(
                "متن پیام نمی‌تواند خالی باشد"
            )
        }

        Log.d(
            TAG,
            "sendMessage: roomId=$roomId"
        )

        val request = SendChatMessageRequest(
            body = message,
            messageType = "text",
            mediaId = null
        )

        val response = api.sendMessage(
            roomId = roomId,
            request = request
        )

        if (response.success && response.data?.message != null) {
            NetworkResult.Success(
                response.data.message.toDomain()
            )
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در ارسال پیام"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "sendMessage error: ${e.message}",
            e
        )

        NetworkResult.Error(
            ApiErrorHandler.extractMessage(e)
        )
    }

    suspend fun deleteMessage(
        roomId: Int,
        messageId: Int
    ): NetworkResult<Unit> = try {
        Log.d(
            TAG,
            "deleteMessage: roomId=$roomId, messageId=$messageId"
        )

        val response = api.deleteMessage(
            roomId = roomId,
            messageId = messageId
        )

        if (response.success) {
            NetworkResult.Success(Unit)
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در حذف پیام"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "deleteMessage error: ${e.message}",
            e
        )

        NetworkResult.Error(
            ApiErrorHandler.extractMessage(e)
        )
    }

    suspend fun markAsRead(
        roomId: Int,
        lastReadMessageId: Int
    ): NetworkResult<Unit> = try {
        Log.d(
            TAG,
            "markAsRead: roomId=$roomId, lastReadMessageId=$lastReadMessageId"
        )

        val request = MarkChatReadRequest(
            lastReadMessageId = lastReadMessageId
        )

        val response = api.markAsRead(
            roomId = roomId,
            request = request
        )

        if (response.success) {
            NetworkResult.Success(Unit)
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در ثبت خوانده‌شدن پیام‌ها"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "markAsRead error: ${e.message}",
            e
        )

        NetworkResult.Error(
            ApiErrorHandler.extractMessage(e)
        )
    }
}
