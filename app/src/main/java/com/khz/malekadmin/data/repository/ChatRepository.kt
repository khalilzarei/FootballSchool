package com.khz.malekadmin.data.repository

import android.util.Log
import com.khz.malekadmin.core.network.ApiErrorHandler
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.dto.request.AddRoomMembersRequest
import com.khz.malekadmin.data.dto.request.CreateChatRoomRequest
import com.khz.malekadmin.data.dto.request.CreateGroupChatRoomRequest
import com.khz.malekadmin.data.dto.request.MarkChatReadRequest
import com.khz.malekadmin.data.dto.request.SendChatMessageRequest
import com.khz.malekadmin.data.dto.request.UpdateChatRoomRequest
import com.khz.malekadmin.data.remote.ChatApi
import com.khz.malekadmin.domain.mapper.toDomain
import com.khz.malekadmin.domain.model.ChatContact
import com.khz.malekadmin.domain.model.ChatMessage
import com.khz.malekadmin.domain.model.ChatRoom

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
     * ساخت روم گروهی (فقط ادمین).
     * اگر روم مشابهی (title + اعضا) وجود داشته باشد،
     * سرور همان را برمی‌گرداند و اعضا را تکمیل می‌کند.
     */
    suspend fun createGroupRoom(
        title: String,
        userIds: List<Int>
    ): NetworkResult<ChatRoom> = try {
        Log.d(
            TAG,
            "createGroupRoom: title=$title count=${userIds.size}"
        )

        val request = CreateGroupChatRoomRequest(
            title = title,
            userIds = userIds
        )

        val response = api.createGroupRoom(request)

        if (response.success && response.data?.room != null) {
            NetworkResult.Success(response.data.room.toDomain())
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در ساخت گروه گفتگو"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "createGroupRoom error: ${e.message}",
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

    // ═════════════════════════════════════════════════════════
    // مدیریت چت — فقط ادمین
    // ═════════════════════════════════════════════════════════

    suspend fun getContacts(): NetworkResult<List<ChatContact>> = try {
        Log.d(
            TAG,
            "getContacts"
        )

        val response = api.getContacts()

        if (response.success && response.data != null) {
            NetworkResult.Success(
                response.data.contacts.map { it.toDomain() })
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در دریافت مخاطبین"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "getContacts error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun updateRoom(
        roomId: Int,
        title: String?,
        image: String?,
        subject: String? = null
    ): NetworkResult<ChatRoom> = try {
        Log.d(
            TAG,
            "updateRoom: roomId=$roomId"
        )

        val request = UpdateChatRoomRequest(
            title = title,
            image = image,
            subject = subject
        )

        val response = api.updateRoom(
            roomId = roomId,
            request = request
        )

        if (response.success && response.data?.room != null) {
            NetworkResult.Success(response.data.room.toDomain())
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در به‌روزرسانی گفتگو"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "updateRoom error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun addMembers(
        roomId: Int,
        userIds: List<Int>
    ): NetworkResult<ChatRoom> = try {
        Log.d(
            TAG,
            "addMembers: roomId=$roomId count=${userIds.size}"
        )

        val request = AddRoomMembersRequest(userIds = userIds)

        val response = api.addMembers(
            roomId = roomId,
            request = request
        )

        if (response.success && response.data?.room != null) {
            NetworkResult.Success(response.data.room.toDomain())
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در افزودن اعضا"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "addMembers error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun removeMember(
        roomId: Int,
        userId: Int
    ): NetworkResult<ChatRoom> = try {
        Log.d(
            TAG,
            "removeMember: roomId=$roomId userId=$userId"
        )

        val response = api.removeMember(
            roomId = roomId,
            userId = userId
        )

        if (response.success && response.data?.room != null) {
            NetworkResult.Success(response.data.room.toDomain())
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در حذف عضو"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "removeMember error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun deleteRoom(roomId: Int): NetworkResult<Unit> = try {
        Log.d(
            TAG,
            "deleteRoom: roomId=$roomId"
        )

        val response = api.deleteRoom(roomId = roomId)

        if (response.success) {
            NetworkResult.Success(Unit)
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در حذف گفتگو"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "deleteRoom error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun lockRoom(roomId: Int): NetworkResult<ChatRoom> = try {
        Log.d(
            TAG,
            "lockRoom: roomId=$roomId"
        )

        val response = api.lockRoom(roomId = roomId)

        if (response.success && response.data?.room != null) {
            NetworkResult.Success(response.data.room.toDomain())
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در قفل گفتگو"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "lockRoom error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun unlockRoom(roomId: Int): NetworkResult<ChatRoom> = try {
        Log.d(
            TAG,
            "unlockRoom: roomId=$roomId"
        )

        val response = api.unlockRoom(roomId = roomId)

        if (response.success && response.data?.room != null) {
            NetworkResult.Success(response.data.room.toDomain())
        } else {
            NetworkResult.Error(
                response.message
                        ?: "خطا در باز کردن قفل گفتگو"
            )
        }
    } catch (e: Exception) {
        Log.e(
            TAG,
            "unlockRoom error: ${e.message}",
            e
        )
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }
}
