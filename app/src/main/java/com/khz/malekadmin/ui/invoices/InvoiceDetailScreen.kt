package com.khz.malekadmin.ui.invoices

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.khz.malekadmin.FootballSchoolApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.core.util.CurrencyUtils
import com.khz.malekadmin.core.util.DateUtils
import com.khz.malekadmin.data.dto.request.AddInvoiceItemRequest
import com.khz.malekadmin.data.dto.request.UpdateInvoiceItemRequest
import com.khz.malekadmin.domain.model.Invoice
import com.khz.malekadmin.domain.model.InvoiceItem
import com.khz.malekadmin.ui.components.GlassBackground
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassTextField
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary
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
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<InvoiceItem?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<InvoiceItem?>(null) }

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
                            GlassCard3D {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            // نام بازیکن - الان باید درست باشد چون بک‌اند player full_name برمی‌گرداند
                                            Text(inv.player?.fullName?.takeIf { it.isNotBlank() && !it.startsWith("بازیکن #") }
                                                    ?: "بازیکن #${inv.playerId}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
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
                                            inv.invoiceNumber?.let {
                                                Text(
                                                    "شماره: $it",
                                                    color = Color.White.copy(0.4f),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                        Box(
                                            Modifier
                                                .clip(RoundedCornerShape(8.dp))
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

                                    if (inv.remainingAmount > 0) {
                                        Spacer(Modifier.height(8.dp))
                                        GlassButton(
                                            text = "💰 ثبت پرداخت (${CurrencyUtils.formatCurrency(inv.remainingAmount)})",
                                            onClick = { showPaymentDialog = true },
                                            modifier = Modifier.fillMaxWidth()
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
                                                    Modifier
                                                        .fillMaxWidth()
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
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "آیتم‌های فاکتور (${DateUtils.toPersianDigits(inv.items.size.toString())}):",
                                    color = Color.White.copy(0.7f),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                TextButton(onClick = { showAddItemDialog = true }) {
                                    Icon(
                                        Icons.Default.Add,
                                        null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        " افزودن آیتم",
                                        color = GoldPrimary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }

                        items(inv.items) { item ->
                            GlassCard3D {
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
                                                    Modifier
                                                        .clip(RoundedCornerShape(6.dp))
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
                                                Modifier
                                                    .clip(RoundedCornerShape(6.dp))
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
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            CurrencyUtils.formatCurrency(item.total),
                                            color = GoldPrimary,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Row {
                                            IconButton(
                                                onClick = { editingItem = item },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    "ویرایش",
                                                    tint = Color.White.copy(0.6f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { showDeleteConfirm = item },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    "حذف",
                                                    tint = Color(0xFFFF8A80).copy(0.7f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (inv.payments.isNotEmpty()) {
                            item {
                                Text(
                                    "پرداخت‌ها:",
                                    color = Color.White.copy(0.6f),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            items(inv.payments) { p ->
                                GlassCard3D {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                CurrencyUtils.formatCurrency(p.amount),
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text("${p.paymentMethod ?: "-"} | ${p.createdAt?.let { DateUtils.toPersianDigits(DateUtils.gregorianToJalali(it.take(10))) } ?: "-"}",
                                                color = Color.White.copy(0.5f),
                                                style = MaterialTheme.typography.labelSmall)
                                            p.notes?.let {
                                                Text(
                                                    it,
                                                    color = Color.White.copy(0.4f),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                        Box(
                                            Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (p.status == "approved") Color(0xFF81C784).copy(0.2f) else GoldPrimary.copy(0.2f))
                                                .padding(
                                                    horizontal = 8.dp,
                                                    vertical = 4.dp
                                                )
                                        ) {
                                            Text(
                                                when (p.status) {
                                                    "approved" -> "تایید شده"
                                                    "pending"  -> "در انتظار"
                                                    "rejected" -> "رد شده"
                                                    else       -> p.status
                                                },
                                                color = if (p.status == "approved") Color(0xFF81C784) else GoldPrimary,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item { Spacer(Modifier.height(80.dp)) }
                    }

                    // دکمه شناور پرداخت
                    if (inv.remainingAmount > 0) {
                        FloatingActionButton(
                            onClick = { showPaymentDialog = true },
                            containerColor = GoldPrimary,
                            contentColor = Color(0xFF1A0533),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Payment,
                                "پرداخت"
                            )
                        }
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

            // دیالوگ‌ها
            if (showPaymentDialog && invoice != null) {
                PaymentDialog(
                    playerId = invoice!!.playerId,
                    invoiceId = invoice!!.id,
                    defaultAmount = invoice!!.remainingAmount,
                    onDismiss = { showPaymentDialog = false },
                    onSuccess = {
                        showPaymentDialog = false
                        load()
                    })
            }

            if (showAddItemDialog && invoice != null) {
                InvoiceItemDialog(
                    invoiceId = invoice!!.id,
                    existingItem = null,
                    onDismiss = { showAddItemDialog = false },
                    onSuccess = {
                        showAddItemDialog = false
                        load()
                    })
            }

            editingItem?.let { item ->
                InvoiceItemDialog(
                    invoiceId = invoice!!.id,
                    existingItem = item,
                    onDismiss = { editingItem = null },
                    onSuccess = {
                        editingItem = null
                        load()
                    })
            }

            showDeleteConfirm?.let { item ->
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = null },
                    containerColor = Color(0xFF1A0533),
                    title = {
                        Text(
                            "حذف آیتم؟",
                            color = Color.White
                        )
                    },
                    text = {
                        Text(
                            "آیا از حذف '${item.title}' مطمئن هستید؟",
                            color = Color.White.copy(0.7f)
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch {
                                repo.deleteItem(item.id)
                                showDeleteConfirm = null
                                load()
                            }
                        }) {
                            Text(
                                "حذف",
                                color = Color(0xFFFF8A80)
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = null }) {
                            Text(
                                "انصراف",
                                color = Color.White.copy(0.6f)
                            )
                        }
                    })
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

@Composable
private fun InvoiceItemDialog(
    invoiceId: Int,
    existingItem: InvoiceItem?,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.invoiceRepository
    val scope = rememberCoroutineScope()

    var title by remember {
        mutableStateOf(
            existingItem?.title
                    ?: ""
        )
    }
    var amount by remember {
        mutableStateOf(
            existingItem?.amount?.toString()
                    ?: ""
        )
    }
    var quantity by remember {
        mutableStateOf(
            existingItem?.quantity?.toString()
                    ?: "1"
        )
    }
    var description by remember {
        mutableStateOf(
            existingItem?.description
                    ?: ""
        )
    }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A0533),
        title = {
            Text(
                if (existingItem == null) "افزودن آیتم" else "ویرایش آیتم",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "عنوان",
                    placeholder = "مثلاً شهریه آبان"
                )
                GlassTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() } },
                    label = "مبلغ واحد (تومان)"
                )
                GlassTextField(
                    value = quantity,
                    onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                    label = "تعداد"
                )
                GlassTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "توضیحات (اختیاری)"
                )
                if (error != null) Text(
                    error!!,
                    color = Color(0xFFFF8A80),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !loading,
                onClick = {
                    val amt = amount.toLongOrNull()
                            ?: 0L
                    val qty = quantity.toIntOrNull()
                            ?: 1
                    if (title.isBlank() || amt <= 0) {
                        error = "عنوان و مبلغ الزامی است"
                        return@TextButton
                    }
                    loading = true
                    scope.launch {
                        val result = if (existingItem == null) {
                            repo.addItem(
                                invoiceId,
                                AddInvoiceItemRequest(
                                    title,
                                    "manual",
                                    amt,
                                    qty,
                                    description.takeIf { it.isNotBlank() })
                            )
                        } else {
                            repo.updateItem(
                                existingItem.id,
                                UpdateInvoiceItemRequest(
                                    title,
                                    amt,
                                    qty,
                                    description.takeIf { it.isNotBlank() })
                            )
                        }
                        loading = false
                        when (result) {
                            is NetworkResult.Success -> onSuccess()
                            is NetworkResult.Error   -> error = result.message
                            else                     -> {}
                        }
                    }
                }) {
                Text(
                    if (loading) "در حال ذخیره..." else "ذخیره",
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "انصراف",
                    color = Color.White.copy(0.6f)
                )
            }
        })
}
