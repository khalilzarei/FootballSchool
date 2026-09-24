package com.khz.malekadmin.domain.model

/**
 * مخاطب قابل گفتگو (برای ادمین: بازیکنان + مربیان فعال)
 */
data class ChatContact(
    val userId: Int,
    val fullName: String?,
    val role: String?,
    val avatarUrl: String?,
    val classTitle: String?
) {
    val isPlayer: Boolean
        get() = role == "player"

    val isCoach: Boolean
        get() = role == "coach"
}
