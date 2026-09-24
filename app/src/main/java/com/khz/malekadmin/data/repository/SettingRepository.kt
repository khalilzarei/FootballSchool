package com.khz.malekadmin.data.repository

import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.core.network.safeCall
import com.khz.malekadmin.data.dto.request.SettingItemRequest
import com.khz.malekadmin.data.dto.request.UpdateSettingsRequest
import com.khz.malekadmin.data.remote.SettingApi
import com.khz.malekadmin.domain.mapper.toDomain
import com.khz.malekadmin.domain.model.Setting

class SettingRepository(private val api: SettingApi) {
    suspend fun getSettings(): NetworkResult<List<Setting>> =
        safeCall({ api.getSettings() }) { it.map { d -> d.toDomain() } }

    suspend fun updateSettings(items: List<SettingItemRequest>): NetworkResult<List<Setting>> =
        safeCall({ api.updateSettings(UpdateSettingsRequest(items)) }) { it.map { d -> d.toDomain() } }
}