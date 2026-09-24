package com.khz.malekadmin.domain.model

/**
 * مدل کامل فاکتور - هماهنگ با بک‌اند جدید
 * بک‌اند: subtotal, discount_total, paid_total, remaining_total, invoice_number, class_debts با paid/remaining
 */
data class Invoice(
    val id: Int,
    val invoiceNumber: String,
    val playerId: Int,
    val player: Player?,
    val invoiceType: String,
    val periodStartDate: String?,
    val periodEndDate: String?,
    val dueDate: String?,
    val status: String,
    val subtotal: Long,
    val discountTotal: Long,
    val totalAmount: Long,      // برای سازگاری: همان subtotal
    val paidAmount: Long,
    val remainingAmount: Long,
    val notes: String?,
    val isDebtor: Boolean,
    val items: List<InvoiceItem> = emptyList(),
    val discounts: List<Discount> = emptyList(),
    val installments: List<Installment> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val classDebts: List<ClassDebt> = emptyList(),
    val createdAt: String?,
    val updatedAt: String?
) {
    // مبلغ قابل پرداخت پس از تخفیف
    val payableAmount: Long get() = (subtotal - discountTotal).coerceAtLeast(0)

    // آیا سررسید گذشته؟
    val isOverdue: Boolean get() = status == "overdue"

    // برچسب فارسی وضعیت
    val statusLabel: String
        get() = when (status) {
            "paid"      -> "تسویه شده"
            "partial"   -> "نیمه پرداخت"
            "open"      -> "بدهکار"
            "overdue"   -> "سررسید گذشته"
            "cancelled" -> "لغو شده"
            "draft"     -> "پیش‌نویس"
            else        -> status
        }

    val typeLabel: String
        get() = when (invoiceType) {
            "monthly"      -> "شهریه ماهانه"
            "session"      -> "شهریه جلسه‌ای"
            "registration" -> "حق ثبت‌نام"
            "manual"       -> "دستی"
            "match"        -> "مسابقه"
            else           -> invoiceType
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
