package com.khz.footballschool.core.util

object Constants {
    const val BASE_URL = "https://football.madahinote.ir/api/v1/"
    const val STORAGE_URL = "https://football.madahinote.ir/"
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // User Roles
    const val ROLE_ADMIN = "admin"
    const val ROLE_COACH = "coach"
    const val ROLE_GUARDIAN = "guardian"

    // Status
    const val STATUS_ACTIVE = "active"
    const val STATUS_INACTIVE = "inactive"
    const val STATUS_PENDING = "pending"
    const val STATUS_CANCELLED = "cancelled"
    const val STATUS_COMPLETED = "completed"
}