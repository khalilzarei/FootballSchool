package com.khz.footballschool.domain.model

data class Media(
    val id: Int,
    val fileName: String,
    val originalName: String,
    val fileType: String,
    val fileSize: Long,
    val mimeType: String,
    val url: String,
    val thumbnailUrl: String?,
    val visibility: String,
    val relatedType: String?,
    val relatedId: Int?,
    val description: String?,
    val status: String,
    val uploadedBy: Int?,
    val createdAt: String?,
    val updatedAt: String?
)