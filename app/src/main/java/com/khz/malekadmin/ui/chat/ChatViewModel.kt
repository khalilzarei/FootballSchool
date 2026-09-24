package com.khz.malekadmin.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.repository.ChatRepository
import com.khz.malekadmin.domain.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repo: ChatRepository
) : ViewModel() {

    private val _messages =
        MutableStateFlow<List<ChatMessage>>(emptyList())

    val messages: StateFlow<List<ChatMessage>> =
        _messages

    private val _loading =
        MutableStateFlow(false)

    val loading: StateFlow<Boolean> =
        _loading

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error

    fun load(roomId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            when (val result = repo.getMessages(roomId)) {
                is NetworkResult.Success -> {
                    _messages.value = result.data

                    result.data
                        .maxOfOrNull { it.id }
                        ?.let { lastId ->
                            repo.markAsRead(
                                roomId,
                                lastId
                            )
                        }
                }

                is NetworkResult.Error -> {
                    _error.value = result.message
                }

                else -> Unit
            }

            _loading.value = false
        }
    }

    fun refresh(roomId: Int) {
        viewModelScope.launch {
            when (
                val result =
                    repo.getMessages(roomId)
            ) {
                is NetworkResult.Success -> {
                    mergeMessages(result.data)

                    result.data
                        .maxOfOrNull { it.id }
                        ?.let { lastId ->
                            repo.markAsRead(
                                roomId,
                                lastId
                            )
                        }
                }

                is NetworkResult.Error -> {
                    _error.value = result.message
                }

                else -> Unit
            }
        }
    }

    fun send(
        roomId: Int,
        body: String
    ) {
        val text = body.trim()

        if (text.isEmpty()) return

        viewModelScope.launch {
            when (
                val result =
                    repo.sendMessage(roomId, text)
            ) {
                is NetworkResult.Success -> {
                    mergeMessages(
                        listOf(result.data)
                    )
                }

                is NetworkResult.Error -> {
                    _error.value = result.message
                }

                else -> Unit
            }
        }
    }

    fun markAsRead(
        roomId: Int,
        lastReadMessageId: Int
    ) {
        if (lastReadMessageId <= 0) return

        viewModelScope.launch {
            repo.markAsRead(
                roomId,
                lastReadMessageId
            )
        }
    }

    private fun mergeMessages(
        incoming: List<ChatMessage>
    ) {
        if (incoming.isEmpty()) return

        val merged =
            (_messages.value + incoming)
                .distinctBy { it.id }
                .sortedBy { it.id }

        _messages.value = merged
    }
}