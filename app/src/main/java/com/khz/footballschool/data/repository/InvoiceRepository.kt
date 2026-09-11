package com.khz.footballschool.data.repository

import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.network.PaginatedResponse
import com.khz.footballschool.data.dto.request.*
import com.khz.footballschool.data.dto.response.InvoiceDto
import com.khz.footballschool.data.dto.response.InvoiceItemDto
import com.khz.footballschool.data.dto.response.InstallmentDto
import com.khz.footballschool.data.remote.InvoiceApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.Installment
import com.khz.footballschool.domain.model.Invoice
import com.khz.footballschool.domain.model.InvoiceItem

class InvoiceRepository(private val api: InvoiceApi) {

    suspend fun getInvoices(
        page: Int = 1,
        perPage: Int = 20,
        playerId: Int? = null,
        status: String? = null,
        invoiceType: String? = null,
        q: String? = null
    ): NetworkResult<PaginatedResponse<Invoice>> = try {
        val r = api.getInvoices(page, perPage, playerId, status, invoiceType, q)
        if (r.success && r.data != null) {
            NetworkResult.Success(
                PaginatedResponse(
                    items = r.data.items.map { it.toDomain() },
                    total = r.data.total,
                    page = r.data.page,
                    perPage = r.data.perPage
                )
            )
        } else NetworkResult.Error(r.message ?: "خطا در دریافت فاکتورها")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun createInvoice(request: CreateInvoiceRequest): NetworkResult<Invoice> = try {
        val r = api.createInvoice(request)
        val dto = r.data.unwrap<InvoiceDto>("invoice")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در ایجاد فاکتور")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getInvoice(id: Int): NetworkResult<Invoice> = try {
        val r = api.getInvoice(id)
        val dto = r.data.unwrap<InvoiceDto>("invoice")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در دریافت فاکتور")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun cancelInvoice(id: Int): NetworkResult<Unit> = try {
        val r = api.cancelInvoice(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun addItem(invoiceId: Int, request: AddInvoiceItemRequest): NetworkResult<InvoiceItem> = try {
        val r = api.addItem(invoiceId, request)
        val dto = r.data.unwrap<InvoiceItemDto>("item", "invoice_item")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در افزودن آیتم")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun updateItem(id: Int, request: UpdateInvoiceItemRequest): NetworkResult<InvoiceItem> = try {
        val r = api.updateItem(id, request)
        val dto = r.data.unwrap<InvoiceItemDto>("item", "invoice_item")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی آیتم")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun deleteItem(id: Int): NetworkResult<Unit> = try {
        val r = api.deleteItem(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun applyDiscount(invoiceId: Int, request: ApplyDiscountToInvoiceRequest): NetworkResult<Invoice> = try {
        val r = api.applyDiscount(invoiceId, request)
        val dto = r.data.unwrap<InvoiceDto>("invoice")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در اعمال تخفیف")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun addInstallment(invoiceId: Int, request: AddInstallmentRequest): NetworkResult<Installment> = try {
        val r = api.addInstallment(invoiceId, request)
        val dto = r.data.unwrap<InstallmentDto>("installment")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در افزودن قسط")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun listInstallments(invoiceId: Int): NetworkResult<List<Installment>> = try {
        val r = api.listInstallments(invoiceId)
        if (r.success && r.data != null) NetworkResult.Success(r.data.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت اقساط")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }
}