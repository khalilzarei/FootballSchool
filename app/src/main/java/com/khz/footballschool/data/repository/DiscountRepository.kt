package com.khz.footballschool.data.repository

import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.CreateDiscountRequest
import com.khz.footballschool.data.dto.request.UpdateDiscountRequest
import com.khz.footballschool.data.dto.response.DiscountDto
import com.khz.footballschool.data.remote.DiscountApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.Discount

class DiscountRepository(private val api: DiscountApi) {

    suspend fun getDiscounts(page: Int = 1, perPage: Int = 20, q: String? = null, status: String? = null): NetworkResult<List<Discount>> = try {
        val r = api.getDiscounts(page, perPage, q, status)
        if (r.success && r.data != null) NetworkResult.Success(r.data.items.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت تخفیف‌ها")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun createDiscount(request: CreateDiscountRequest): NetworkResult<Discount> = try {
        val r = api.createDiscount(request)
        val dto = r.data.unwrap<DiscountDto>("discount")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در ایجاد تخفیف")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getDiscount(id: Int): NetworkResult<Discount> = try {
        val r = api.getDiscount(id)
        val dto = r.data.unwrap<DiscountDto>("discount")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در دریافت تخفیف")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun updateDiscount(id: Int, request: UpdateDiscountRequest): NetworkResult<Discount> = try {
        val r = api.updateDiscount(id, request)
        val dto = r.data.unwrap<DiscountDto>("discount")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی تخفیف")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun toggleStatus(id: Int, activate: Boolean): NetworkResult<Unit> = try {
        val r = if (activate) api.activateDiscount(id) else api.deactivateDiscount(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }
}