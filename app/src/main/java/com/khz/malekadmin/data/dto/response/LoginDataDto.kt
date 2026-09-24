package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class LoginDataDto(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserDto
)
