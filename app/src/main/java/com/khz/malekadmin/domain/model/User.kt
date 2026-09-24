package com.khz.malekadmin.domain.model

data class User(
    val id: Int,
    val fullName: String,
    val mobile: String?,
    val nationalCode: String?,
    val role: String,
    val status: String,
    val mustChangePassword: Boolean,
    val failedLoginCount: Int,
    val lockedUntil: String?,
    val lastLoginAt: String?,
    val createdBy: Int?,
    val createdAt: String?,
    val avatarUrl: String?,
    val updatedAt: String?
)