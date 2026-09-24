package com.khz.malekadmin.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.repository.ClassRepository
import com.khz.malekadmin.data.repository.SessionRepository
import com.khz.malekadmin.domain.model.FootballClass
import com.khz.malekadmin.domain.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SessionListViewModel(
    private val repository: SessionRepository,
    private val classRepo: ClassRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SessionListState>(SessionListState.Idle)
    val state: StateFlow<SessionListState> = _state

    private val _classes = MutableStateFlow<List<FootballClass>>(emptyList())
    val classes: StateFlow<List<FootballClass>> = _classes

    private val _classesLoading = MutableStateFlow(true)
    val classesLoading: StateFlow<Boolean> = _classesLoading

    private val _classesError = MutableStateFlow<String?>(null)
    val classesError: StateFlow<String?> = _classesError

    private val _selectedClassId = MutableStateFlow<Int?>(null)
    val selectedClassId: StateFlow<Int?> = _selectedClassId

    init {
        loadClasses()
    }

    fun loadClasses() {
        viewModelScope.launch {
            _classesLoading.value = true
            _classesError.value = null
            when (val r = classRepo.getClasses(perPage = 100)) {
                is NetworkResult.Success -> _classes.value = r.data.items
                is NetworkResult.Error -> _classesError.value = r.message
                else -> {}
            }
            _classesLoading.value = false
        }
    }

    /** انتخاب کلاس از اسپینر — نال یعنی هنوز چیزی انتخاب نشده */
    fun selectClass(classId: Int?) {
        _selectedClassId.value = classId
        if (classId == null) {
            _state.value = SessionListState.Idle
        } else {
            load()
        }
    }

    fun load() {
        val classId = _selectedClassId.value
        if (classId == null) {
            _state.value = SessionListState.Idle
            return
        }
        viewModelScope.launch {
            _state.value = SessionListState.Loading
            when (val r = repository.getSessions(classId = classId, perPage = 100)) {
                is NetworkResult.Success -> _state.value = SessionListState.Success(
                    // مرتب‌سازی صعودی بر اساس تاریخ (و ساعت شروع) — اولین جلسه بالای لیست
                    r.data.items.sortedWith(compareBy({ it.sessionDate }, { it.startTime ?: "" }))
                )
                is NetworkResult.Error -> _state.value = SessionListState.Error(r.message)
                else -> {}
            }
        }
    }

    fun cancelSession(id: Int) {
        viewModelScope.launch { repository.cancelSession(id); load() }
    }

    fun completeSession(id: Int) {
        viewModelScope.launch { repository.completeSession(id); load() }
    }

    /**
     * ثبت/ویرایش توضیح جلسه توسط مربی (topic + notes)
     */
    fun updateSessionInfo(id: Int, topic: String?, notes: String?, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val r = repository.updateSession(
                id,
                com.khz.malekadmin.data.dto.request.UpdateSessionRequest(
                    sessionDate = null,
                    startTime = null,
                    endTime = null,
                    location = null,
                    topic = topic?.takeIf { it.isNotBlank() },
                    status = null,
                    notes = notes?.takeIf { it.isNotBlank() }
                )
            )
            onDone(r is NetworkResult.Success)
            load()
        }
    }
}

sealed class SessionListState {
    /** هنوز کلاسی انتخاب نشده است */
    object Idle : SessionListState()
    object Loading : SessionListState()
    data class Success(val sessions: List<Session>) : SessionListState()
    data class Error(val message: String) : SessionListState()
}