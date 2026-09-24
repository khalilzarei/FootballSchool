package com.khz.malekadmin.data.dto.response

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