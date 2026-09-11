package com.khz.footballschool.domain.model

data class Installment(
    val id: Int,
    val invoiceId: Int,
    val amount: Long,
    val paidAmount: Long,
    val dueDate: String?,
    val status: String,
    val notes: String?,
    val createdAt: String?,
    val updatedAt: String?
)