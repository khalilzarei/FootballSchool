package com.khz.footballschool.domain.model

data class Season(
    val id: Int,
    val title: String,
    val startDate: String?,
    val endDate: String?,
    val ageCutoffDate: String?,
    val status: String,
    val isActive: Boolean,
    val notes: String?,
    val createdAt: String?,
    val updatedAt: String?
)