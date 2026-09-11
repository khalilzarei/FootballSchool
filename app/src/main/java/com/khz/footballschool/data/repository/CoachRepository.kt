package com.khz.footballschool.data.repository

import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.UpdateCoachRequest
import com.khz.footballschool.data.dto.response.CoachDto
import com.khz.footballschool.data.remote.CoachApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.Coach

class CoachRepository(private val api: CoachApi) {

    suspend fun getCoaches(page: Int = 1, perPage: Int = 20, q: String? = null, status: String? = null): NetworkResult<List<Coach>> = try {
        val r = api.getCoaches(page, perPage, q, status)
        if (r.success && r.data != null) NetworkResult.Success(r.data.items.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت مربیان")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getCoach(id: Int): NetworkResult<Coach> = try {
        val r = api.getCoach(id)
        val dto = r.data.unwrap<CoachDto>("coach")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در دریافت مربی")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun updateCoach(id: Int, request: UpdateCoachRequest): NetworkResult<Coach> = try {
        val r = api.updateCoach(id, request)
        val dto = r.data.unwrap<CoachDto>("coach")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی مربی")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }
}