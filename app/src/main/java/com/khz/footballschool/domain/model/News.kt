package com.khz.footballschool.domain.model

/**
 * مدل دامنه‌ی خبر.
 *
 * `media` عکس‌ها و فیلم‌های متصل به خبر هستند (سرور از جدول football_media
 * با related_type = "news" پر می‌کند).
 */
data class News(
    val id: Int,
    val title: String,
    val body: String,
    val status: String,
    val publishAt: String?,
    val publishedAt: String?,
    val archivedAt: String?,
    val createdBy: Int?,
    val media: List<Media> = emptyList(),
    val createdAt: String?,
    val updatedAt: String?
) {
    val isPublished: Boolean get() = status == "published"

    val images: List<Media> get() = media.filter { it.isImage }

    val videos: List<Media> get() = media.filter { it.isVideo }

    /** خلاصه‌ی تعداد رسانه برای نمایش در لیست */
    val mediaSummary: String?
        get() {
            if (media.isEmpty()) return null

            val parts = mutableListOf<String>()
            if (images.isNotEmpty()) parts += "${images.size} عکس"
            if (videos.isNotEmpty()) parts += "${videos.size} فیلم"

            return parts.joinToString(" · ")
        }
}
