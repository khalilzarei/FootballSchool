package com.khz.footballschool.ui.invoices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.khz.footballschool.core.util.CurrencyUtils
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GenericListScreen
import com.khz.footballschool.ui.components.InfoCard

@Composable
fun InvoiceListScreen() {
    val vm: InvoiceListViewModel = appViewModel()
    val state by vm.state.collectAsState()
    GenericListScreen(
        title = "فاکتورها",
        state = state,
        onRefresh = { vm.refresh() }
    ) { inv ->
        InfoCard(
            title = "${inv.player?.fullName ?: "-"} | ${CurrencyUtils.formatCurrency(inv.totalAmount)}",
            subtitle = "باقی‌مانده: ${CurrencyUtils.formatCurrency(inv.remainingAmount)}",
            trailing = inv.status
        ) {
            if (inv.status == "pending" || inv.status == "partial") {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { vm.cancelInvoice(inv.id) }) {
                        Text("لغو فاکتور", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}