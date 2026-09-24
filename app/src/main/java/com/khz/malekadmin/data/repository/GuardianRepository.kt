package com.khz.malekadmin.data.repository

import com.khz.malekadmin.core.network.JsonParser.unwrap
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.dto.request.AttachPlayerToGuardianRequest
import com.khz.malekadmin.data.dto.request.UpdateGuardianRequest
import com.khz.malekadmin.data.dto.response.GuardianDto
import com.khz.malekadmin.data.dto.response.GuardianPlayerDto
import com.khz.malekadmin.data.remote.GuardianApi
import com.khz.malekadmin.domain.mapper.toDomain
import com.khz.malekadmin.domain.model.Guardian
import com.khz.malekadmin.domain.model.GuardianPlayer

class GuardianRepository(private val api: GuardianApi) {

    suspend fun getGuardians(page: Int = 1, perPage: Int = 20, q: String? = null, status: String? = null): NetworkResult<List<Guardian>> = try {
        val r = api.getGuardians(page, perPage, q, status)
        if (r.success && r.data != null) NetworkResult.Success(r.data.items.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت سرپرست‌ها")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getGuardian(id: Int): NetworkResult<Guardian> = try {
        val r = api.getGuardian(id)
        val dto = r.data.unwrap<GuardianDto>("guardian")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در دریافت سرپرست")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun updateGuardian(id: Int, request: UpdateGuardianRequest): NetworkResult<Guardian> = try {
        val r = api.updateGuardian(id, request)
        val dto = r.data.unwrap<GuardianDto>("guardian")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی سرپرست")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getGuardianPlayers(id: Int): NetworkResult<List<GuardianPlayer>> = try {
        val r = api.getGuardianPlayers(id)
        if (r.success && r.data != null) NetworkResult.Success(r.data.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت بازیکنان")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun attachPlayer(id: Int, request: AttachPlayerToGuardianRequest): NetworkResult<GuardianPlayer> = try {
        val r = api.attachPlayer(id, request)
        val dto = r.data.unwrap<GuardianPlayerDto>("guardian_player", "player", "guardian")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در اتصال بازیکن")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun detachPlayer(id: Int, playerId: Int): NetworkResult<Unit> = try {
        val r = api.detachPlayer(id, playerId)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }
}