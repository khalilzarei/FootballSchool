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

class ChatRepository(private val api: ChatApi) {

    companion object {
        private const val TAG = "ChatRepository"

        // انواع اتاق چت مطابق سرور (ChatController::createRoom)
        const val ROOM_TYPE_PLAYER_ADMIN = "player_admin"
        const val ROOM_TYPE_COACH_ADMIN = "coach_admin"
        const val ROOM_TYPE_PLAYER_COACH = "player_coach"

        /**
         * نوع اتاق مجاز را بر اساس نقش دو کاربر تعیین می‌کند؛
         * سرور فقط ترکیب‌های admin↔player، admin↔coach و player↔coach را می‌پذیرد
         */
        fun roomTypeForRoles(
            currentRole: String?,
            targetRole: String?
        ): String? {
            if (currentRole == null || targetRole == null) return null
            return when (setOf(
                currentRole,
                targetRole
            )) {
                setOf(
                    "admin",
                    "player"
                ) -> ROOM_TYPE_PLAYER_ADMIN

                setOf(
                    "admin",
                    "coach"
                ) -> ROOM_TYPE_COACH_ADMIN

                setOf(
                    "player",
                    "coach"
                ) -> ROOM_TYPE_PLAYER_COACH

                else -> null
            }
        }
    }

    // ═════════════════════════════════════════════
    // Rooms
    // ═════════════════════════════════════════════
    suspend fun getRooms(): NetworkResult<List<ChatRoom>> = try {
        val r = api.getRooms()
        if (r.success && r.data != null) {
            NetworkResult.Success(
                r.data.rooms.orEmpty()
                    .map { it.toDomain() })
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
        if (r.success && r.data?.room != null) {
            NetworkResult.Success(r.data.room.toDomain())
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
    // Create Room — مطابق قرارداد سرور
    // (سرور endpoint جستجوی اتاق با userId ندارد؛ اما خود createRoom
    //  با unique_key اتاق موجود را برمی‌گرداند، پس create همیشه
    //  «get or create» است و idempotent می‌باشد)
    // ═════════════════════════════════════════════

    /**
     * ایجاد (یا دریافت) اتاق چت بین کاربر جاری و کاربر مقابل.
     * نوع اتاق باید یکی از سه نوع مجاز سرور باشد (roomTypeForRoles).
     */
    suspend fun createRoom(
        roomType: String,
        targetUserId: Int,
        playerId: Int? = null,
        classId: Int? = null,
        subject: String? = null
    ): NetworkResult<ChatRoom> = try {
        Log.d(
            TAG,
            "createRoom: type=$roomType, targetUserId=$targetUserId, playerId=$playerId, classId=$classId"
        )

        val request = CreateChatRoomRequest(
            roomType = roomType,
            targetUserId = targetUserId,
            playerId = playerId,
            classId = classId,
            subject = subject,
            participantIds = arrayListOf(),
        )
        val r = api.createRoom(request)

        if (r.success && r.data?.room != null) {
            NetworkResult.Success(r.data.room.toDomain())
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
     * ایجاد اتاق خصوصی با کاربر مشخص‌شده؛
     * نوع اتاق را بر اساس نقش دو طرف تعیین کنید (roomTypeForRoles)
     */
    suspend fun createPrivateRoom(
        targetUserId: Int,
        roomType: String,
        subject: String? = null
    ): NetworkResult<ChatRoom> = createRoom(
        roomType = roomType,
        targetUserId = targetUserId,
        subject = subject
    )

    /**
     * اتاق گفتگو درباره یک بازیکن خاص (بین سرپرست و مربی)
     */
    suspend fun createPlayerRoom(
        targetUserId: Int,
        playerId: Int,
        subject: String? = null
    ): NetworkResult<ChatRoom> = createRoom(
        roomType = ROOM_TYPE_PLAYER_COACH,
        targetUserId = targetUserId,
        playerId = playerId,
        subject = subject
    )

    /**
     * دریافت یا ایجاد اتاق خصوصی با کاربر مشخص‌شده.
     * (سرور اتاق موجود را با unique_key برمی‌گرداند؛ فراخوانی create کافی است)
     */
    suspend fun getOrCreatePrivateRoomWithUser(
        userId: Int,
        roomType: String
    ): NetworkResult<ChatRoom> = createPrivateRoom(
        targetUserId = userId,
        roomType = roomType
    )

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
            NetworkResult.Success(
                r.data.messages.orEmpty()
                    .map { it.toDomain() })
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
            messageType = "text",
            mediaId = null
        )
        val r = api.sendMessage(
            roomId,
            request
        )

        if (r.success && r.data?.message != null) {
            NetworkResult.Success(r.data.message.toDomain())
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

    /**
     * علامت‌گذاری پیام‌های اتاق به‌عنوان خوانده‌شده (تا آخرین پیام مشخص‌شده)
     * شمارنده پیام‌های خوانده‌نشده کاربر در این اتاق صفر می‌شود
     */
    suspend fun markAsRead(
        roomId: Int,
        lastReadMessageId: Int
    ): NetworkResult<Unit> = try {
        Log.d(
            TAG,
            "markAsRead: roomId=$roomId, lastReadMessageId=$lastReadMessageId"
        )

        val r = api.markAsRead(
            roomId,
            MarkChatReadRequest(lastReadMessageId)
        )
        if (r.success) {
            NetworkResult.Success(Unit)
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در ثبت خوانده‌شدن پیام‌ها"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "markAsRead error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }
}