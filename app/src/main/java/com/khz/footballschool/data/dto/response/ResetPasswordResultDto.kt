package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class ResetPasswordResultDto(
    @SerializedName("password") val password: String
)