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
    val createdByName: String? = null,
    val media: List<Media> = emptyList(),
    val audiences: List<NewsAudience> = emptyList(),
    val createdAt: String?,
    val updatedAt: String?
) {
    val isPublished: Boolean get() = status == "published"
    val isDraft: Boolean get() = status == "draft"
    val isArchived: Boolean get() = status == "archived"

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

    val audienceSummary: String?
        get() {
            if (audiences.isEmpty()) return "همه"
            if (audiences.any { it.audienceType == "global" }) return "همه"

            val parts = mutableListOf<String>()

            val roles = audiences.filter { it.audienceType == "role" }
            if (roles.isNotEmpty()) {
                parts += roles.joinToString("، ") { it.displayTitle }
            }

            val ageGroups = audiences.filter { it.audienceType == "age_group" }
            if (ageGroups.isNotEmpty()) {
                if (ageGroups.size <= 2 && ageGroups.all { !it.targetTitle.isNullOrBlank() }) {
                    parts += ageGroups.joinToString("، ") { it.displayTitle }
                } else {
                    parts += "${ageGroups.size} گروه سنی"
                }
            }

            val classes = audiences.filter { it.audienceType == "class" }
            if (classes.isNotEmpty()) {
                if (classes.size <= 2 && classes.all { !it.targetTitle.isNullOrBlank() }) {
                    parts += classes.joinToString("، ") { it.displayTitle }
                } else {
                    parts += "${classes.size} کلاس"
                }
            }

            val players = audiences.filter { it.audienceType == "player" }
            if (players.isNotEmpty()) {
                parts += "${players.size} بازیکن خاص"
            }

            return if (parts.isEmpty()) "${audiences.size} مخاطب خاص" else parts.joinToString(" · ")
        }

    /** جزئیات کامل برای نمایش در صفحه جزئیات */
    val audienceDetails: List<String>
        get() {
            if (audiences.isEmpty() || audiences.any { it.audienceType == "global" }) return listOf("همه کاربران")
            return audiences.map { it.displayTitle }
        }

    private fun roleLabel(role: String): String = when (role) {
        "admin"  -> "مدیران"
        "coach"  -> "مربیان"
        "player" -> "بازیکنان"
        else     -> role
    }
}

data class NewsAudience(
    val id: Int? = null,
    val newsId: Int? = null,
    val audienceType: String,
    val role: String? = null,
    val targetId: Int? = null,
    val targetTitle: String? = null
) {
    val displayTitle: String
        get() = when (audienceType) {
            "global"    -> "همه"
            "role"      -> when (role) {
                "player" -> "بازیکنان"
                "coach"  -> "مربیان"
                "admin"  -> "مدیران"
                else     -> role
                        ?: audienceType
            }

            "age_group" -> targetTitle
                    ?: "گروه سنی #$targetId"

            "class"     -> targetTitle
                    ?: "کلاس #$targetId"

            "player"    -> targetTitle
                    ?: "بازیکن #$targetId"

            else        -> targetTitle
                    ?: audienceType
        }
}
