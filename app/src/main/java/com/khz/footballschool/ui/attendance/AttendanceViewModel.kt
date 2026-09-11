package com.khz.footballschool.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.AttendanceItemRequest
import com.khz.footballschool.data.repository.ClassRepository
import com.khz.footballschool.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AttendanceRow(
    val playerId: Int,
    val playerName: String,
    var status: String = "present",
    var isBillable: Boolean = true,
    var note: String? = null
)

class AttendanceViewModel(
    private val sessionRepo: SessionRepository,
    private val classRepo: ClassRepository
) : ViewModel() {

    private val _rows = MutableStateFlow<List<AttendanceRow>>(emptyList())
    val rows: StateFlow<List<AttendanceRow>> = _rows

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    fun load(sessionId: Int, classId: Int) {
//        viewModelScope.launch {
//            _loading.value = true
//            val enrollmentsResult = classRepo.getClassPlayers(classId, perPage = 100)
//            val enrollments = (enrollmentsResult as? NetworkResult.Success)?.data?.items ?: emptyList() // ← items
//            val existing = (sessionRepo.getSessionAttendance(sessionId) as? NetworkResult.Success)?.data ?: emptyList()
//            _rows.value = enrollments.map { e ->
//                val att = existing.find { it.playerId == e.playerId }
//                AttendanceRow(
//                    playerId = e.playerId,
//                    playerName = e.player.fullName,
//                    status = att?.status ?: "present",
//                    isBillable = att?.isBillable ?: true,
//                    note = att?.note
//                )
//            }
//            _loading.value = false
//        }
    }

    fun setStatus(playerId: Int, status: String) {
        _rows.value = _rows.value.map { if (it.playerId == playerId) it.copy(status = status) else it }
    }

    fun toggleBillable(playerId: Int) {
        _rows.value = _rows.value.map { if (it.playerId == playerId) it.copy(isBillable = !it.isBillable) else it }
    }

    fun save(sessionId: Int) {
        viewModelScope.launch {
            val items = _rows.value.map { AttendanceItemRequest(it.playerId, it.status, it.isBillable, it.note) }
            val request = com.khz.footballschool.data.dto.request.SaveBulkAttendanceRequest(items)
            val result = sessionRepo.saveBulkAttendance(sessionId, request)
            if (result is NetworkResult.Success) _saved.value = true
        }
    }
}