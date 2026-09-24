package com.khz.malekadmin.core.util

import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object MultipartHelper {

    private const val TAG = "MultipartHelper"

    /**
     * تبدیل String به Part متنی
     *
     * ✅ از overload دو پارامتری استفاده می‌کنیم که filename ندارد
     * ✅ این باعث می‌شود Laravel آن را به‌عنوان فیلد فرم بشناسد
     */
    fun textPart(name: String, value: String?): MultipartBody.Part? {
        if (value.isNullOrBlank()) {
            Log.d(TAG, "Skipping $name: value is null or blank")
            return null
        }

        // ✅ overload صحیح: فقط name و value، بدون filename
        val part = MultipartBody.Part.createFormData(name, value)
        Log.d(TAG, "Created text part: $name = $value")
        return part
    }

    /**
     * تبدیل Int به Part متنی
     */
    fun textPart(name: String, value: Int?): MultipartBody.Part? {
        if (value == null) {
            Log.d(TAG, "Skipping $name: value is null")
            return null
        }
        return textPart(name, value.toString())
    }

    /**
     * تبدیل Boolean به Part متنی (0/1)
     */
    fun boolPart(name: String, value: Boolean?): MultipartBody.Part? {
        if (value == null) return null
        return textPart(name, if (value) "1" else "0")
    }

    /**
     * تبدیل URI به Part فایل (عکس)
     *
     * ✅ فقط برای فایل‌ها از overload سه پارامتری با filename استفاده می‌کنیم
     */
    fun filePart(
        context: Context,
        uri: Uri,
        fieldName: String = "avatar"
    ): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e(TAG, "Cannot open input stream for URI: $uri")
                return null
            }

            val fileName = "avatar_${UUID.randomUUID()}.jpg"
            val tempFile = File(context.cacheDir, fileName)

            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()

            Log.d(TAG, "Created temp file: ${tempFile.absolutePath}, size: ${tempFile.length()} bytes")

            val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
            val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())

            // ✅ برای فایل، overload سه پارامتری با filename صحیح است
            val part = MultipartBody.Part.createFormData(fieldName, tempFile.name, requestFile)
            Log.d(TAG, "Created file part: $fieldName = ${tempFile.name}")

            part
        } catch (e: Exception) {
            Log.e(TAG, "Error creating file part: ${e.message}", e)
            null
        }
    }
}