package com.khz.malekadmin.core.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException

/**
 * استخراج پیام خطای واقعی از پاسخ‌های HTTP غیر-2xx
 */
object ApiErrorHandler {

    private val gson = Gson()

    /**
     * پیام خطا را از HttpException استخراج می‌کند.
     * اگر body پاسخ شامل "message" باشد، آن را برمی‌گرداند.
     * در غیر این صورت، پیام پیش‌فرض exception را برمی‌گرداند.
     */
    fun extractMessage(throwable: Throwable): String {
        return when (throwable) {
            // خطای وضعیت اتصال (اینترنت قطع / فیلترشکن روشن) — پیام آماده دارد
            is NetworkConnectivityException -> throwable.message
            is HttpException                -> {
                val errorBody = throwable.response()
                    ?.errorBody()
                        ?: return defaultHttpMessage(throwable.code())
                parseErrorBody(errorBody)
                        ?: defaultHttpMessage(throwable.code())
            }

            is IOException                  -> "خطا در اتصال به سرور. لطفاً اتصال اینترنت خود را بررسی کنید."
            is JsonSyntaxException          -> "خطا در پردازش پاسخ سرور"
            else                            -> throwable.message
                    ?: "خطای ناشناخته"
        }
    }

    private fun parseErrorBody(errorBody: ResponseBody): String? {
        return try {
            val jsonString = errorBody.string()
            val jsonObject = gson.fromJson(
                jsonString,
                Map::class.java
            )
            jsonObject?.get("message")
                ?.toString()
        } catch (e: Exception) {
            null
        }
    }

    private fun defaultHttpMessage(code: Int): String = when (code) {
        400 -> "درخواست نامعتبر"
        401 -> "نشست شما منقضی شده است. لطفاً دوباره وارد شوید."
        403 -> "شما اجازه دسترسی به این بخش را ندارید."
        404 -> "مورد درخواستی یافت نشد."
        422 -> "اطلاعات وارد شده نامعتبر است."
        429 -> "تعداد درخواست‌ها بیش از حد مجاز است. لطفاً صبر کنید."
        500, 502, 503, 504 -> "خطای داخلی سرور. لطفاً بعداً تلاش کنید."
        else -> "خطای سرور (کد $code)"
    }
}