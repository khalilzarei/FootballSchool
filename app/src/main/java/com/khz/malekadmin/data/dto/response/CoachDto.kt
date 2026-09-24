package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class CoachDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user") val user: UserDto,
    @SerializedName("specialty") val specialty: String?,
    @SerializedName("license_level") val licenseLevel: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("classes") val classes: List<ClassDto> = emptyList()
)