package com.khz.footballschool.data.repository

import android.util.Log
import com.khz.footballschool.core.network.ApiErrorHandler
import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.network.PaginatedResponse
import com.khz.footballschool.data.dto.request.CreateClassRequest
import com.khz.footballschool.data.dto.request.UpdateClassRequest
import com.khz.footballschool.data.dto.response.ClassDto
import com.khz.footballschool.data.remote.ClassApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.FootballClass

class ClassRepository(private val api: ClassApi) {

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
}