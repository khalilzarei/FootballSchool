package com.khz.malekadmin.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * ساعت/تاریخ سرور به‌جای ساعت گوشی.
 *
 * با هر پاسخ HTTP از هدر Date همگام می‌شود (مثل «Mon, 14 Sep 2026 06:02:45 GMT»)
 * و اختلاف ساعت سرور با گوشی را نگه می‌دارد.
 * تا پیش از اولین پاسخ، ساعت دستگاه استفاده می‌شود.
 */
object ServerTime {

    @Volatile
    private var offsetMillis: Long? = null

    /** همگام‌سازی از هدر Date پاسخ سرور */
    fun syncFromHttpDate(dateHeader: String?) {
        if (dateHeader.isNullOrBlank()) return
        try {
            val serverMillis = Instant.from(
                DateTimeFormatter.RFC_1123_DATE_TIME.parse(dateHeader)
            )
                .toEpochMilli()
            offsetMillis = serverMillis - System.currentTimeMillis()
        } catch (_: Exception) {
            // هدر نامعتبر بود — ساعت دستگاه باقی می‌ماند
        }
    }

    /** زمان فعلی بر اساس ساعت سرور (اگر همگام شده باشد) */
    fun nowMillis(): Long = System.currentTimeMillis() + (offsetMillis
            ?: 0L)

    /** تاریخ امروز بر اساس ساعت سرور و منطقه‌ی زمانی گوشی */
    fun today(): LocalDate = Instant.ofEpochMilli(nowMillis())
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}