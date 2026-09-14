package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

data class CreateSeasonRequest(
    @SerializedName("title") val title: String,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("age_cutoff_date") val ageCutoffDate: String?,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String?
)

data class UpdateSeasonRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("start_date") val startDate: String?,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("age_cutoff_date") val ageCutoffDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("notes") val notes: String?
)

data class CreateAgeGroupRequest(
    @SerializedName("title") val title: String,
    @SerializedName("birth_date_from") val birthDateFrom: String?,
    @SerializedName("birth_date_to") val birthDateTo: String?,
    @SerializedName("min_age_at_cutoff") val minAgeAtCutoff: Int?,
    @SerializedName("max_age_at_cutoff") val maxAgeAtCutoff: Int?,
    @SerializedName("sort_order") val sortOrder: Int,
    @SerializedName("status") val status: String
)

data class UpdateAgeGroupRequest(
    @SerializedName("title") val title: String?,
    @SerializedName("birth_date_from") val birthDateFrom: String?,
    @SerializedName("birth_date_to") val birthDateTo: String?,
    @SerializedName("min_age_at_cutoff") val minAgeAtCutoff: Int?,
    @SerializedName("max_age_at_cutoff") val maxAgeAtCutoff: Int?,
    @SerializedName("sort_order") val sortOrder: Int?,
    @SerializedName("status") val status: String?
)

data class UpdateCoachRequest(
    @SerializedName("specialty") val specialty: String?,
    @SerializedName("license_level") val licenseLevel: String?,
    @SerializedName("bio") val bio: String?
)