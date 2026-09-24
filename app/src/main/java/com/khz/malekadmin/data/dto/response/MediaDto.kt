package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

data class MediaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("file_name") val fileName: String?,
    @SerializedName("original_name") val originalName: String?,
    @SerializedName("file_type") val fileType: String?,
    @SerializedName("file_size") val fileSize: Long? = 0L,
    @SerializedName("mime_type") val mimeType: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("stream_url") val streamUrl: String?,
    @SerializedName("thumbnail_url") val thumbnailUrl: String?,
    @SerializedName("duration_seconds") val durationSeconds: Int? = null,
    @SerializedName("visibility") val visibility: String?,
    @SerializedName("related_type") val relatedType: String?,
    @SerializedName("related_id") val relatedId: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("uploaded_by") val uploadedBy: Int?,
    @SerializedName("uploader_name") val uploaderName: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)
