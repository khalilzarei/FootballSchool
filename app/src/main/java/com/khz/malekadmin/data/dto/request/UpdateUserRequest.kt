package com.khz.malekadmin.data.dto.request

import com.google.gson.annotations.SerializedName

data class UpdateUserRequest(
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("national_code") val nationalCode: String?,
    @SerializedName("status") val status: String?
)