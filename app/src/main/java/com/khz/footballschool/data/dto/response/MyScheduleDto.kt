package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * یک جلسه در «برنامه من» (GET me/schedule)
 * سرور ردیف football_sessions را با class_title برمی‌گرداند:
 * {"id":12,"class_id":3,"class_title":"زیر ۹ سال","session_date":"2026-09-15",
 *  "start_time":"17:00:00","end_time":"18:30:00","location":"زمین ۱",
 *  "status":"scheduled","topic":"پاسکاری","notes":"...","created_at":"..."}
 *
 * نکته Gson: مقادیر پیش‌فرض روی فیلد غایب اعمال نمی‌شوند؛ همه nullable.
 */
data class MyScheduleDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("class_id") val classId: Int? = null,
    @SerializedName("class_title") val classTitle: String? = null,
    @SerializedName("session_date") val sessionDate: String? = null,
    @SerializedName("start_time") val startTime: String? = null,
    @SerializedName("end_time") val endTime: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("topic") val topic: String? = null,
    @SerializedName("notes") val notes: String? = null
)
