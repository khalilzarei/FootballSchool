package com.khz.malekadmin.core.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * انتخاب و آماده‌سازی عکس/فیلم برای آپلود.
 *
 * تفاوت با MultipartHelper:
 *  - پسوند اصلی حفظ می‌شود (سرور allowlist پسوند دارد)
 *  - نام واقعی از ContentResolver خوانده می‌شود
 *  - MIME از خود Uri گرفته می‌شود، نه حدس
 *  - امکان پاک‌سازی فایل‌های موقت
 */
object MediaPickerHelper {

    private const val TAG = "MediaPickerHelper"
    private const val FIELD_NAME = "file"

    private fun uploadDir(context: Context): File = File(
        context.cacheDir,
        "pending_uploads"
    ).apply { if (!exists()) mkdirs() }

    fun queryFileInfo(
        context: Context,
        uri: Uri
    ): Pair<String, Long> {
        var name = "file_${UUID.randomUUID()}"
        var size = -1L

        runCatching {
            context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

                        if (nameIndex >= 0) {
                            cursor.getString(nameIndex)
                                ?.takeIf { it.isNotBlank() }
                                ?.let { name = it }
                        }
                        if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                            size = cursor.getLong(sizeIndex)
                        }
                    }
                }
        }.onFailure {
            Log.w(
                TAG,
                "queryFileInfo failed: ${it.message}"
            )
        }

        return name to size
    }

    fun filePart(
        context: Context,
        uri: Uri,
        fieldName: String = FIELD_NAME
    ): MultipartBody.Part? {
        return try {
            val (displayName, _) = queryFileInfo(
                context,
                uri
            )
            val safeName = displayName.replace(
                Regex("[^A-Za-z0-9._-]"),
                "_"
            )
            val tempFile = File(
                uploadDir(context),
                "${UUID.randomUUID()}_$safeName"
            )

            val opened = context.contentResolver.openInputStream(uri)
                ?.use { input ->
                    FileOutputStream(tempFile).use { output -> input.copyTo(output) }
                    true
                }
                    ?: false

            if (!opened || tempFile.length() <= 0L) {
                Log.e(
                    TAG,
                    "Empty or unreadable file for uri=$uri"
                )
                tempFile.delete()
                return null
            }

            val mime = context.contentResolver.getType(uri)
                    ?: guessMimeType(safeName)
            val requestBody = tempFile.asRequestBody(mime.toMediaTypeOrNull())

            MultipartBody.Part.createFormData(
                fieldName,
                safeName,
                requestBody
            )
        } catch (e: Exception) {
            Log.e(
                TAG,
                "filePart failed: ${e.message}",
                e
            )
            null
        }
    }

    fun guessMimeType(fileName: String): String = when (fileName.substringAfterLast(
        '.',
        ""
    )
        .lowercase()) {
        "jpg", "jpeg" -> "image/jpeg"
        "png"         -> "image/png"
        "gif"         -> "image/gif"
        "webp"        -> "image/webp"
        "mp4", "m4v"  -> "video/mp4"
        "webm"        -> "video/webm"
        "mov"         -> "video/quicktime"
        else          -> "application/octet-stream"
    }

    /** هم‌خوان با MediaService::allowedMimeTypes در سرور */
    fun isAllowed(fileName: String): Boolean {
        val ext = fileName.substringAfterLast(
            '.',
            ""
        )
            .lowercase()
        return ext in setOf(
            "jpg",
            "jpeg",
            "png",
            "gif",
            "webp",
            "mp4",
            "m4v",
            "webm",
            "mov"
        )
    }

    fun clearTempFiles(context: Context) {
        runCatching {
            uploadDir(context).listFiles()
                ?.forEach { it.delete() }
        }.onFailure {
            Log.w(
                TAG,
                "clearTempFiles failed: ${it.message}"
            )
        }
    }
}