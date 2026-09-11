package com.khz.footballschool.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khz.footballschool.core.util.CurrencyUtils
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.InfoCard
import com.khz.footballschool.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen() {
    val vm: ReportsViewModel = appViewModel()
    val finance by vm.finance.collectAsState()
    val debts by vm.debts.collectAsState()
    val attendance by vm.attendance.collectAsState()
    val classes by vm.classes.collectAsState()
    val loading by vm.loading.collectAsState()
    var tab by remember { mutableStateOf(0) }
    val tabs = listOf("مالی", "بدهی‌ها", "حضور", "کلاس‌ها")

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { GlassTopBar(title = "گزارش‌ها") }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            TabRow(
                selectedTabIndex = tab,
                containerColor = Color.Transparent,
                contentColor = GoldPrimary
            ) {
                tabs.forEachIndexed { i, t ->
                    Tab(
                        selected = tab == i,
                        onClick = { tab = i },
                        text = { Text(t) }
                    )
                }
            }
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GoldPrimary)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (tab) {
                        0 -> finance?.let { f ->
                            item {
                                GlassCard3D {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        ReportLine(
                                            "کل فاکتورها",
                                            CurrencyUtils.formatCurrency(f.totalInvoiced)
                                        )
                                        ReportLine(
                                            "پرداخت‌شده",
                                            CurrencyUtils.formatCurrency(f.totalPaid)
                                        )
                                        ReportLine(
                                            "در انتظار",
                                            CurrencyUtils.formatCurrency(f.totalPending)
                                        )
                                        ReportLine(
                                            "کل بدهی",
                                            CurrencyUtils.formatCurrency(f.totalDebt)
                                        )
                                        ReportLine(
                                            "درآمد ماهانه",
                                            CurrencyUtils.formatCurrency(f.monthlyRevenue)
                                        )
                                        ReportLine(
                                            "درآمد امروز",
                                            CurrencyUtils.formatCurrency(f.dailyRevenue)
                                        )
                                    }
                                }
                            }
                        }
                        1 -> items(debts) { d ->
                            InfoCard(
                                title = d.playerName,
                                subtitle = "سررسید گذشته: ${CurrencyUtils.formatCurrency(d.overdueDebt)}",
                                trailing = CurrencyUtils.formatCurrency(d.totalDebt)
                            )
                        }
                        2 -> items(attendance) { a ->
                            InfoCard(
                                title = a.playerName,
                                subtitle = "حاضر: ${a.presentCount} | غایب: ${a.absentCount} | موجه: ${a.excusedCount}",
                                trailing = "${a.attendanceRate.toInt()}٪"
                            )
                        }
                        3 -> items(classes) { c ->
                            InfoCard(
                                title = c.classTitle,
                                subtitle = "مربی: ${c.coachName ?: "-"} | ثبت‌نام: ${c.enrolledCount}/${c.capacity ?: "-"}",
                                trailing = CurrencyUtils.formatCurrency(c.totalRevenue)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.White.copy(0.6f), style = MaterialTheme.typography.bodyMedium)
        Text(value, color = GoldPrimary, style = MaterialTheme.typography.titleSmall)
    }
}