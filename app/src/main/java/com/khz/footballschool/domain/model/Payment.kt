package com.khz.footballschool.domain.model

data class Payment(
    val id: Int,
    val playerId: Int,
    val player: Player?,
    val invoiceId: Int?,
    val invoice: Invoice?,
    val installmentId: Int?,
    val amount: Long,
    val paymentMethod: String,
    val status: String,
    val receiptMediaId: Int?,
    val notes: String?,
    val approvedAt: String?,
    val rejectedAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)