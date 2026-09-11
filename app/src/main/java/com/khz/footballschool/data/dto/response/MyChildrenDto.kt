package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class MyChildrenDto(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("birth_date") val birthDate: String?,
    @SerializedName("age") val age: Int?,
    @SerializedName("current_class") val currentClass: ClassDto?,
    @SerializedName("balance") val balance: PlayerBalanceDto?
)