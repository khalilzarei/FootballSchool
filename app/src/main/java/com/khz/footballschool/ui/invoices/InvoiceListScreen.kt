package com.khz.footballschool.ui.invoices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.footballschool.core.util.CurrencyUtils
import com.khz.footballschool.core.util.DateUtils
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GenericListScreen
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.theme.GoldPrimary

@Composable
fun InvoiceListScreen(onDetail: ((Int) -> Unit)? = null) {
    val vm: InvoiceListViewModel = appViewModel()
    val state by vm.state.collectAsState()
    GenericListScreen(
        title = "فاکتورها",
        state = state,
        onRefresh = { vm.refresh() }) { inv ->
        GlassCard3D(
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            inv.player?.fullName
                                    ?: "بازیکن #${DateUtils.toPersianDigits(inv.playerId.toString())}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            Modifier.clip(RoundedCornerShape(8.dp))
                                .background(if (inv.isDebtor) Color(0xFFFF8A80).copy(0.2f) else Color(0xFF81C784).copy(0.2f))
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                )
                        ) {
                            Text(
                                inv.status,
                                color = if (inv.isDebtor) Color(0xFFFF8A80) else Color(0xFF81C784),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "کل: ${CurrencyUtils.formatCurrency(inv.totalAmount)}",
                            color = Color.White.copy(0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "پرداختی: ${CurrencyUtils.formatCurrency(inv.paidAmount)}",
                            color = Color(0xFF81C784).copy(0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "باقی‌مانده: ${CurrencyUtils.formatCurrency(inv.remainingAmount)}",
                            color = if (inv.remainingAmount > 0) Color(0xFFFF8A80) else Color(0xFF81C784),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    if (inv.classDebts.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            inv.classDebts.take(3)
                                .forEach { cd ->
                                    Box(
                                        Modifier.clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(0.06f))
                                            .padding(
                                                horizontal = 6.dp,
                                                vertical = 3.dp
                                            )
                                    ) {
                                        Text(
                                            "${cd.classTitle}: ${CurrencyUtils.formatCurrency(cd.total)}",
                                            color = Color.White.copy(0.6f),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            if (inv.classDebts.size > 3) {
                                Text(
                                    "+${DateUtils.toPersianDigits((inv.classDebts.size - 3).toString())}",
                                    color = Color.White.copy(0.4f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                    if (inv.items.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            inv.items.take(3)
                                .forEach { item ->
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            item.title,
                                            color = Color.White.copy(0.6f),
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.weight(1f)
                                        )
                                        item.classTitle?.let {
                                            Text(
                                                it,
                                                color = GoldPrimary.copy(0.7f),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            CurrencyUtils.formatCurrency(item.total),
                                            color = Color.White.copy(0.7f),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            if (inv.items.size > 3) Text(
                                "... و ${DateUtils.toPersianDigits((inv.items.size - 3).toString())} آیتم دیگر",
                                color = Color.White.copy(0.4f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onDetail != null) {
                            Text(
                                "لمس برای جزئیات",
                                color = Color.White.copy(0.35f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        if (inv.status == "pending" || inv.status == "partial" || inv.status == "open") {
                            TextButton(onClick = { vm.cancelInvoice(inv.id) }) {
                                Text(
                                    "لغو فاکتور",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}
