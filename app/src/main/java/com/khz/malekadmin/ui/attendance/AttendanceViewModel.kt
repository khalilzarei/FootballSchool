package com.khz.malekadmin.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.dto.request.AttendanceItemRequest
import com.khz.malekadmin.data.repository.ClassRepository
import com.khz.malekadmin.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AttendanceRow(
    val playerId: Int,
    val playerName: String,
    val playerAvatar: String? = null,
    val status: String = "present",
    val isBillable: Boolean = true,
    val note: String? = null
)

class AttendanceViewModel(
    private val sessionRepo: SessionRepository,
    private val classRepo: ClassRepository
) : ViewModel() {

    private val _rows = MutableStateFlow<List<AttendanceRow>>(emptyList())
    val rows: StateFlow<List<AttendanceRow>> = _rows

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(sessionId: Int, classId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            // ─── ریکوئست جدید سرور: بازیکنان + حضور و غیاب ذخیره‌شده در یک پاسخ ───
            val sheet = sessionRepo.getAttendanceSheet(sessionId)

            if (sheet is NetworkResult.Success) {
                _rows.value = sheet.data.map { p ->
                    AttendanceRow(
                        playerId = p.playerId,
                        playerName = p.fullName?.takeIf { it.isNotBlank() }
                                ?: listOfNotNull(p.firstName, p.lastName).joinToString(" ").ifBlank { "بازیکن" },
                        playerAvatar = p.avatarUrl,
                        status = p.status ?: "present",
                        isBillable = p.isBillable ?: true,
                        note = p.note
                    )
                }
            } else {
                // ─── فالبک: هاست هنوز endpoint جدید را ندارد → مثل قبل دو ریکوئست ───
                val enrollmentsResult = classRepo.getClassPlayers(classId, perPage = 100)
                val enrollments = (enrollmentsResult as? NetworkResult.Success)?.data?.items ?: emptyList()
                val attendanceResult = sessionRepo.getSessionAttendance(sessionId)
                val existing = (attendanceResult as? NetworkResult.Success)?.data ?: emptyList()

                _rows.value = enrollments.map { e ->
                    val att = existing.find { it.playerId == e.playerId }
                    AttendanceRow(
                        playerId = e.playerId,
                        playerName = e.playerFullName,
                        playerAvatar = e.player?.avatarPath,
                        status = att?.status ?: "present",
                        isBillable = att?.isBillable ?: true,
                        note = att?.note
                    )
                }

                _error.value = when {
                    enrollments.isEmpty() && enrollmentsResult is NetworkResult.Error ->
                        enrollmentsResult.message
                    attendanceResult is NetworkResult.Error ->
                        "خطا در دریافت حضور و غیاب ذخیره‌شده: ${attendanceResult.message}"
                    enrollments.isEmpty() && sheet is NetworkResult.Error ->
                        sheet.message
                    else -> null
                }
            }

            _loading.value = false
        }
    }

    fun setStatus(playerId: Int, status: String) {
        _rows.value = _rows.value.map { if (it.playerId == playerId) it.copy(status = status) else it }
    }

    fun setNote(playerId: Int, note: String) {
        _rows.value = _rows.value.map { if (it.playerId == playerId) it.copy(note = note) else it }
    }

    fun save(sessionId: Int) {
        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            val items = _rows.value.map { AttendanceItemRequest(it.playerId, it.status, it.isBillable, it.note) }
            val request = com.khz.malekadmin.data.dto.request.SaveBulkAttendanceRequest(items)
            when (val result = sessionRepo.saveBulkAttendance(sessionId, request)) {
                is NetworkResult.Success -> _saved.value = true
                is NetworkResult.Error -> _error.value = result.message
                else -> {}
            }
            _saving.value = false
        }
    }
}