package com.khz.footballschool.data.repository

import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.network.safeCall
import com.khz.footballschool.data.remote.ClientApi
import com.khz.footballschool.domain.mapper.toDomain
import com.khz.footballschool.domain.model.Media
import com.khz.footballschool.domain.model.MyChild
import com.khz.footballschool.domain.model.MyFinance
import com.khz.footballschool.domain.model.MyScheduleItem
import com.khz.footballschool.domain.model.News

class ClientRepository(private val api: ClientApi) {

    suspend fun getMyChildren(): NetworkResult<List<MyChild>> =
        safeCall({ api.getMyChildren() }) { it.map { d -> d.toDomain() } }

    suspend fun getMySchedule(): NetworkResult<List<MyScheduleItem>> =
        safeCall({ api.getMySchedule() }) { it.map { d -> d.toDomain() } }

    suspend fun getMyNews(): NetworkResult<List<News>> =
        safeCall({ api.getMyNews() }) { it.map { d -> d.toDomain() } }

    suspend fun getMyMedia(): NetworkResult<List<Media>> =
        safeCall({ api.getMyMedia() }) { it.map { d -> d.toDomain() } }

    suspend fun getMyFinance(): NetworkResult<List<MyFinance>> =
        safeCall({ api.getMyFinance() }) { it.map { d -> d.toDomain() } }
}