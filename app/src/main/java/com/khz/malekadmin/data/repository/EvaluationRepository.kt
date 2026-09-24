package com.khz.malekadmin.data.repository

import com.khz.malekadmin.core.network.JsonParser.unwrap
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.dto.request.CreateEvaluationRequest
import com.khz.malekadmin.data.dto.request.UpdateEvaluationRequest
import com.khz.malekadmin.data.dto.response.EvaluationDto
import com.khz.malekadmin.data.remote.EvaluationApi
import com.khz.malekadmin.domain.mapper.toDomain
import com.khz.malekadmin.domain.model.Evaluation

class EvaluationRepository(private val api: EvaluationApi) {

    suspend fun getEvaluations(page: Int = 1, perPage: Int = 20, playerId: Int? = null, sessionId: Int? = null, coachId: Int? = null): NetworkResult<List<Evaluation>> = try {
        val r = api.getEvaluations(page, perPage, playerId, sessionId, coachId)
        if (r.success && r.data != null) NetworkResult.Success(r.data.items.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت ارزیابی‌ها")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun createEvaluation(request: CreateEvaluationRequest): NetworkResult<Evaluation> = try {
        val r = api.createEvaluation(request)
        val dto = r.data.unwrap<EvaluationDto>("evaluation")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در ایجاد ارزیابی")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getEvaluation(id: Int): NetworkResult<Evaluation> = try {
        val r = api.getEvaluation(id)
        val dto = r.data.unwrap<EvaluationDto>("evaluation")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در دریافت ارزیابی")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun updateEvaluation(id: Int, request: UpdateEvaluationRequest): NetworkResult<Evaluation> = try {
        val r = api.updateEvaluation(id, request)
        val dto = r.data.unwrap<EvaluationDto>("evaluation")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی ارزیابی")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getSessionEvaluations(sessionId: Int): NetworkResult<List<Evaluation>> = try {
        val r = api.getSessionEvaluations(sessionId)
        if (r.success && r.data != null) NetworkResult.Success(r.data.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت ارزیابی‌های جلسه")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }
}