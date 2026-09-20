package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class ChatRoomsPayload(
    val rooms: List<ChatRoomDto> = emptyList()
)

data class ChatRoomPayload(
    val room: ChatRoomDto
)

data class ChatMessagesPayload(
    val messages: List<ChatMessageDto> = emptyList()
)

data class ChatMessagePayload(
    val message: ChatMessageDto
)

data class ChatReadPayload(
    val roomId: Int,
    val lastReadMessageId: Int
)

data class ChatContactsPayload(
    val contacts: List<ChatContactDto> = emptyList()
)