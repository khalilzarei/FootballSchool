package com.khz.footballschool.core.util

import java.time.LocalDate
import java.time.Period

object DateUtils {

    /**
     * تبدیل تاریخ شمسی به میلادی (فرمت YYYY-MM-DD)
     */
    fun jalaliToGregorian(jYear: Int, jMonth: Int, jDay: Int): String {
        var jy = jYear - 979
        var jm = jMonth - 1
        var jd = jDay - 1
        var jDayNo = 365 * jy + (jy / 33) * 8 + ((jy % 33) + 3) / 4
        for (i in 0 until jm) jDayNo += if (i < 6) 31 else 30
        jDayNo += jd
        var gDayNo = jDayNo + 79
        var gy = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097
        var leap = true
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524)
            gDayNo %= 36524
            if (gDayNo >= 365) gDayNo++ else leap = false
        }
        gy += 4 * (gDayNo / 1461)
        gDayNo %= 1461
        if (gDayNo >= 366) {
            leap = false
            gDayNo--
            gy += gDayNo / 365
            gDayNo %= 365
        }
        val gDays = arrayOf(31, if (leap) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gm = 0
        while (gDayNo >= gDays[gm]) {
            gDayNo -= gDays[gm]
            gm++
        }
        return String.format("%04d-%02d-%02d", gy, gm + 1, gDayNo + 1)
    }

    /**
     * محاسبه سن دقیق بر اساس تاریخ میلادی (فرمت YYYY-MM-DD)
     * خروجی: "X سال و Y ماه" یا "Z روز"
     */
    fun calculateAgeFromGregorian(gregorianDateStr: String): String {
        return try {
            val parts = gregorianDateStr.split("-")
            val birthDate = LocalDate.of(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            val today = LocalDate.now()
            val period = Period.between(birthDate, today)
            
            buildString {
                if (period.years > 0) append("${period.years} سال")
                if (period.months > 0) {
                    if (isNotEmpty()) append(" و ")
                    append("${period.months} ماه")
                }
                // نمایش روز فقط اگر سن کمتر از یک سال است (برای تمیزی UI)
                if (period.days > 0 && period.years == 0) {
                    if (isNotEmpty()) append(" و ")
                    append("${period.days} روز")
                }
            }.ifEmpty { "نوزاد" }
        } catch (e: Exception) {
            "-"
        }
    }
}