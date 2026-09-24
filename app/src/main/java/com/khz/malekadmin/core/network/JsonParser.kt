package com.khz.malekadmin.core.network

import com.google.gson.Gson
import com.google.gson.JsonElement

object JsonParser {

     val gson = Gson()

    inline fun <reified T> parse(element: JsonElement?): T? {
        if (element == null || element.isJsonNull) return null
        return try {
            gson.fromJson(element, T::class.java)
        } catch (e: Exception) {
            android.util.Log.e("JsonParser", "Parse error for ${T::class.simpleName}: ${e.message}")
            null
        }
    }

    inline fun <reified T> parseField(element: JsonElement?, fieldName: String): T? {
        if (element == null || !element.isJsonObject) return null
        val obj = element.asJsonObject
        if (!obj.has(fieldName) || obj.get(fieldName).isJsonNull) return null
        return parse<T>(obj.get(fieldName))
    }

    /**
     * ابتدا کلیدهای wrapper را امتحان می‌کند؛
     * اگر هیچ‌کدام نبود، خود آبجکت را به‌عنوان DTO پارس می‌کند.
     * این برای حالت‌هایی مفید است که سرور گاهی مستقیم DTO برمی‌گرداند.
     */
    inline fun <reified T> JsonElement?.unwrap(vararg candidateFields: String): T? {
        if (this == null) return null

        // تلاش ۱: بررسی کلیدهای wrapper
        if (this.isJsonObject) {
            val obj = this.asJsonObject
            for (field in candidateFields) {
                val el = obj.get(field)
                if (el != null && el.isJsonObject) {
                    parse<T>(el)?.let { return it }
                }
            }
            // تلاش ۲: پارس مستقیم خود آبجکت (برای پاسخ‌های بدون wrapper)
            parse<T>(obj)?.let { return it }
        }

        return null
    }

    /**
     * برای پارس لیست‌ها از JsonElement
     */
    inline fun <reified T> JsonElement?.unwrapList(): List<T> {
        if (this == null || !this.isJsonArray) return emptyList()
        return this.asJsonArray.mapNotNull { parse<T>(it) }
    }
}