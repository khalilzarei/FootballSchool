package com.khz.footballschool.data.dto.request

import com.google.gson.annotations.SerializedName

data class AttachGuardianToPlayerRequest(
    @SerializedName("guardian_id") val guardianId: Int,
    @SerializedName("relation") val relation: String,
    @SerializedName("is_primary") val isPrimary: Boolean = false,
    @SerializedName("can_view_reports") val canViewReports: Boolean = true,
    @SerializedName("can_pay") val canPay: Boolean = true
)

data class AttachPlayerToGuardianRequest(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("relation") val relation: String,
    @SerializedName("is_primary") val isPrimary: Boolean = false,
    @SerializedName("can_view_reports") val canViewReports: Boolean = true,
    @SerializedName("can_pay") val canPay: Boolean = true
)

/**
 * ساخت سرپرست جدید (با موبایل) + اتصال به بازیکن — POST players/{id}/guardians/new
 */
data class CreateGuardianForPlayerRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("national_code") val nationalCode: String? = null,
    @SerializedName("relation") val relation: String,
    @SerializedName("is_primary") val isPrimary: Boolean = false,
    @SerializedName("can_view_reports") val canViewReports: Boolean = true,
    @SerializedName("can_pay") val canPay: Boolean = true
)

/**
 * ویرایش سرپرستِ متصل به بازیکن — PUT players/{id}/guardians/{guardianId}
 */
data class UpdateGuardianRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("mobile") val mobile: String,
    @SerializedName("national_code") val nationalCode: String? = null,
    @SerializedName("relation") val relation: String,
    @SerializedName("can_view_reports") val canViewReports: Boolean = true,
    @SerializedName("can_pay") val canPay: Boolean = true
)