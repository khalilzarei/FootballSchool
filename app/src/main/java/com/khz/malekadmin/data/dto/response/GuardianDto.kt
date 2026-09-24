package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class GuardianDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("user") val user: UserDto? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("emergency_phone") val emergencyPhone: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("players") val players: List<GuardianPlayerDto>? = null
)