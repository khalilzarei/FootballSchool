package com.khz.malekadmin.core.util

import java.time.LocalDate
import java.time.Period
import java.util.Locale

object DateUtils {

    // ارقام فارسی — فقط برای نمایش؛ هر مقداری که به سرور می‌رود باید لاتین باشد
    private val PERSIAN_DIGITS = hashMapOf(
        '0' to '۰',
        '1' to '۱',
        '2' to '۲',
        '3' to '۳',
        '4' to '۴',
        '5' to '۵',
        '6' to '۶',
        '7' to '۷',
        '8' to '۸',
        '9' to '۹'
    )

    /** تبدیل ارقام لاتین به فارسی — صرفاً برای نمایش */
    fun toPersianDigits(text: String): String = buildString {
        for (c in text) append(
            PERSIAN_DIGITS[c]
                    ?: c
        )
    }

    /**
     * تبدیل تاریخ شمسی به میلادی (فرمت YYYY-MM-DD)
     *
     * مهم: Locale.US الزامی است — بدون آن، روی دستگاه با زبان فارسی
     * String.format ارقام فارسی (۲۰۱۸) تولید می‌کند و سرور تاریخ را رد می‌کند
     */
    fun jalaliToGregorian(
        jYear: Int,
        jMonth: Int,
        jDay: Int
    ): String {
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
        val gDays = arrayOf(
            31,
            if (leap) 29 else 28,
            31,
            30,
            31,
            30,
            31,
            31,
            30,
            31,
            30,
            31
        )
        var gm = 0
        while (gDayNo >= gDays[gm]) {
            gDayNo -= gDays[gm]
            gm++
        }
        return String.format(
            Locale.US,
            "%04d-%02d-%02d",
            gy,
            gm + 1,
            gDayNo + 1
        )
    }

    /**
     * تبدیل تاریخ میلادی (YYYY-MM-DD) به شمسی (YYYY/MM/DD)
     * دقیقاً معکوسِ jalaliToGregorian است تا نمایش و ارسال همیشه سازگار باشند
     */
    fun gregorianToJalali(gregorianDate: String): String {
        return try {
            val parts = gregorianDate.trim()
                .split("-")
            val gy0 = parts[0].toInt()
            val gm = parts[1].toInt()
            val gd0 = parts[2].toInt()

            val gDaysInMonth = intArrayOf(
                31,
                28,
                31,
                30,
                31,
                30,
                31,
                31,
                30,
                31,
                30,
                31
            )

            val gy = gy0 - 1600
            val gmi = gm - 1
            val gd = gd0 - 1

            var gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400
            for (i in 0 until gmi) gDayNo += gDaysInMonth[i]
            if (gmi > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) gDayNo++
            gDayNo += gd

            var jDayNo = gDayNo - 79

            val jNp = jDayNo / 12053
            jDayNo %= 12053

            var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
            jDayNo %= 1461

            if (jDayNo >= 366) {
                jy += (jDayNo - 1) / 365
                jDayNo = (jDayNo - 1) % 365
            }

            // سال کبیسه شمسی — مطابق فرمول jalaliToGregorian همین فایل:
            // وقتی (سال - ۹۷۹) mod 33 در {0,4,8,12,16,20,24,28} باشد، اسفند ۳۰ روزه است
            // (بدون این، ۳۰ اسفند کبیسه به ماه ۱۳ سرریز می‌شد)
            val r = (jy - 979) % 33
            val isJalaliLeap = r == 0 || r == 4 || r == 8 || r == 12 || r == 16 || r == 20 || r == 24 || r == 28

            val jDaysInMonth = intArrayOf(
                31,
                31,
                31,
                31,
                31,
                31,
                30,
                30,
                30,
                30,
                30,
                if (isJalaliLeap) 30 else 29
            )
            var jm = 0
            while (jm < 12 && jDayNo >= jDaysInMonth[jm]) {
                jDayNo -= jDaysInMonth[jm]
                jm++
            }

            // Locale.US تا خروجی همیشه با ارقام لاتین باشد (این رشته دوباره پارس می‌شود)
            String.format(
                Locale.US,
                "%04d/%02d/%02d",
                jy,
                jm + 1,
                jDayNo + 1
            )
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * محاسبه سن دقیق بر اساس تاریخ میلادی (فرمت YYYY-MM-DD)
     * خروجی: "X سال و Y ماه" یا "Z روز"
     */
    fun calculateAgeFromGregorian(gregorianDateStr: String): String {
        return try {
            val parts = gregorianDateStr.split("-")
            val birthDate = LocalDate.of(
                parts[0].toInt(),
                parts[1].toInt(),
                parts[2].toInt()
            )
            val today = LocalDate.now()
            val period = Period.between(
                birthDate,
                today
            )

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