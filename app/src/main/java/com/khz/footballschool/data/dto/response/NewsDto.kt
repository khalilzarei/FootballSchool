package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class NewsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("status") val status: String,
    @SerializedName("publish_at") val publishAt: String?,
    @SerializedName("published_at") val publishedAt: String?,
    @SerializedName("archived_at") val archivedAt: String?,
    @SerializedName("created_by") val createdBy: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)