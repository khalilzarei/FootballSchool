package com.khz.footballschool.domain.model

data class FootballClass(
    val id: Int,
    val title: String,
    val seasonId: Int? = null,
    val season: Season? = null,
    val ageGroupId: Int? = null,
    val ageGroup: AgeGroup?,
    val coachId: Int? = null,
    val coach: Coach?,
    val assistantCoachId: Int? = null,
    val capacity: Int?,
    val status: String,
    val isActive: Boolean,
    val location: String?,
    val description: String?,
    val pricingType: String?,
    val monthlyFee: Long?,
    val sessionFee: Long?,
    val registrationFee: Long?,
    val startDate: String?,
    val endDate: String?,
    val enrolledCount: Int,
    val schedules: List<ClassSchedule>
) {
    val isFull: Boolean
        get() = capacity != null && enrolledCount >= capacity

    val fillPercent: Float
        get() = if (capacity != null && capacity > 0) {
            (enrolledCount.toFloat() / capacity.toFloat()).coerceIn(
                0f,
                1f
            )
        } else 0f

    val coachName: String?
        get() = coach?.user?.fullName

    val ageGroupTitle: String?
        get() = ageGroup?.title

    val seasonTitle: String?
        get() = season?.title

    val pricingLabel: String
        get() = when (pricingType) {
            "monthly" -> "ماهانه"
            "session" -> "جلسه‌ای"
            "both"    -> "ماهانه + جلسه‌ای"
            else      -> "-"
        }
}