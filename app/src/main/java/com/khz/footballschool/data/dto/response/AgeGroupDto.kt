package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class AgeGroupDto(
    @SerializedName("id") val id: Int,
    @SerializedName("season_id") val seasonId: Int,
    @SerializedName("season") val season: SeasonDto?,
    @SerializedName("title") val title: String,
    @SerializedName("birth_date_from") val birthDateFrom: String?,
    @SerializedName("birth_date_to") val birthDateTo: String?,
    @SerializedName("min_age_at_cutoff") val minAgeAtCutoff: Int?,
    @SerializedName("max_age_at_cutoff") val maxAgeAtCutoff: Int?,
    @SerializedName("sort_order") val sortOrder: Int,
    @SerializedName("status") val status: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)