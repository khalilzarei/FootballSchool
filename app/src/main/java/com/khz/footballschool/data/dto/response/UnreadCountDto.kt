package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class UnreadCountDto(
    @SerializedName("unread_count") val unreadCount: Int
)