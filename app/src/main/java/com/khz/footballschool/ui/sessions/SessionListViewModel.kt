package com.khz.footballschool.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.repository.SessionRepository
import com.khz.footballschool.domain.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SessionListViewModel(private val repository: SessionRepository) : ViewModel() {
    private val _state = MutableStateFlow<SessionListState>(SessionListState.Loading)
    val state: StateFlow<SessionListState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = SessionListState.Loading
            when (val r = repository.getSessions()) {
                is NetworkResult.Success -> _state.value = SessionListState.Success(r.data.items) // ← items
                is NetworkResult.Error   -> _state.value = SessionListState.Error(r.message)
                else                     -> {}
            }
        }
    }

    fun cancelSession(id: Int) {
        viewModelScope.launch { repository.cancelSession(id); load() }
    }

    fun completeSession(id: Int) {
        viewModelScope.launch { repository.completeSession(id); load() }
    }
}

sealed class SessionListState {
    object Loading : SessionListState()
    data class Success(val sessions: List<Session>) : SessionListState()
    data class Error(val message: String) : SessionListState()
}