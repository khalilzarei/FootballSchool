package com.khz.malekadmin.domain.model

data class FootballClass(
    val id: Int,
    val title: String,
    val seasonId: Int? = null,
    val season: Season? = null,
    val ageGroupId: Int? = null,
    val ageGroup: AgeGroup?,
    // چند گروه سنی برای هر کلاس
    val ageGroupIds: List<Int> = emptyList(),
    val ageGroups: List<AgeGroup> = emptyList(),
    val coachId: Int? = null,
    val coach: Coach?,
    val assistantCoachId: Int? = null,
    val capacity: Int?,
    val status: String,
    val isActive: Boolean,
    val location: String?,
    val description: String?,
    val pricingType: String?,
    val billingCycle: String? = null,
    val monthlyFee: Long?,
    val seasonalFee: Long? = null,
    val billingMonths: Int? = null,
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

    // عنوان همه‌ی گروه‌های سنی کلاس (مثلاً «U10، U12») — سازگاری با داده‌های قدیمی
    val ageGroupsTitle: String?
        get() {
            val titles = ageGroups.map { it.title }
                .filter { it.isNotBlank() }
            return when {
                titles.isNotEmpty()   -> titles.joinToString("، ")
                ageGroupTitle != null -> ageGroupTitle
                else                  -> null
            }
        }

    val seasonTitle: String?
        get() = season?.title

    val pricingLabel: String
        get() = when (pricingType) {
            "monthly" -> "ماهانه"
            "session" -> "جلسه‌ای"
            "both"    -> "ماهانه + جلسه‌ای"
            else      -> "-"
        }

    val billingCycleLabel: String
        get() = when (billingCycle) {
            "seasonal", "quarterly" -> "فصلی (۳ ماهه)"
            "monthly"               -> "ماهیانه"
            else                    -> "ماهیانه"
        }

    val isSeasonal: Boolean
        get() = billingCycle == "seasonal" || billingCycle == "quarterly"

    val effectiveFee: Long?
        get() = if (isSeasonal) {
            seasonalFee
                    ?: monthlyFee?.let { it * 3 }
                    ?: monthlyFee
        } else monthlyFee

    val effectiveFeeLabel: String
        get() {
            val fee = effectiveFee
                    ?: 0L
            return if (isSeasonal) "فصلی: ${fee} تومان (۳ ماه)" else "ماهیانه: ${fee} تومان"
        }
}