package com.khz.footballschool.data.repository

import android.util.Log
import com.khz.footballschool.core.network.ApiErrorHandler
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.CreateChatRoomRequest
import com.khz.footballschool.data.dto.request.SendChatMessageRequest
import com.khz.footballschool.data.dto.response.ChatMessageDto
import com.khz.footballschool.data.dto.response.ChatRoomDto
import com.khz.footballschool.data.remote.ChatApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.ChatMessage
import com.khz.footballschool.domain.model.ChatRoom

class ChatRepository(private val api: ChatApi) {

    companion object {
        private const val TAG = "ChatRepository"

        // انواع اتاق چت
        const val ROOM_TYPE_PRIVATE = "private"
        const val ROOM_TYPE_PLAYER = "player"
        const val ROOM_TYPE_CLASS = "class"
        const val ROOM_TYPE_GROUP = "group"
    }

    // ═════════════════════════════════════════════
    // Rooms
    // ═════════════════════════════════════════════
    suspend fun getRooms(): NetworkResult<List<ChatRoom>> = try {
        val r = api.getRooms()
        if (r.success && r.data != null) {
            NetworkResult.Success(r.data.map { it.toDomain() })
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در دریافت اتاق‌ها"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "getRooms error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun getRoom(roomId: Int): NetworkResult<ChatRoom> = try {
        val r = api.getRoom(roomId)
        if (r.success && r.data != null) {
            NetworkResult.Success(r.data.toDomain())
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در دریافت اتاق"
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Create Room - متدهای تخصصی
    // ═════════════════════════════════════════════

    /**
     * ایجاد اتاق چت خصوصی بین دو کاربر
     */
    suspend fun createPrivateRoom(
        targetUserId: Int,
        subject: String? = null
    ): NetworkResult<ChatRoom> = try {
        val request = CreateChatRoomRequest(
            participantIds = listOf(targetUserId),
            roomType = ROOM_TYPE_PRIVATE,
            targetUserId = targetUserId,
            subject = subject
        )

        Log.d(
            TAG,
            "createPrivateRoom: targetUserId=$targetUserId"
        )
        val r = api.createRoom(request)

        if (r.success && r.data != null) {
            NetworkResult.Success(r.data.toDomain())
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در ایجاد اتاق خصوصی"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "createPrivateRoom error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    /**
     * ایجاد اتاق چت درباره یک بازیکن خاص
     * (مثلاً بین مربی و سرپرست بازیکن)
     */
    suspend fun createPlayerRoom(
        playerId: Int,
        participantIds: List<Int>,
        subject: String? = null
    ): NetworkResult<ChatRoom> = try {
        val request = CreateChatRoomRequest(
            participantIds = participantIds,
            roomType = ROOM_TYPE_PLAYER,
            playerId = playerId,
            subject = subject
        )

        Log.d(
            TAG,
            "createPlayerRoom: playerId=$playerId, participants=$participantIds"
        )
        val r = api.createRoom(request)

        if (r.success && r.data != null) {
            NetworkResult.Success(r.data.toDomain())
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در ایجاد اتاق بازیکن"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "createPlayerRoom error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    /**
     * ایجاد اتاق چت گروهی برای یک کلاس
     */
    suspend fun createClassRoom(
        classId: Int,
        participantIds: List<Int>,
        subject: String? = null
    ): NetworkResult<ChatRoom> = try {
        val request = CreateChatRoomRequest(
            participantIds = participantIds,
            roomType = ROOM_TYPE_CLASS,
            classId = classId,
            subject = subject
        )

        Log.d(
            TAG,
            "createClassRoom: classId=$classId, participants=$participantIds"
        )
        val r = api.createRoom(request)

        if (r.success && r.data != null) {
            NetworkResult.Success(r.data.toDomain())
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در ایجاد اتاق کلاس"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "createClassRoom error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    /**
     * ایجاد اتاق چت عمومی (با تمام پارامترها)
     */
    suspend fun createRoom(
        participantIds: List<Int>,
        roomType: String,
        targetUserId: Int? = null,
        playerId: Int? = null,
        classId: Int? = null,
        subject: String? = null
    ): NetworkResult<ChatRoom> = try {
        val request = CreateChatRoomRequest(
            participantIds = participantIds,
            roomType = roomType,
            targetUserId = targetUserId,
            playerId = playerId,
            classId = classId,
            subject = subject
        )

        Log.d(
            TAG,
            "createRoom: type=$roomType, participants=$participantIds"
        )
        val r = api.createRoom(request)

        if (r.success && r.data != null) {
            NetworkResult.Success(r.data.toDomain())
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در ایجاد اتاق"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "createRoom error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    /**
     * دریافت یا ایجاد اتاق خصوصی با کاربر مشخص‌شده
     * ابتدا تلاش می‌کند اتاق موجود را پیدا کند، در غیر این صورت ایجاد می‌کند
     */
    suspend fun getOrCreatePrivateRoomWithUser(userId: Int): NetworkResult<ChatRoom> = try {
        Log.d(
            TAG,
            "getOrCreatePrivateRoomWithUser: userId=$userId"
        )

        // تلاش برای پیدا کردن اتاق موجود
        val findResult = api.getRoomWithUser(userId)
        if (findResult.success && findResult.data != null) {
            Log.d(
                TAG,
                "Found existing room: ${findResult.data.id}"
            )
            NetworkResult.Success(findResult.data.toDomain())
        } else {
            // اگر پیدا نشد، ایجاد کن
            Log.d(
                TAG,
                "Room not found, creating new private room"
            )
            createPrivateRoom(userId)
        }
    } catch (e: Exception) {
        // اگر endpoint /with-user وجود ندارد، مستقیم ایجاد کن
        Log.w(
            TAG,
            "getRoomWithUser failed, trying to create directly: ${e.message}"
        )
        createPrivateRoom(userId)
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
            "getMessages: roomId=$roomId, limit=$limit"
        )

        val r = api.getMessages(
            roomId,
            limit,
            before
        )
        if (r.success && r.data != null) {
            NetworkResult.Success(r.data.map { it.toDomain() })
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در دریافت پیام‌ها"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "getMessages error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun sendMessage(
        roomId: Int,
        body: String
    ): NetworkResult<ChatMessage> = try {
        Log.d(
            TAG,
            "sendMessage: roomId=$roomId, body=$body"
        )

        val request = SendChatMessageRequest(
            body = body,
            messageType = "",
            mediaId = 1

        )
        val r = api.sendMessage(
            roomId,
            request
        )

        if (r.success && r.data != null) {
            NetworkResult.Success(r.data.toDomain())
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در ارسال پیام"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "sendMessage error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun deleteMessage(
        roomId: Int,
        messageId: Int
    ): NetworkResult<Unit> = try {
        Log.d(
            TAG,
            "deleteMessage: roomId=$roomId, messageId=$messageId"
        )

        val r = api.deleteMessage(
            roomId,
            messageId
        )
        if (r.success) {
            NetworkResult.Success(Unit)
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در حذف پیام"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "deleteMessage error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }
}