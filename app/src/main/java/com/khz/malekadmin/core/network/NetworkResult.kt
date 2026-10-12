package com.khz.malekadmin.core.network

/**
 * نتیجه‌ی یک فراخوانی شبکه‌ای.
 *
 * نکته‌ی مهم: [Error] دیگر یک data class ساده نیست؛ پیام خام در سازنده
 * از [ErrorMessageSanitizer] عبور می‌کند تا هیچ آدرس API / هاست / مسیر فایلی
 * به UI نرسد. رفتار بیرونی کلاس (ساخت با `NetworkResult.Error("...")`,
 * دسترسی به `.message`, `copy()`, تخریب/Destructuring) بدون تغییر مانده است.
 */
sealed class NetworkResult<out T> {

    data class Success<T>(val data: T) : NetworkResult<T>()

    class Error(
        message: String,
        val code: Int? = null
    ) : NetworkResult<Nothing>() {

        /** پیام پاک‌سازی‌شده و امن برای نمایش */
        val message: String = ErrorMessageSanitizer.sanitize(
            message,
            ErrorMessageSanitizer.NEUTRAL_MESSAGE
        )

        operator fun component1(): String = message

        operator fun component2(): Int? = code

        fun copy(
            message: String = this.message,
            code: Int? = this.code
        ): Error = Error(
            message,
            code
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Error) return false
            return message == other.message && code == other.code
        }

        override fun hashCode(): Int = 31 * message.hashCode() + (code
                ?: 0)

        override fun toString(): String = "Error(message=$message, code=$code)"
    }

    object Loading : NetworkResult<Nothing>()
}
