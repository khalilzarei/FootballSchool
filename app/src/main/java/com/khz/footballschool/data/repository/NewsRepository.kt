package com.khz.footballschool.data.repository

import com.khz.footballschool.core.network.JsonParser.unwrap
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.CreateNewsRequest
import com.khz.footballschool.data.dto.request.SetNewsAudiencesRequest
import com.khz.footballschool.data.dto.request.UpdateNewsRequest
import com.khz.footballschool.data.dto.response.NewsDto
import com.khz.footballschool.data.remote.NewsApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.News

class NewsRepository(private val api: NewsApi) {

    suspend fun getNews(page: Int = 1, perPage: Int = 20, q: String? = null, status: String? = null): NetworkResult<List<News>> = try {
        val r = api.getNews(page, perPage, q, status)
        if (r.success && r.data != null) NetworkResult.Success(r.data.items.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت اخبار")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun createNews(request: CreateNewsRequest): NetworkResult<News> = try {
        val r = api.createNews(request)
        val dto = r.data.unwrap<NewsDto>("news")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در ایجاد خبر")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getNews(id: Int): NetworkResult<News> = try {
        val r = api.getNews(id)
        val dto = r.data.unwrap<NewsDto>("news")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در دریافت خبر")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun updateNews(id: Int, request: UpdateNewsRequest): NetworkResult<News> = try {
        val r = api.updateNews(id, request)
        val dto = r.data.unwrap<NewsDto>("news")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در به‌روزرسانی خبر")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun publishNews(id: Int): NetworkResult<Unit> = try {
        val r = api.publishNews(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun archiveNews(id: Int): NetworkResult<Unit> = try {
        val r = api.archiveNews(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun setAudiences(id: Int, request: SetNewsAudiencesRequest): NetworkResult<Unit> = try {
        val r = api.setAudiences(id, request)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun deleteNews(id: Int): NetworkResult<Unit> = try {
        val r = api.deleteNews(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }
}