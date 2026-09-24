package com.khz.malekadmin.ui.invoices

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.khz.malekadmin.core.util.CurrencyUtils
import com.khz.malekadmin.core.util.DateUtils
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.ui.components.GenericListScreen
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.theme.GoldPrimary

@Composable
fun InvoiceListScreen(onDetail: ((Int) -> Unit)? = null) {
    val vm: InvoiceListViewModel = appViewModel()
    val state by vm.state.collectAsState()
    val selectedFilter by vm.filter.collectAsState()

    // رفرش خودکار هنگام بازگشت از جزئیات
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) vm.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    GenericListScreen(
        title = "فاکتورها",
        state = state,
        onRefresh = { vm.refresh() },
        headerContent = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // خلاصه مالی
                val invoices = when (val s = state) {
                    is com.khz.malekadmin.ui.components.ListState.Success -> s.items
                    else                                                  -> emptyList()
                }
                // برای آمار کلی باید از همه فاکتورها استفاده کنیم، نه فقط فیلتر شده - فعلاً از همین لیست
                // آمار واقعی از ViewModel.allInvoices بهتر است، اما برای سادگی از state فعلی استفاده می‌کنیم
                // اگر نیاز بود، می‌توان allInvoices را expose کرد

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(InvoiceStatusFilter.values()) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { vm.setFilter(filter) },
                            label = {
                                Text(
                                    filter.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (filter) {
                                    InvoiceStatusFilter.OPEN      -> Color(0xFFFF8A80).copy(0.2f)
                                    InvoiceStatusFilter.PAID      -> Color(0xFF81C784).copy(0.2f)
                                    InvoiceStatusFilter.OVERDUE   -> Color(0xFFFF5252).copy(0.2f)
                                    InvoiceStatusFilter.CANCELLED -> Color.White.copy(0.1f)
                                    else                          -> GoldPrimary.copy(0.2f)
                                },
                                selectedLabelColor = when (filter) {
                                    InvoiceStatusFilter.OPEN    -> Color(0xFFFF8A80)
                                    InvoiceStatusFilter.PAID    -> Color(0xFF81C784)
                                    InvoiceStatusFilter.OVERDUE -> Color(0xFFFF5252)
                                    else                        -> GoldPrimary
                                }
                            )
                        )
                    }
                }
            }
        }) { inv ->
        GlassCard3D(
            onClick = if (onDetail != null) {
                { onDetail(inv.id) }
            } else null
        ) {
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
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (inv.status) {
                                    "paid"      -> Color(0xFF81C784).copy(0.2f)
                                    "partial"   -> Color(0xFFFFB74D).copy(0.2f)
                                    "overdue"   -> Color(0xFFFF5252).copy(0.2f)
                                    "cancelled" -> Color.White.copy(0.1f)
                                    else        -> Color(0xFFFF8A80).copy(0.2f)
                                }
                            )
                            .padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                    ) {
                        val label = when (inv.status) {
                            "paid"      -> "تسویه"
                            "partial"   -> "نیمه پرداخت"
                            "open"      -> "بدهکار"
                            "overdue"   -> "سررسید گذشته"
                            "cancelled" -> "لغو شده"
                            else        -> inv.status
                        }
                        Text(
                            label,
                            color = when (inv.status) {
                                "paid"      -> Color(0xFF81C784)
                                "partial"   -> Color(0xFFFFB74D)
                                "overdue"   -> Color(0xFFFF5252)
                                "cancelled" -> Color.White.copy(0.5f)
                                else        -> Color(0xFFFF8A80)
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
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
                                    Modifier
                                        .clip(RoundedCornerShape(6.dp))
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
                    if (inv.status == "open" || inv.status == "partial" || inv.status == "overdue") {
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
    }
}
