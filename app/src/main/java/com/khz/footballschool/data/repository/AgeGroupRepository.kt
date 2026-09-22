package com.khz.footballschool.data.repository

import com.khz.footballschool.core.network.ApiErrorHandler
import com.khz.footballschool.core.network.JsonParser.parseField
import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.JsonParser.unwrapList
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.CreateAgeGroupRequest
import com.khz.footballschool.data.dto.request.UpdateAgeGroupRequest
import com.khz.footballschool.data.dto.response.AgeGroupDto
import com.khz.footballschool.data.dto.response.PlayerDto
import com.khz.footballschool.data.remote.AgeGroupApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.AgeGroup
import com.khz.footballschool.domain.model.Player
import com.google.gson.JsonElement

class AgeGroupRepository(private val api: AgeGroupApi) {

    suspend fun getAgeGroups(
        page: Int = 1,
        perPage: Int = 20,
        seasonId: Int? = null,
        q: String? = null,
        status: String? = null
    ): NetworkResult<List<AgeGroup>> = try {
        val r = api.getAgeGroups(
            page,
            perPage,
            seasonId,
            q,
            status
        )
        if (r.success && r.data != null) NetworkResult.Success(r.data.items.map { it.toDomain() })
        else NetworkResult.Error(
            r.message
                    ?: "خطا در دریافت گروه‌های سنی"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun createAgeGroup(request: CreateAgeGroupRequest): NetworkResult<AgeGroup> = try {
        val r = api.createAgeGroup(request)
        val dto = r.data.unwrap<AgeGroupDto>("age_group")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(
            r.message
                    ?: "خطا در ایجاد گروه سنی"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun getAgeGroup(id: Int): NetworkResult<AgeGroup> = try {
        val r = api.getAgeGroup(id)
        val dto = r.data.unwrap<AgeGroupDto>("age_group")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(
            r.message
                    ?: "خطا در دریافت گروه سنی"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun updateAgeGroup(
        id: Int,
        request: UpdateAgeGroupRequest
    ): NetworkResult<AgeGroup> = try {
        val r = api.updateAgeGroup(
            id,
            request
        )
        val dto = r.data.unwrap<AgeGroupDto>("age_group")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(
            r.message
                    ?: "خطا در به‌روزرسانی گروه سنی"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    suspend fun toggleStatus(
        id: Int,
        activate: Boolean
    ): NetworkResult<Unit> = try {
        val r = if (activate) api.activateAgeGroup(id) else api.deactivateAgeGroup(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(
            r.message
                    ?: "خطا"
        )
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }

    /**
     * بازیکنان عضو گروه سنی — هر بازیکنی که تاریخ تولدش در بازه گروه باشد
     */
    suspend fun getPlayers(ageGroupId: Int): NetworkResult<List<Player>> = try {
        val r = api.getAgeGroupPlayers(ageGroupId)
        if (r.success && r.data != null) {
            val players = parseField<JsonElement>(
                r.data,
                "players"
            )?.unwrapList<PlayerDto>()
                ?.map { it.toDomain() }
                    ?: emptyList()
            NetworkResult.Success(players)
        } else {
            NetworkResult.Error(
                r.message
                        ?: "خطا در دریافت بازیکنان گروه سنی"
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error(ApiErrorHandler.extractMessage(e))
    }
}