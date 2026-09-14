package com.khz.footballschool.data.repository

import android.util.Log
import com.khz.footballschool.core.network.ApiErrorHandler
import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.network.PaginatedResponse
import com.khz.footballschool.data.dto.request.CreateClassRequest
import com.khz.footballschool.data.dto.request.CreateScheduleRequest
import com.khz.footballschool.data.dto.request.EnrollPlayerRequest
import com.khz.footballschool.data.dto.request.UpdateClassRequest
import com.khz.footballschool.data.dto.request.UpdateEnrollmentRequest
import com.khz.footballschool.data.dto.request.UpdateScheduleRequest
import com.khz.footballschool.data.dto.response.ClassDto
import com.khz.footballschool.data.dto.response.ClassScheduleDto
import com.khz.footballschool.data.dto.response.EnrollAgeGroupResultDto
import com.khz.footballschool.data.dto.response.EnrollmentDto
import com.khz.footballschool.data.remote.ClassApi
import com.khz.footballschool.data.remote.ClassScheduleApi
import com.khz.footballschool.data.remote.EnrollmentApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.ClassSchedule
import com.khz.footballschool.domain.model.Enrollment
import com.khz.footballschool.domain.model.FootballClass

class ClassRepository(
    private val api: ClassApi,
    private val classScheduleApi: ClassScheduleApi,
    private val enrollmentApi: EnrollmentApi
) {

    companion object {
        private const val TAG = "ClassRepository"
    }

    suspend fun getClasses(
        page: Int = 1,
        perPage: Int = 50,
        query: String? = null,
        status: String? = null
    ): NetworkResult<PaginatedResponse<FootballClass>> = try {
        val r = api.getClasses(page, perPage, query, status)
        if (r.success && r.data != null) {
            NetworkResult.Success(
                PaginatedResponse(
                    items = r.data.items.map { it.toDomain() },
                    total = r.data.total,
                    page = r.data.page,
                    perPage = r.data.perPage
                )
            )
        } else {
            NetworkResult.Error(r.message ?: "خطا در دریافت کلاس‌ها")
        }
    } catch (e: Exception) {
        Log.e(TAG, "getClasses error: ${e.message}", e)
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun getClass(id: Int): NetworkResult<FootballClass> = try {
        val r = api.getClass(id)
        val dto = r.data.unwrap<ClassDto>("class")
        if (r.success && dto != null) {
            NetworkResult.Success(dto.toDomain())
        } else {
            NetworkResult.Error(r.message ?: "خطا در دریافت کلاس")
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun createClass(
        title: String,
        seasonId: Int? = null,
        ageGroupId: Int? = null,
        coachId: Int? = null,
        assistantCoachId: Int? = null,
        capacity: Int? = null,
        status: String = "active",
        location: String? = null,
        description: String? = null,
        pricingType: String? = null,
        monthlyFee: Long? = null,
        sessionFee: Long? = null,
        registrationFee: Long? = null,
        startDate: String? = null,
        endDate: String? = null
    ): NetworkResult<FootballClass> = try {
        val request = CreateClassRequest(
            title = title,
            seasonId = seasonId,
            ageGroupId = ageGroupId,
            coachId = coachId,
            assistantCoachId = assistantCoachId,
            capacity = capacity,
            status = status,
            location = location,
            description = description,
            pricingType = pricingType,
            monthlyFee = monthlyFee,
            sessionFee = sessionFee,
            registrationFee = registrationFee,
            startDate = startDate,
            endDate = endDate
        )

        Log.d(TAG, "createClass: $title")
        val r = api.createClass(request)
        val dto = r.data.unwrap<ClassDto>("class")

        if (r.success && dto != null) {
            NetworkResult.Success(dto.toDomain())
        } else {
            NetworkResult.Error(r.message ?: "خطا در ایجاد کلاس")
        }
    } catch (e: Exception) {
        Log.e(TAG, "createClass error: ${e.message}", e)
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun updateClass(
        id: Int,
        title: String? = null,
        seasonId: Int? = null,
        ageGroupId: Int? = null,
        coachId: Int? = null,
        assistantCoachId: Int? = null,
        capacity: Int? = null,
        status: String? = null,
        location: String? = null,
        description: String? = null,
        pricingType: String? = null,
        monthlyFee: Long? = null,
        sessionFee: Long? = null,
        registrationFee: Long? = null,
        startDate: String? = null,
        endDate: String? = null
    ): NetworkResult<FootballClass> = try {
        val request = UpdateClassRequest(
            title = title,
            seasonId = seasonId,
            ageGroupId = ageGroupId,
            coachId = coachId,
            assistantCoachId = assistantCoachId,
            capacity = capacity,
            status = status,
            location = location,
            description = description,
            pricingType = pricingType,
            monthlyFee = monthlyFee,
            sessionFee = sessionFee,
            registrationFee = registrationFee,
            startDate = startDate,
            endDate = endDate
        )

        Log.d(TAG, "updateClass: id=$id, title=$title")
        val r = api.updateClass(id, request)
        val dto = r.data.unwrap<ClassDto>("class")

        if (r.success && dto != null) {
            NetworkResult.Success(dto.toDomain())
        } else {
            NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی کلاس")
        }
    } catch (e: Exception) {
        Log.e(TAG, "updateClass error: ${e.message}", e)
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun toggleStatus(id: Int, activate: Boolean): NetworkResult<Unit> = try {
        val r = if (activate) api.activateClass(id) else api.deactivateClass(id)
        if (r.success) NetworkResult.Success(Unit)
        else NetworkResult.Error(r.message ?: "خطا در تغییر وضعیت")
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun deleteClass(id: Int): NetworkResult<Unit> = try {
        val r = api.deleteClass(id)
        if (r.success) NetworkResult.Success(Unit)
        else NetworkResult.Error(r.message ?: "خطا در حذف کلاس")
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Schedules (برنامه هفتگی کلاس)
    // ═════════════════════════════════════════════

    suspend fun getSchedules(classId: Int): NetworkResult<List<ClassSchedule>> = try {
        val r = classScheduleApi.getSchedules(classId)
        if (r.success && r.data != null) {
            NetworkResult.Success((r.data.schedules ?: emptyList()).map { it.toDomain() })
        } else {
            NetworkResult.Error(r.message ?: "خطا در دریافت برنامه هفتگی")
        }
    } catch (e: Exception) {
        Log.e(TAG, "getSchedules error: ${e.message}", e)
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun createSchedule(
        classId: Int,
        weekday: Int,
        startTime: String,
        endTime: String,
        location: String? = null
    ): NetworkResult<ClassSchedule> = try {
        val r = classScheduleApi.createSchedule(
            classId,
            CreateScheduleRequest(weekday, startTime, endTime, location, "active")
        )
        val dto = r.data.unwrap<ClassScheduleDto>("schedule")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در ایجاد برنامه")
    } catch (e: Exception) {
        Log.e(TAG, "createSchedule error: ${e.message}", e)
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun updateSchedule(
        scheduleId: Int,
        weekday: Int,
        startTime: String,
        endTime: String,
        location: String? = null,
        status: String? = null
    ): NetworkResult<ClassSchedule> = try {
        val r = classScheduleApi.updateSchedule(
            scheduleId,
            UpdateScheduleRequest(weekday, startTime, endTime, location, status)
        )
        val dto = r.data.unwrap<ClassScheduleDto>("schedule")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی برنامه")
    } catch (e: Exception) {
        Log.e(TAG, "updateSchedule error: ${e.message}", e)
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun toggleScheduleStatus(scheduleId: Int, activate: Boolean): NetworkResult<Unit> = try {
        val r = if (activate) classScheduleApi.activateSchedule(scheduleId)
        else classScheduleApi.deactivateSchedule(scheduleId)
        if (r.success) NetworkResult.Success(Unit)
        else NetworkResult.Error(r.message ?: "خطا در تغییر وضعیت برنامه")
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    // ═════════════════════════════════════════════
    // Enrollments (ثبت‌نام بازیکنان در کلاس)
    // ═════════════════════════════════════════════

    suspend fun getClassPlayers(
        classId: Int,
        page: Int = 1,
        perPage: Int = 50,
        query: String? = null,
        status: String? = null
    ): NetworkResult<PaginatedResponse<Enrollment>> = try {
        val r = enrollmentApi.getClassPlayers(classId, page, perPage, query, status)
        if (r.success && r.data != null) {
            NetworkResult.Success(
                PaginatedResponse(
                    items = r.data.items.map { it.toDomain() },
                    total = r.data.total,
                    page = r.data.page,
                    perPage = r.data.perPage
                )
            )
        } else {
            NetworkResult.Error(r.message ?: "خطا در دریافت بازیکنان کلاس")
        }
    } catch (e: Exception) {
        Log.e(TAG, "getClassPlayers error: ${e.message}", e)
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun enrollPlayer(
        classId: Int,
        playerId: Int,
        enrolledAt: String? = null,
        monthlyFeeOverride: Long? = null,
        sessionFeeOverride: Long? = null,
        registrationFeeOverride: Long? = null,
        notes: String? = null
    ): NetworkResult<Enrollment> = try {
        val r = enrollmentApi.enrollPlayer(
            classId,
            EnrollPlayerRequest(
                playerId = playerId,
                status = "active",
                enrolledAt = enrolledAt,
                endedAt = null,
                monthlyFeeOverride = monthlyFeeOverride,
                sessionFeeOverride = sessionFeeOverride,
                registrationFeeOverride = registrationFeeOverride,
                notes = notes
            )
        )
        val dto = r.data.unwrap<EnrollmentDto>("enrollment")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در ثبت‌نام بازیکن")
    } catch (e: Exception) {
        Log.e(TAG, "enrollPlayer error: ${e.message}", e)
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun toggleEnrollmentStatus(enrollmentId: Int, activate: Boolean): NetworkResult<Unit> = try {
        val r = if (activate) enrollmentApi.activateEnrollment(enrollmentId)
        else enrollmentApi.deactivateEnrollment(enrollmentId)
        if (r.success) NetworkResult.Success(Unit)
        else NetworkResult.Error(r.message ?: "خطا در تغییر وضعیت ثبت‌نام")
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    /**
     * پایان ثبت‌نام بازیکن (status = completed + تاریخ پایان امروز)
     */
    suspend fun endEnrollment(enrollmentId: Int): NetworkResult<Enrollment> = try {
        val r = enrollmentApi.updateEnrollment(
            enrollmentId,
            UpdateEnrollmentRequest(
                status = "completed",
                enrolledAt = null,
                endedAt = java.time.LocalDate.now().toString(),
                monthlyFeeOverride = null,
                sessionFeeOverride = null,
                registrationFeeOverride = null,
                notes = null
            )
        )
        val dto = r.data.unwrap<EnrollmentDto>("enrollment")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در پایان ثبت‌نام")
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    /**
     * ثبت‌نام گروهی: همه بازیکنان فعالِ گروه سنیِ کلاس
     * فقط برای کلاس‌هایی که گروه سنی دارند (کلاس فوق‌العاده = خطای سرور)
     */
    suspend fun enrollAgeGroup(classId: Int): NetworkResult<EnrollAgeGroupResultDto> = try {
        val r = enrollmentApi.enrollAgeGroup(classId)
        if (r.success && r.data != null) {
            NetworkResult.Success(r.data)
        } else {
            NetworkResult.Error(r.message ?: "خطا در ثبت‌نام گروهی")
        }
    } catch (e: Exception) {
        Log.e(TAG, "enrollAgeGroup error: ${e.message}", e)
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }
}