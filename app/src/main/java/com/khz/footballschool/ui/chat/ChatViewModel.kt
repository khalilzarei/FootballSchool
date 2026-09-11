package com.khz.footballschool.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.SendChatMessageRequest
import com.khz.footballschool.data.repository.ChatRepository
import com.khz.footballschool.domain.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val repo: ChatRepository) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun load(roomId: Int) {
        viewModelScope.launch {
            _loading.value = true
            (repo.getMessages(roomId) as? NetworkResult.Success)?.let { _messages.value = it.data }
            _loading.value = false
        }
    }

    fun send(
        roomId: Int,
        body: String
    ) {
        viewModelScope.launch {
            val r = repo.sendMessage(
                roomId,
                body,
            )

            if (r is NetworkResult.Success) _messages.value += r.data
        }
    }
}