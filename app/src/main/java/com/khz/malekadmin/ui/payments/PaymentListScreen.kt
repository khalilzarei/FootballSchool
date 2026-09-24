package com.khz.malekadmin.ui.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.core.util.CurrencyUtils
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.ui.components.GenericListScreen
import com.khz.malekadmin.ui.components.InfoCard
import com.khz.malekadmin.ui.theme.GoldPrimary

@Composable
fun PaymentListScreen() {
    val vm: PaymentListViewModel = appViewModel()
    val state by vm.state.collectAsState()
    GenericListScreen(
        title = "پرداخت‌ها",
        state = state,
        onRefresh = { vm.refresh() }) { p ->
        InfoCard(
            title = "${p.player?.fullName ?: "-"} | ${CurrencyUtils.formatCurrency(p.amount)}",
            subtitle = "روش: ${p.paymentMethod}",
            trailing = p.status
        ) {
            if (p.status == "pending") {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        androidx.compose.ui.Alignment.End
                    )
                ) {
                    TextButton(onClick = { vm.reject(p.id) }) { Text("رد") }
                    TextButton(onClick = { vm.approve(p.id) }) {
                        Text(
                            "تأیید",
                            color = GoldPrimary
                        )
                    }
                }
            }
        }
    }
}