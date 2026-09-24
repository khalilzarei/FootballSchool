package com.khz.malekadmin.core.util

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImagePickerHelper {

    /**
     * تبدیل URI به File برای آپلود
     */
    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "${UUID.randomUUID()}.jpg"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    /**
     * ساخت MultipartBody.Part مستقیم از URI
     */
    fun uriToMultipartPart(context: Context, uri: Uri, fieldName: String = "avatar"): MultipartBody.Part? {
        val file = uriToFile(context, uri) ?: return null
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(fieldName, file.name, requestFile)
    }

    /**
     * دریافت سایز فایل به KB
     */
    fun getFileSizeKB(context: Context, uri: Uri): Long {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { it.statSize / 1024 } ?: 0
        } catch (e: Exception) { 0 }
    }
}