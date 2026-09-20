package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

/**
 * تغییر پروفایل روم (فقط ادمین)
 * PUT /chat/rooms/{id}
 */
data class UpdateChatRoomRequest(
    @SerializedName("title") val title: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("subject") val subject: String? = null
)

/**
 * افزودن عضو به روم (فقط ادمین)
 * POST /chat/rooms/{id}/members
 */
data class AddRoomMembersRequest(
    @SerializedName("user_ids") val userIds: List<Int>
)
