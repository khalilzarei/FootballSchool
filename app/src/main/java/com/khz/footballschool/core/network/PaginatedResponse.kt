package com.khz.footballschool.core.network

import com.google.gson.annotations.SerializedName

data class PaginatedResponse<T>(
    @SerializedName("items") val items: List<T>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int
)