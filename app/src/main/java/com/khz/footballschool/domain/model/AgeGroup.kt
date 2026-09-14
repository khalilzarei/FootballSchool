package com.khz.footballschool.domain.model

data class AgeGroup(
    val id: Int,
    val title: String,
    val birthDateFrom: String?,
    val birthDateTo: String?,
    val minAgeAtCutoff: Int?,
    val maxAgeAtCutoff: Int?,
    val sortOrder: Int,
    val status: String,
    val isActive: Boolean,
    val playersCount: Int? = null,
    val createdAt: String?,
    val updatedAt: String?
)