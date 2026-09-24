package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class UnreadCountDto(
    @SerializedName("unread_count") val unreadCount: Int
)