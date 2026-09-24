package com.khz.malekadmin.ui.invoices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.FootballSchoolApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.core.util.CurrencyUtils
import com.khz.malekadmin.data.dto.request.CreatePaymentRequest
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassTextField
import com.khz.malekadmin.ui.components.JalaliDateField
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDialog(
    playerId: Int,
    invoiceId: Int?,
    defaultAmount: Long,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val paymentRepo = container.paymentRepository
    val scope = rememberCoroutineScope()

    var amountText by remember { mutableStateOf(defaultAmount.toString()) }
    var notes by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("cash") }
    var isApproved by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var paidAtDate by remember {
        mutableStateOf(
            LocalDate.now()
                .toString()
        )
    } // YYYY-MM-DD میلادی

    val methods = listOf(
        "cash" to "نقدی",
        "card_transfer" to "کارت به کارت",
        "pos" to "کارتخوان",
        "cheque" to "چک",
        "online" to "آنلاین",
        "other" to "سایر"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A0533),
        title = {
            Text(
                if (invoiceId != null) "ثبت پرداخت برای فاکتور #$invoiceId" else "ثبت پرداخت",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (defaultAmount > 0) {
                    Text(
                        "بدهی باقی‌مانده: ${CurrencyUtils.formatCurrency(defaultAmount)}",
                        color = Color(0xFFFF8A80),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                GlassTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    label = "مبلغ پرداختی (تومان)",
                    placeholder = "مثلاً 500000"
                )

                // روش پرداخت
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }) {
                    GlassTextField(
                        value = methods.find { it.first == selectedMethod }?.second
                                ?: selectedMethod,
                        onValueChange = {},
                        label = "روش پرداخت",
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        methods.forEach { (value, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedMethod = value
                                    expanded = false
                                })
                        }
                    }
                }

                // تاریخ پرداخت
                JalaliDateField(
                    label = "تاریخ پرداخت",
                    gregorianValue = paidAtDate,
                    onDatePicked = { paidAtDate = it })

                GlassTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "یادداشت (اختیاری)",
                    placeholder = "مثلاً پرداخت شهریه آبان"
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "تایید فوری (تسویه مستقیم)",
                        color = Color.White.copy(0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Switch(
                        checked = isApproved,
                        onCheckedChange = { isApproved = it })
                }

                if (error != null) {
                    GlassCard3D(glowColor = Color(0x44FF5252)) {
                        Text(
                            error!!,
                            color = Color(0xFFFF8A80),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !loading,
                onClick = {
                    val amount = amountText.toLongOrNull()
                            ?: 0L
                    if (amount <= 0) {
                        error = "مبلغ معتبر وارد کنید"
                        return@TextButton
                    }
                    loading = true
                    error = null
                    scope.launch {
                        val req = CreatePaymentRequest(
                            playerId = playerId,
                            invoiceId = invoiceId,
                            installmentId = null,
                            amount = amount,
                            paymentMethod = selectedMethod,
                            status = if (isApproved) "approved" else "pending",
                            receiptMediaId = null,
                            paidAt = paidAtDate,
                            notes = notes.takeIf { it.isNotBlank() })
                        when (val r = paymentRepo.createPayment(req)) {
                            is NetworkResult.Success -> {
                                loading = false
                                onSuccess()
                            }

                            is NetworkResult.Error   -> {
                                loading = false
                                error = r.message
                            }

                            else                     -> {
                                loading = false
                            }
                        }
                    }
                }) {
                Text(
                    if (loading) "در حال ثبت..." else "ثبت پرداخت",
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
