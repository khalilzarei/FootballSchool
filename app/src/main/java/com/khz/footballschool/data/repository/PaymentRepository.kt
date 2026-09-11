package com.khz.footballschool.data.repository

import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.CreatePaymentRequest
import com.khz.footballschool.data.dto.response.PaymentDto
import com.khz.footballschool.data.remote.PaymentApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.Payment

class PaymentRepository(private val api: PaymentApi) {

    suspend fun getPayments(page: Int = 1, perPage: Int = 20, playerId: Int? = null, invoiceId: Int? = null, status: String? = null): NetworkResult<List<Payment>> = try {
        val r = api.getPayments(page, perPage, playerId, invoiceId, status)
        if (r.success && r.data != null) NetworkResult.Success(r.data.items.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت پرداخت‌ها")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun createPayment(request: CreatePaymentRequest): NetworkResult<Payment> = try {
        val r = api.createPayment(request)
        val dto = r.data.unwrap<PaymentDto>("payment")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در ثبت پرداخت")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getPayment(id: Int): NetworkResult<Payment> = try {
        val r = api.getPayment(id)
        val dto = r.data.unwrap<PaymentDto>("payment")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در دریافت پرداخت")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun approvePayment(id: Int): NetworkResult<Unit> = try {
        val r = api.approvePayment(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun rejectPayment(id: Int): NetworkResult<Unit> = try {
        val r = api.rejectPayment(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }
}