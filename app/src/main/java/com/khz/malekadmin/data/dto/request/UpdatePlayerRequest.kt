package com.khz.malekadmin.data.dto.request

import com.google.gson.annotations.SerializedName

data class UpdatePlayerRequest(
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("national_code") val nationalCode: String?,
    @SerializedName("mobile") val mobile: String? = null,
    @SerializedName("birth_date") val birthDate: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("medical_notes") val medicalNotes: String?,
    @SerializedName("notes") val notes: String?
)