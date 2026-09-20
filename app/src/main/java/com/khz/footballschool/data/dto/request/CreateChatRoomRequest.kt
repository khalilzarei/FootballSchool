package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

/**
 * ایجاد اتاق چت — مطابق قرارداد سرور (ChatController::createRoom):
 * اتاق همیشه بین کاربر جاری و targetUserId است؛ سرور با unique_key
 * اتاق موجود را برمی‌گرداند (idempotent).
 *
 * room_type فقط یکی از: guardian_admin | coach_admin | guardian_coach
 * (سرور participant_ids ندارد؛ اعضا = کاربر جاری + targetUserId)
 */
//data class CreateChatRoomRequest(
//    /** اگر ارسال نشود (null)، سرور خودش از نقش دو کاربر استنتاج می‌کند */
//    @SerializedName("room_type") val roomType: String? = null,
//    @SerializedName("target_user_id") val targetUserId: Int,
//    @SerializedName("player_id") val playerId: Int? = null,
//    @SerializedName("class_id") val classId: Int? = null,
//    @SerializedName("subject") val subject: String? = null
//)

data class CreateChatRoomRequest(
    @SerializedName("target_user_id")
    val targetUserId: Int
)

data class CreateGroupChatRoomRequest(
    @SerializedName("is_group")
    val isGroup: Boolean = true,

    @SerializedName("title")
    val title: String,

    @SerializedName("user_ids")
    val userIds: List<Int>
)