package com.khz.footballschool.ui.invoices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.util.CurrencyUtils
import com.khz.footballschool.core.util.DateUtils
import com.khz.footballschool.domain.model.Invoice
import com.khz.footballschool.ui.components.GlassBackground
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@Composable
fun InvoiceDetailScreen(
    invoiceId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.invoiceRepository
    val scope = rememberCoroutineScope()

    var invoice by remember { mutableStateOf<Invoice?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun load() {
        scope.launch {
            loading = true
            when (val r = repo.getInvoice(invoiceId)) {
                is NetworkResult.Success -> invoice = r.data
                is NetworkResult.Error   -> error = r.message
                else                     -> {}
            }
            loading = false
        }
    }

    LaunchedEffect(invoiceId) { load() }

    GlassBackground {
        Box(Modifier.fillMaxSize()) {
            GlassTopBar(
                title = "جزئیات فاکتور #${DateUtils.toPersianDigits(invoiceId.toString())}",
                onBack = onBack
            )

            if (loading) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }
            } else {
                invoice?.let { inv ->
                    LazyColumn(
                        modifier = Modifier
                            .padding(top = 56.dp)
                            .padding(16.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            GlassCard3D() {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                inv.player?.fullName
                                                        ?: "بازیکن #${DateUtils.toPersianDigits(inv.playerId.toString())}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                "نوع: ${inv.invoiceType} | وضعیت: ${inv.status}",
                                                color = Color.White.copy(0.6f),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            inv.dueDate?.let {
                                                Text(
                                                    "سررسید: ${DateUtils.toPersianDigits(DateUtils.gregorianToJalali(it))}",
                                                    color = Color.White.copy(0.5f),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                        Box(
                                            Modifier.clip(RoundedCornerShape(8.dp))
                                                .background(if (inv.isDebtor) Color(0xFFFF8A80).copy(0.2f) else Color(0xFF81C784).copy(0.2f))
                                                .padding(
                                                    horizontal = 10.dp,
                                                    vertical = 6.dp
                                                )
                                        ) {
                                            Text(
                                                if (inv.isDebtor) "بدهکار" else "تسویه",
                                                color = if (inv.isDebtor) Color(0xFFFF8A80) else Color(0xFF81C784),
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(4.dp))

                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        FinanceBox(
                                            "کل فاکتور",
                                            inv.totalAmount,
                                            Color.White
                                        )
                                        FinanceBox(
                                            "پرداخت شده",
                                            inv.paidAmount,
                                            Color(0xFF81C784)
                                        )
                                        FinanceBox(
                                            "باقی‌مانده",
                                            inv.remainingAmount,
                                            if (inv.remainingAmount > 0) Color(0xFFFF8A80) else Color(0xFF81C784)
                                        )
                                    }

                                    if (inv.classDebts.isNotEmpty()) {
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "بدهی به تفکیک کلاس:",
                                            color = Color.White.copy(0.6f),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            inv.classDebts.forEach { cd ->
                                                Row(
                                                    Modifier.fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color.White.copy(0.06f))
                                                        .padding(10.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(Modifier.weight(1f)) {
                                                        Text(
                                                            cd.classTitle,
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                                        )
                                                        cd.ageGroupTitle?.let {
                                                            Text(
                                                                it,
                                                                color = Color.White.copy(0.5f),
                                                                style = MaterialTheme.typography.labelSmall
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        CurrencyUtils.formatCurrency(cd.total),
                                                        color = Color.White.copy(0.8f),
                                                        style = MaterialTheme.typography.labelMedium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                "آیتم‌های فاکتور (${DateUtils.toPersianDigits(inv.items.size.toString())}):",
                                color = Color.White.copy(0.7f),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        items(inv.items) { item ->
                            GlassCard3D() {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            item.title,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            item.classTitle?.let {
                                                Box(
                                                    Modifier.clip(RoundedCornerShape(6.dp))
                                                        .background(GoldPrimary.copy(0.15f))
                                                        .padding(
                                                            horizontal = 6.dp,
                                                            vertical = 2.dp
                                                        )
                                                ) {
                                                    Text(
                                                        it,
                                                        color = GoldPrimary,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                            }
                                            Box(
                                                Modifier.clip(RoundedCornerShape(6.dp))
                                                    .background(Color.White.copy(0.08f))
                                                    .padding(
                                                        horizontal = 6.dp,
                                                        vertical = 2.dp
                                                    )
                                            ) {
                                                Text(
                                                    "${item.itemType} | ${DateUtils.toPersianDigits(item.quantity.toString())} × ${CurrencyUtils.formatCurrency(item.amount)}",
                                                    color = Color.White.copy(0.6f),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                        item.description?.takeIf { it.isNotBlank() }
                                            ?.let {
                                                Text(
                                                    it,
                                                    color = Color.White.copy(0.5f),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                    }
                                    Text(
                                        CurrencyUtils.formatCurrency(item.total),
                                        color = GoldPrimary,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        if (inv.discounts.isNotEmpty()) {
                            item {
                                Text(
                                    "تخفیف‌ها:",
                                    color = Color.White.copy(0.6f),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            items(inv.discounts) { d ->
                                GlassCard3D() {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            d.title,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            "-${CurrencyUtils.formatCurrency(d.value.toLong())}",
                                            color = Color(0xFF81C784),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }

                        if (inv.payments.isNotEmpty()) {
                            item {
                                Text(
                                    "پرداخت‌ها:",
                                    color = Color.White.copy(0.6f),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            items(inv.payments) { p ->
                                GlassCard3D() {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                CurrencyUtils.formatCurrency(p.amount),
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                p.paymentMethod
                                                        ?: "-",
                                                color = Color.White.copy(0.5f),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                        Box(
                                            Modifier.clip(RoundedCornerShape(6.dp))
                                                .background(if (p.status == "approved") Color(0xFF81C784).copy(0.2f) else GoldPrimary.copy(0.2f))
                                                .padding(
                                                    horizontal = 8.dp,
                                                    vertical = 4.dp
                                                )
                                        ) {
                                            Text(
                                                p.status,
                                                color = if (p.status == "approved") Color(0xFF81C784) else GoldPrimary,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item { Spacer(Modifier.height(40.dp)) }
                    }
                }
                        ?: run {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(top = 56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    error
                                            ?: "فاکتور یافت نشد",
                                    color = Color.White.copy(0.6f)
                                )
                            }
                        }
            }
        }
    }
}

@Composable
private fun FinanceBox(
    label: String,
    amount: Long,
    color: Color
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(0.12f))
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            color = color.copy(0.7f),
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            CurrencyUtils.formatCurrency(amount),
            color = color,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}
