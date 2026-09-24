package com.khz.malekadmin.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.repository.ChatRepository
import com.khz.malekadmin.domain.model.ChatRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatRoomListUiState(
    val rooms: List<ChatRoom> = emptyList(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val error: String? = null
)

class ChatRoomListViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatRoomListUiState())
    val state: StateFlow<ChatRoomListUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                loading = _state.value.rooms.isEmpty(),
                refreshing = _state.value.rooms.isNotEmpty(),
                error = null
            )

            when (val result = repository.getRooms()) {
                is NetworkResult.Success -> {
                    _state.value = ChatRoomListUiState(
                        rooms = result.data,
                        loading = false,
                        refreshing = false
                    )
                }

                is NetworkResult.Error   -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        refreshing = false,
                        error = result.message
                    )
                }

                else                     -> {
                    _state.value = _state.value.copy(
                        loading = false,
                        refreshing = false
                    )
                }
            }
        }
    }

    fun refresh() {
        load()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}