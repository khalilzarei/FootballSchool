package com.khz.malekadmin.core.util

/**
 * اعتبارسنجی ورودی‌های کاربر — پیام خطا فارسی برمی‌گرداند یا null اگر معتبر است.
 * یکپارچه با اپ کلاینت برای تجربه‌ی یکسان.
 */
object InputValidator {

    /**
     * کد ملی ایران: دقیقاً ۱۰ رقم + الگوریتم checksum (سخت‌گیر — برای ثبت‌نام)
     * @return null اگر معتبر است، وگرنه پیام خطا
     */
    fun validateNationalCode(code: String?): String? {
        val raw = code?.trim()
            ?.filter { it.isDigit() }
            .orEmpty()
        if (raw.isEmpty()) return "کد ملی الزامی است"
        if (raw.length != 10) return "کد ملی باید ۱۰ رقم باشد"
        if (raw.all { it == raw[0] }) return "کد ملی معتبر نیست"
        val check = raw[9].digitToInt()
        var sum = 0
        for (i in 0 until 9) {
            sum += raw[i].digitToInt() * (10 - i)
        }
        val remainder = sum % 11
        val expected = if (remainder < 2) remainder else 11 - remainder
        if (check != expected) return "کد ملی معتبر نیست"
        return null
    }

    /**
     * اعتبارسنجی شناسه‌ی ورود (کد ملی یا موبایل) — بدون checksum
     * برای لاگین هر ۱۰ رقمی پذیرفته است (داده‌های قدیمی).
     */
    fun validateLoginIdentifier(identifier: String?): String? {
        val raw = identifier?.trim()
            ?.filter { it.isDigit() }
            .orEmpty()
        if (raw.isEmpty()) return "کد ملی یا موبایل را وارد کنید"
        if (raw.length == 10) {
            if (raw.all { it == raw[0] }) return "کد ملی معتبر نیست"
            return null
        }
        if (raw.length == 11 && raw.startsWith("09")) return null
        if (raw.length == 10 && raw.startsWith("9")) return null
        return "کد ملی باید ۱۰ رقم باشد"
    }

    fun validateMobile(mobile: String?): String? {
        val raw = mobile?.trim()
            ?.filter { it.isDigit() }
            .orEmpty()
        if (raw.isEmpty()) return null
        val normalized = if (raw.length == 10 && raw.startsWith("9")) "0$raw" else raw
        if (normalized.length != 11) return "شماره موبایل باید ۱۱ رقم باشد (۰۹...)"
        if (!normalized.startsWith("09")) return "شماره موبایل باید با ۰۹ شروع شود"
        return null
    }

    fun validateMobileRequired(mobile: String?): String? {
        if (mobile.isNullOrBlank()) return "شماره موبایل الزامی است"
        return validateMobile(mobile)
    }

    fun validateEmergencyPhone(phone: String?): String? {
        val raw = phone?.trim()
            ?.filter { it.isDigit() }
            .orEmpty()
        if (raw.isEmpty()) return null
        if (raw.length < 8 || raw.length > 11) return "شماره تماس اضطراری معتبر نیست"
        return null
    }

    fun validateName(
        name: String?,
        fieldLabel: String = "نام"
    ): String? {
        val t = name?.trim()
            .orEmpty()
        if (t.isEmpty()) return "$fieldLabel الزامی است"
        if (t.length < 2) return "$fieldLabel باید حداقل ۲ حرف باشد"
        if (t.length > 50) return "$fieldLabel خیلی طولانی است"
        val invalid = t.any { ch ->
            !ch.isLetter() && ch != ' ' && ch != '‌' && ch != '-' && ch != '‍'
        }
        if (invalid) return "$fieldLabel فقط باید شامل حروف باشد"
        return null
    }

    fun validateFullName(fullName: String?): String? {
        val t = fullName?.trim()
            .orEmpty()
        if (t.isEmpty()) return "نام کامل الزامی است"
        if (t.length < 3) return "نام کامل باید حداقل ۳ حرف باشد"
        return null
    }

    fun validateBirthDate(gregorianDate: String?): String? {
        val raw = gregorianDate?.trim()
            .orEmpty()
        if (raw.isEmpty()) return null
        val regex = Regex("""^\d{4}-\d{2}-\d{2}$""")
        if (!regex.matches(raw)) return "تاریخ تولد نامعتبر است"
        if (raw > DateUtils.todayGregorian()) return "تاریخ تولد نمی‌تواند در آینده باشد"
        val year = raw.substring(
            0,
            4
        )
            .toIntOrNull()
                ?: return "تاریخ تولد نامعتبر است"
        if (year !in 1900..2100) return "سال تولد نامعتبر است"
        return null
    }
}
