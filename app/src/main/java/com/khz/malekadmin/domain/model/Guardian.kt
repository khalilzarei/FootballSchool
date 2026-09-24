package com.khz.malekadmin.domain.model

data class Guardian(
    val id: Int,
    val userId: Int,
    val user: User?,
    val address: String?,
    val emergencyPhone: String?,
    val notes: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val players: List<GuardianPlayer>
) {
    /** نام نمایشی سرپرست (از کاربر مرتبط یا fallback) */
    val displayName: String
        get() = user?.fullName
                ?: "سرپرست #$id"

    /** موبایل نمایشی سرپرست */
    val displayMobile: String
        get() = user?.mobile
                ?: "-"

    /** نقش کاربر مرتبط */
    val displayRole: String
        get() = user?.role
                ?: "guardian"
}