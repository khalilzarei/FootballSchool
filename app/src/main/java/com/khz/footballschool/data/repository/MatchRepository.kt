package com.khz.footballschool.data.repository

import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.*
import com.khz.footballschool.data.dto.response.MatchDto
import com.khz.footballschool.data.dto.response.MatchPlayerDto
import com.khz.footballschool.data.remote.MatchApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.Match
import com.khz.footballschool.domain.model.MatchPlayer

class MatchRepository(private val api: MatchApi) {

    suspend fun getMatches(page: Int = 1, perPage: Int = 20, q: String? = null, status: String? = null, classId: Int? = null, ageGroupId: Int? = null): NetworkResult<List<Match>> = try {
        val r = api.getMatches(page, perPage, q, status, classId, ageGroupId, null, null)
        if (r.success && r.data != null) NetworkResult.Success(r.data.items.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت مسابقات")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun createMatch(request: CreateMatchRequest): NetworkResult<Match> = try {
        val r = api.createMatch(request)
        val dto = r.data.unwrap<MatchDto>("match")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در ایجاد مسابقه")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getMatch(id: Int): NetworkResult<Match> = try {
        val r = api.getMatch(id)
        val dto = r.data.unwrap<MatchDto>("match")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در دریافت مسابقه")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun updateMatch(id: Int, request: UpdateMatchRequest): NetworkResult<Match> = try {
        val r = api.updateMatch(id, request)
        val dto = r.data.unwrap<MatchDto>("match")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی مسابقه")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun cancelMatch(id: Int): NetworkResult<Unit> = try {
        val r = api.cancelMatch(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun setResult(id: Int, request: SetMatchResultRequest): NetworkResult<Match> = try {
        val r = api.setResult(id, request)
        val dto = r.data.unwrap<MatchDto>("match")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در ثبت نتیجه")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun addPlayer(id: Int, request: AddMatchPlayerRequest): NetworkResult<MatchPlayer> = try {
        val r = api.addPlayer(id, request)
        val dto = r.data.unwrap<MatchPlayerDto>("match_player")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در افزودن بازیکن")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun updatePlayer(id: Int, request: UpdateMatchPlayerRequest): NetworkResult<MatchPlayer> = try {
        val r = api.updatePlayer(id, request)
        val dto = r.data.unwrap<MatchPlayerDto>("match_player")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی بازیکن مسابقه")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun removePlayer(id: Int): NetworkResult<Unit> = try {
        val r = api.removePlayer(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }
}