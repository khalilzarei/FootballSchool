package com.khz.malekadmin.domain.model

data class PlayerBalance(
    val playerId: Int,
    val totalInvoiced: Long = 0,
    val totalPaid: Long = 0,
    val balance: Long = 0,
    val debt: Long = 0,
    val pendingPayments: Long = 0,
    val pendingCount: Int = 0,
    val pendingAmount: Long = 0,
    val isDebtor: Boolean = false,
    val openInvoices: Int = 0,
    val classDebts: List<PlayerClassDebt> = emptyList(),
    val classFees: List<PlayerClassFee> = emptyList()
)

data class PlayerClassDebt(
    val classId: Int?,
    val classTitle: String,
    val ageGroupTitle: String? = null,
    val total: Long = 0,
    val paid: Long = 0,
    val remaining: Long = 0,
    val itemsCount: Int = 0
)

data class PlayerClassFee(
    val classId: Int?,
    val classTitle: String,
    val ageGroupTitle: String? = null,
    val monthlyFee: Long? = null,
    val seasonalFee: Long? = null,
    val billingCycle: String? = null,
    val billingMonths: Int? = null,
    val sessionFee: Long? = null,
    val registrationFee: Long? = null,
    val debt: Long = 0
) {
    val isSeasonal: Boolean get() = billingCycle == "seasonal" || billingCycle == "quarterly"
    val effectiveFee: Long?
        get() = if (isSeasonal) seasonalFee
                ?: monthlyFee?.let { it * 3 } else monthlyFee
    val cycleLabel: String get() = if (isSeasonal) "فصلی (۳ ماهه)" else "ماهیانه"
}
