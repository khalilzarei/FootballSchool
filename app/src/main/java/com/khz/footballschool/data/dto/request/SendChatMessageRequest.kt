package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

data class SendChatMessageRequest(
    @SerializedName("message_type") val messageType: String = "text",
    @SerializedName("body") val body: String,
    @SerializedName("media_id") val mediaId: Int? = null
)