package com.khz.malekadmin.data.repository

import com.khz.malekadmin.core.network.JsonParser.unwrap
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.dto.request.SetMediaAudiencesRequest
import com.khz.malekadmin.data.dto.response.MediaDto
import com.khz.malekadmin.data.remote.MediaApi
import com.khz.malekadmin.domain.mapper.toDomain
import com.khz.malekadmin.domain.model.Media
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MediaRepository(private val api: MediaApi) {

    suspend fun getMedia(page: Int = 1, perPage: Int = 20, status: String? = null, visibility: String? = null, fileType: String? = null, q: String? = null): NetworkResult<List<Media>> = try {
        val r = api.getMedia(page, perPage, status, visibility, fileType, null, null, q)
        if (r.success && r.data != null) NetworkResult.Success(r.data.items.map { it.toDomain() })
        else NetworkResult.Error(r.message ?: "خطا در دریافت رسانه‌ها")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun uploadMedia(file: MultipartBody.Part, visibility: RequestBody, relatedType: RequestBody, relatedId: RequestBody?, description: RequestBody?): NetworkResult<Media> = try {
        val r = api.uploadMedia(file, visibility, relatedType, relatedId, description)
        val dto = r.data.unwrap<MediaDto>("media")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در آپلود رسانه")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun getMedia(id: Int): NetworkResult<Media> = try {
        val r = api.getMedia(id)
        val dto = r.data.unwrap<MediaDto>("media")
        if (r.success && dto != null) NetworkResult.Success(dto.toDomain())
        else NetworkResult.Error(r.message ?: "خطا در دریافت رسانه")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun setAudiences(id: Int, request: SetMediaAudiencesRequest): NetworkResult<Unit> = try {
        val r = api.setAudiences(id, request)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }

    suspend fun deleteMedia(id: Int): NetworkResult<Unit> = try {
        val r = api.deleteMedia(id)
        if (r.success) NetworkResult.Success(Unit) else NetworkResult.Error(r.message ?: "خطا")
    } catch (e: Exception) { NetworkResult.Error(e.message ?: "خطای شبکه") }
}