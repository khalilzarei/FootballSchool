package com.khz.footballschool.domain.model

data class Invoice(
    val id: Int,
    val playerId: Int,
    val player: Player?,
    val invoiceType: String,
    val periodStartDate: String?,
    val periodEndDate: String?,
    val dueDate: String?,
    val status: String,
    val totalAmount: Long,
    val paidAmount: Long,
    val remainingAmount: Long,
    val notes: String?,
    val items: List<InvoiceItem> = emptyList(),
    val discounts: List<Discount> = emptyList(),
    val installments: List<Installment> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val createdAt: String?,
    val updatedAt: String?
) {
    val isDebtor: Boolean get() = remainingAmount > 0
    val classDebts: List<ClassDebt>
        get() {
            val map = mutableMapOf<String, ClassDebt>()
            items.forEach { item ->
                val key = item.classId?.toString()
                        ?: "other"
                val existing = map[key]
                if (existing == null) {
                    map[key] = ClassDebt(
                        classId = item.classId,
                        classTitle = item.classTitle
                                ?: if (item.classId == null) "سایر هزینه‌ها" else "کلاس #${item.classId}",
                        ageGroupTitle = item.ageGroupTitle,
                        total = item.total,
                        itemsCount = 1
                    )
                } else {
                    map[key] = existing.copy(
                        total = existing.total + item.total,
                        itemsCount = existing.itemsCount + 1
                    )
                }
            }
            return map.values.toList()
        }
}

data class ClassDebt(
    val classId: Int?,
    val classTitle: String,
    val ageGroupTitle: String? = null,
    val total: Long,
    val paid: Long = 0,
    val remaining: Long = 0,
    val itemsCount: Int = 0
)

data class InvoiceItem(
    val id: Int,
    val invoiceId: Int,
    val title: String,
    val itemType: String,
    val amount: Long,
    val quantity: Int,
    val total: Long,
    val classId: Int? = null,
    val classTitle: String? = null,
    val ageGroupTitle: String? = null,
    val sessionId: Int? = null,
    val description: String?,
    val createdAt: String?,
    val updatedAt: String?
)
