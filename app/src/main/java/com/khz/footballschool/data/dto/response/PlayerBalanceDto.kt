package com.khz.footballschool.data.dto.response

import com.google.gson.annotations.SerializedName

data class PlayerBalanceDto(
    @SerializedName("player_id") val playerId: Int,
    @SerializedName("total_invoiced") val totalInvoiced: Long? = null,
    @SerializedName("total_paid") val totalPaid: Long? = null,
    @SerializedName("balance") val balance: Long? = null,
    @SerializedName("debt") val debt: Long? = null,
    @SerializedName("pending_payments") val pendingPayments: Long? = null,
    @SerializedName("pending_payments_count") val pendingPaymentsCount: Int? = null,
    @SerializedName("pending_amount") val pendingAmount: Long? = null,
    @SerializedName("is_debtor") val isDebtor: Boolean? = null,
    @SerializedName("class_debts") val classDebts: List<ClassDebtDto>? = null,
    @SerializedName("class_fees") val classFees: List<ClassFeeDto>? = null
)

data class ClassDebtDto(
    @SerializedName("class_id") val classId: Int?,
    @SerializedName("class_title") val classTitle: String?,
    @SerializedName("age_group_title") val ageGroupTitle: String?,
    @SerializedName("total") val total: Long?,
    @SerializedName("paid") val paid: Long?,
    @SerializedName("remaining") val remaining: Long?,
    @SerializedName("items_count") val itemsCount: Int? = null
)

data class ClassFeeDto(
    @SerializedName("class_id") val classId: Int?,
    @SerializedName("class_title") val classTitle: String?,
    @SerializedName("age_group_title") val ageGroupTitle: String?,
    @SerializedName("monthly_fee") val monthlyFee: Long?,
    @SerializedName("session_fee") val sessionFee: Long?,
    @SerializedName("registration_fee") val registrationFee: Long?,
    @SerializedName("debt") val debt: Long? = null
)
