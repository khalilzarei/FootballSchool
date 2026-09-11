package com.khz.footballschool.ui.chat

import com.khz.footballschool.data.repository.ChatRepository
import com.khz.footballschool.domain.model.ChatRoom
import com.khz.footballschool.ui.components.SimpleListViewModel

class ChatRoomListViewModel(repo: ChatRepository) :
    SimpleListViewModel<ChatRoom>({ repo.getRooms() })