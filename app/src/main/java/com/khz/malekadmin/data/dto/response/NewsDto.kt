package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class NewsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("status") val status: String,
    @SerializedName("publish_at") val publishAt: String?,
    @SerializedName("published_at") val publishedAt: String?,
    @SerializedName("archived_at") val archivedAt: String? = null,
    @SerializedName("created_by") val createdBy: Int?,
    @SerializedName("created_by_name") val createdByName: String? = null,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("media") val media: List<MediaDto>? = null,
    @SerializedName("audiences") val audiences: List<NewsAudienceDto>? = null
)

data class NewsAudienceDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("news_id") val newsId: Int? = null,
    @SerializedName("audience_type") val audienceType: String,
    @SerializedName("role") val role: String? = null,
    @SerializedName("target_id") val targetId: Int? = null,
    @SerializedName("target_title") val targetTitle: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)
