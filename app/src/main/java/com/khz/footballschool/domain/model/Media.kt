package com.khz.footballschool.domain.model

/**
 * مدل دامنه‌ی رسانه (عکس یا فیلم).
 *
 * تمام فیلدهای اختیاری nullable هستند تا نبود یک فیلد در پاسخ سرور
 * باعث crash نشود.
 */
data class Media(
    val id: Int,
    val fileName: String?,
    val originalName: String?,
    val fileType: String,
    val fileSize: Long,
    val mimeType: String,
    val durationSeconds: Int?,
    val url: String?,
    val streamUrl: String?,
    val thumbnailUrl: String?,
    val visibility: String,
    val relatedType: String?,
    val relatedId: Int?,
    val description: String?,
    val status: String,
    val uploadedBy: Int?,
    val uploaderName: String?,
    val createdAt: String?,
    val updatedAt: String?
) {
    val isImage: Boolean get() = fileType == "image"
    val isVideo: Boolean get() = fileType == "video"

    /** نام قابل نمایش با fallback */
    val displayName: String get() = originalName ?: fileName ?: "فایل $id"

    /** حجم خوانا مثل «۲.۴ مگابایت» */
    val humanSize: String
        get() = when {
            fileSize <= 0L -> "-"
            fileSize < 1024L -> "$fileSize بایت"
            fileSize < 1024L * 1024L -> "${fileSize / 1024} کیلوبایت"
            else -> String.format("%.1f مگابایت", fileSize / (1024.0 * 1024.0))
        }

    /** مدت زمان ویدیو مثل «۱:۲۳» */
    val humanDuration: String?
        get() {
            val total = durationSeconds ?: return null
            if (total <= 0) return null
            val minutes = total / 60
            val seconds = total % 60
            return "%d:%02d".format(minutes, seconds)
        }
}
