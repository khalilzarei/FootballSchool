package com.khz.footballschool.domain.model

data class Setting(
    val id: Int,
    val key: String,
    val value: String,
    val valueType: String,
    val description: String?,
    val updatedAt: String?
)