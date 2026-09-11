package com.khz.footballschool.domain.model

data class News(
    val id: Int,
    val title: String,
    val body: String,
    val status: String,
    val publishAt: String?,
    val publishedAt: String?,
    val archivedAt: String?,
    val createdBy: Int?,
    val createdAt: String?,
    val updatedAt: String?
)