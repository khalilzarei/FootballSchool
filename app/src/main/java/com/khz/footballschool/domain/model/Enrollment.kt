package com.khz.footballschool.domain.model

data class Enrollment(
    val id: Int,
    val classId: Int,
    val classItem: FootballClass? = null,
    val playerId: Int,
    val player: Player? = null,
    val status: String,
    val isActive: Boolean,
    val enrolledAt: String?,
    val endedAt: String?,
    val monthlyFeeOverride: Long?,
    val sessionFeeOverride: Long?,
    val registrationFeeOverride: Long?,
    val notes: String?,
    val createdAt: String?,
    val updatedAt: String?
) {
    val playerFullName: String
        get() = player?.fullName
                ?: "-"

    val statusLabel: String
        get() = when (status) {
            "active"    -> "فعال"
            "pending"   -> "در انتظار"
            "waitlist"  -> "لیست انتظار"
            "completed" -> "پایان‌یافته"
            "inactive"  -> "غیرفعال"
            else        -> status
        }
}
