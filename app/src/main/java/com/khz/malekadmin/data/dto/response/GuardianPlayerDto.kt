package com.khz.malekadmin.data.dto.response

import com.google.gson.annotations.SerializedName

/**
 * رکورد رابطه سرپرست-بازیکن (جدول guardian_player)
 * بسته به endpoint، آبجکت guardian یا player تو در تو می‌آید؛
 * هر دو nullable تعریف شده‌اند تا پارس هیچ‌گاه نشکند.
 */
data class GuardianPlayerDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("guardian_id") val guardianId: Int = 0,
    @SerializedName("player_id") val playerId: Int = 0,
    @SerializedName("relation") val relation: String = "",
    @SerializedName("is_primary") val isPrimary: Boolean = false,
    @SerializedName("can_view_reports") val canViewReports: Boolean = true,
    @SerializedName("can_pay") val canPay: Boolean = true,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("guardian") val guardian: GuardianDto? = null,
    @SerializedName("player") val player: PlayerDto? = null
)