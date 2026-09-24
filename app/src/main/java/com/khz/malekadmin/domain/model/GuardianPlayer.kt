package com.khz.malekadmin.domain.model

data class GuardianPlayer(
    val id: Int,
    val guardianId: Int,
    val playerId: Int,
    val relation: String,
    val isPrimary: Boolean,
    val canViewReports: Boolean,
    val canPay: Boolean,
    val guardian: Guardian?,
    val player: Player?
) {
    /** نام سرپرست این رابطه (برای نمایش در صفحه جزئیات بازیکن) */
    val guardianName: String
        get() = guardian?.displayName ?: "سرپرست #$guardianId"

    /** نام بازیکن این رابطه (برای نمایش در صفحه جزئیات سرپرست) */
    val playerName: String
        get() = player?.fullName ?: "بازیکن #$playerId"

    /** برچسب فارسی نسبت */
    val relationLabel: String
        get() = when (relation) {
            "father" -> "پدر"
            "mother" -> "مادر"
            "grandfather" -> "پدربزرگ"
            "grandmother" -> "مادربزرگ"
            "uncle" -> "عمو/دایی"
            "aunt" -> "عمه/خاله"
            "other" -> "سایر"
            else -> relation.ifBlank { "-" }
        }
}