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
)

data class InvoiceItem(
    val id: Int,
    val invoiceId: Int,
    val title: String,
    val itemType: String,
    val amount: Long,
    val quantity: Int,
    val total: Long,
    val description: String?,
    val createdAt: String?,
    val updatedAt: String?
)