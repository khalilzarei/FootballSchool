package com.khz.malekadmin.core.network

suspend inline fun <T, R> safeCall(
    crossinline call: suspend () -> ApiResponse<T>,
    crossinline map: (T) -> R
): NetworkResult<R> = try {
    val r = call()
    if (r.success && r.data != null) NetworkResult.Success(map(r.data))
    else NetworkResult.Error(r.message ?: "خطا در دریافت اطلاعات")
} catch (e: Exception) {
    NetworkResult.Error(e.message ?: "خطای شبکه")
}