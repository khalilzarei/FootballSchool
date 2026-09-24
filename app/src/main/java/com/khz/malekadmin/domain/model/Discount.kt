package com.khz.malekadmin.domain.model

data class Discount(
    val id: Int,
    val title: String,
    val discountType: String,
    val value: Double,
    val appliesTo: String,
    val autoApply: Boolean,
    val startDate: String?,
    val endDate: String?,
    val status: String,
    val isActive: Boolean,
    val description: String?,
    val createdAt: String?,
    val updatedAt: String?
)