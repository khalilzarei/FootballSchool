package com.khz.footballschool.data.repository

import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.network.PaginatedResponse
import com.khz.footballschool.data.dto.request.CreateSessionRequest
import com.khz.footballschool.data.dto.request.GenerateSessionsRequest
import com.khz.footballschool.data.dto.request.SaveBulkAttendanceRequest
import com.khz.footballschool.data.dto.request.UpdateSessionRequest
import com.khz.footballschool.data.dto.response.SessionDto
import com.khz.footballschool.data.remote.AttendanceApi
import com.khz.footballschool.data.remote.SessionApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.Attendance
import com.khz.footballschool.domain.model.Session

class SessionRepository(
    private val sessionApi: SessionApi,
    private val attendanceApi: AttendanceApi
) {

    suspend fun getSessions(
        page: Int = 1,
        perPage: Int = 20,
        classId: Int? = null,
        status: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): NetworkResult<PaginatedResponse<Session>> = try {
        val r = sessionApi.getSessions(page, perPage, classId, status, dateFrom, dateTo)
        if (r.success && r.data != null) {
            NetworkResult.Success(
                PaginatedResponse(
                    items = r.data.items.map { it.toDomain() },
                    total = r.data.total,
                    page = r.data.page,
                    perPage = r.data.perPage
                )
            )
        } else NetworkResult.Error(r.message ?: "خطا در دریافت جلسات")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun createSession(request: CreateSessionRequest): NetworkResult<Session> = try {
        val r = sessionApi.createSession(request)
        val dto = r.data.unwrap<SessionDto>("session")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در ایجاد جلسه")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun generateSessions(request: GenerateSessionsRequest): NetworkResult<List<Session>> = try {
        val r = sessionApi.generateSessions(request)
        if (r.success && r.data != null) NetworkResult.Success(r.data.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در تولید جلسات")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getSession(id: Int): NetworkResult<Session> = try {
        val r = sessionApi.getSession(id)
        val dto = r.data.unwrap<SessionDto>("session")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در دریافت جلسه")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun updateSession(id: Int, request: UpdateSessionRequest): NetworkResult<Session> = try {
        val r = sessionApi.updateSession(id, request)
        val dto = r.data.unwrap<SessionDto>("session")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی جلسه")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun cancelSession(id: Int): NetworkResult<Unit> = try {
        val r = sessionApi.cancelSession(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun completeSession(id: Int): NetworkResult<Unit> = try {
        val r = sessionApi.completeSession(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getSessionAttendance(sessionId: Int): NetworkResult<List<Attendance>> = try {
        val r = attendanceApi.getSessionAttendance(sessionId)
        if (r.success && r.data != null) NetworkResult.Success(r.data.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت حضور و غیاب")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun saveBulkAttendance(sessionId: Int, request: SaveBulkAttendanceRequest): NetworkResult<List<Attendance>> = try {
        val r = attendanceApi.saveBulkAttendance(sessionId, request)
        if (r.success && r.data != null) NetworkResult.Success(r.data.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در ثبت حضور و غیاب")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }
}