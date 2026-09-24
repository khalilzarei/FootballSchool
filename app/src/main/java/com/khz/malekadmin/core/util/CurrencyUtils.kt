package com.khz.malekadmin.core.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun formatCurrency(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(
            Locale(
                "fa",
                "IR"
            )
        )
        return "${formatter.format(amount)} تومان"
    }

    fun formatCurrencyShort(amount: Long): String {
        return when {
            amount >= 1_000_000_000 -> "${amount / 1_000_000_000} میلیارد"
            amount >= 1_000_000     -> "${amount / 1_000_000} میلیون"
            amount >= 1_000         -> "${amount / 1_000} هزار"
            else                    -> amount.toString()
        }
    }
}