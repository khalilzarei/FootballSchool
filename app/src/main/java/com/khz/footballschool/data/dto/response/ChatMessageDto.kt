package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * DTO پیام چت — با هر دو ساختار پاسخ سرور سازگار است:
 *
 * ساختار جدید (ChatRepository::hydrateMessage):
 * {"id":41,"room_id":7,"sender":{"id":9,"full_name":"...","role":"coach"},
 *  "message_type":"text","body":"...","is_read":true,"created_at":"..."}
 *
 * ساختار قدیمی (SELECT msg.* خام):
 * {"id":41,"chat_room_id":7,"sender_name":"...","sender_role":"coach",
 *  "message_type":"text","body":"...","sent_at":"..."}
 *
 * نکته: مقدارهای پیش‌فرض کاتلین در Gson (به‌دلیل Unsafe.allocateInstance)
 * اعمال نمی‌شوند؛ بنابراین همه فیلدهای غایب‌پذیر، نال‌پذیر تعریف شده‌اند
 * و حل نهایی توی computed property ها انجام می‌شود.
 */
data class ChatMessageDto(
    @SerializedName("id") val id: Int = 0,

    // ─── ساختار جدید سرور ───
    @SerializedName("room_id") val roomId: Int? = null,
    @SerializedName("sender") val sender: UserDto? = null,

    // ─── ساختار قدیمی سرور (برای سازگاری تا زمان استقرار نسخه جدید) ───
    @SerializedName("chat_room_id") val chatRoomId: Int? = null,
    @SerializedName("sender_name") val senderName: String? = null,
    @SerializedName("sender_role") val senderRole: String? = null,

    @SerializedName("sender_id") val senderId: Int = 0,
    @SerializedName("message_type") val messageType: String = "text",
    @SerializedName("body") val body: String? = null,
    @SerializedName("media_id") val mediaId: Int? = null,
    @SerializedName("media") val media: MediaDto? = null,

    @SerializedName("is_read") val isRead: Boolean = false,
    @SerializedName("read_at") val readAt: String? = null,
    @SerializedName("sent_at") val sentAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
) {
    /** شناسه اتاق: room_id (جدید) یا chat_room_id (قدیمی)؛ 0 اگر هیچ‌کدام نیامده باشد */
    val effectiveRoomId: Int
        get() = roomId ?: chatRoomId ?: 0

    /** نام فرستنده: از شیء sender (جدید) یا فیلد تخت sender_name (قدیمی) */
    val effectiveSenderName: String?
        get() = sender?.fullName ?: senderName

    /** نقش فرستنده: از شیء sender (جدید) یا فیلد تخت sender_role (قدیمی) */
    val effectiveSenderRole: String?
        get() = sender?.role ?: senderRole
}
