package com.khz.malekadmin.data.repository

import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.core.network.safeCall
import com.khz.malekadmin.data.remote.ClientApi
import com.khz.malekadmin.domain.mapper.toDomain
import com.khz.malekadmin.domain.model.Media
import com.khz.malekadmin.domain.model.MyChild
import com.khz.malekadmin.domain.model.MyFinance
import com.khz.malekadmin.domain.model.MyScheduleItem
import com.khz.malekadmin.domain.model.News

class ClientRepository(private val api: ClientApi) {

    suspend fun getMyChildren(): NetworkResult<List<MyChild>> = safeCall({ api.getMyChildren() }) {
        it.children.orEmpty()
            .map { d -> d.toDomain() }
    }

    suspend fun getMySchedule(): NetworkResult<List<MyScheduleItem>> = safeCall({ api.getMySchedule() }) {
        it.sessions.orEmpty()
            .map { d -> d.toDomain() }
    }

    suspend fun getMyNews(): NetworkResult<List<News>> = safeCall({ api.getMyNews() }) {
        it.news.orEmpty()
            .map { d -> d.toDomain() }
    }

    suspend fun getMyMedia(): NetworkResult<List<Media>> = safeCall({ api.getMyMedia() }) {
        it.media.orEmpty()
            .map { d -> d.toDomain() }
    }

    suspend fun getMyFinance(): NetworkResult<List<MyFinance>> = safeCall({ api.getMyFinance() }) {
        it.finance.orEmpty()
            .map { d -> d.toDomain() }
    }
}