package com.khz.malekadmin.data.repository

import com.khz.malekadmin.core.network.JsonParser.unwrap
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.dto.request.CreateSeasonRequest
import com.khz.malekadmin.data.dto.request.UpdateSeasonRequest
import com.khz.malekadmin.data.dto.response.SeasonDto
import com.khz.malekadmin.data.remote.SeasonApi
import com.khz.malekadmin.domain.mapper.toDomain
import com.khz.malekadmin.domain.model.Season

class SeasonRepository(private val api: SeasonApi) {

    suspend fun getSeasons(page: Int = 1, perPage: Int = 20, q: String? = null, status: String? = null): NetworkResult<List<Season>> = try {
        val r = api.getSeasons(page, perPage, q, status)
        if (r.success && r.data != null) NetworkResult.Success(r.data.items.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت فصل‌ها")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun createSeason(request: CreateSeasonRequest): NetworkResult<Season> = try {
        val r = api.createSeason(request)
        val dto = r.data.unwrap<SeasonDto>("season")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در ایجاد فصل")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getSeason(id: Int): NetworkResult<Season> = try {
        val r = api.getSeason(id)
        val dto = r.data.unwrap<SeasonDto>("season")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در دریافت فصل")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun updateSeason(id: Int, request: UpdateSeasonRequest): NetworkResult<Season> = try {
        val r = api.updateSeason(id, request)
        val dto = r.data.unwrap<SeasonDto>("season")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی فصل")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun toggleStatus(id: Int, activate: Boolean): NetworkResult<Unit> = try {
        val r = if (activate) api.activateSeason(id) else api.deactivateSeason(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }
}