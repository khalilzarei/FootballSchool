package com.khz.footballschool.data.repository

import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.network.safeCall
import com.khz.footballschool.data.dto.request.SettingItemRequest
import com.khz.footballschool.data.dto.request.UpdateSettingsRequest
import com.khz.footballschool.data.remote.SettingApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.Setting

class SettingRepository(private val api: SettingApi) {
    suspend fun getSettings(): NetworkResult<List<Setting>> =
        safeCall({ api.getSettings() }) { it.map { d -> d.toDomain() } }

    suspend fun updateSettings(items: List<SettingItemRequest>): NetworkResult<List<Setting>> =
        safeCall({ api.updateSettings(UpdateSettingsRequest(items)) }) { it.map { d -> d.toDomain() } }
}