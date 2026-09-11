package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

data class CreateChatRoomRequest(
    @SerializedName("participant_ids") val participantIds: List<Int>,
    @SerializedName("room_type") val roomType: String,
    @SerializedName("target_user_id") val targetUserId: Int? = null,
    @SerializedName("player_id") val playerId: Int? = null,
    @SerializedName("class_id") val classId: Int? = null,
    @SerializedName("subject") val subject: String? = null
)