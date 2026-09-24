package com.khz.malekadmin.ui.players

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.khz.malekadmin.core.util.CurrencyUtils
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassSearchField
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary

enum class PlayerFinanceFilter(val label: String) {
    ALL("همه"),
    DEBTOR("بدهکار"),
    SETTLED("تسویه کرده"),
    ACTIVE("فعال"),
    INACTIVE("غیرفعال")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerListScreen(
    onPlayerClick: (Int) -> Unit,
    onAddPlayer: () -> Unit,
    onChat: (Int) -> Unit = {}
) {
    val viewModel: PlayerListViewModel = appViewModel()
    val state by viewModel.state.collectAsState()

    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(PlayerFinanceFilter.ALL) }

    LaunchedEffect(Unit) { viewModel.load() }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.load(silent = true)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "بازیکنان",
                actions = {
                    IconButton(onClick = onAddPlayer) {
                        Icon(
                            Icons.Default.PersonAdd,
                            "افزودن بازیکن",
                            tint = GoldPrimary
                        )
                    }
                })
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            GlassSearchField(
                value = query,
                onValueChange = { query = it },
                label = "جستجوی بازیکن"
            )

            Spacer(Modifier.height(12.dp))

            // فیلترهای مالی و وضعیت
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(PlayerFinanceFilter.values()) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                filter.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (filter) {
                                PlayerFinanceFilter.DEBTOR  -> Color(0xFFFF8A80).copy(0.2f)
                                PlayerFinanceFilter.SETTLED -> Color(0xFF81C784).copy(0.2f)
                                else                        -> GoldPrimary.copy(0.2f)
                            },
                            selectedLabelColor = when (filter) {
                                PlayerFinanceFilter.DEBTOR  -> Color(0xFFFF8A80)
                                PlayerFinanceFilter.SETTLED -> Color(0xFF81C784)
                                else                        -> GoldPrimary
                            }
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            when (val s = state) {
                is PlayerListState.Loading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GoldPrimary)
                    }
                }

                is PlayerListState.Error   -> {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        GlassCard3D(glowColor = Color(0x66A50044)) {
                            Text(
                                s.message,
                                color = Color.White.copy(0.85f),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        GlassButton(
                            text = "تلاش مجدد",
                            onClick = { viewModel.load() })
                    }
                }

                is PlayerListState.Success -> {
                    // آمار کلی
                    val totalDebt = s.players.filter { it.balance?.isDebtor == true }
                        .sumOf {
                            it.balance?.debt
                                    ?: 0
                        }
                    val debtorCount = s.players.count { it.balance?.isDebtor == true }
                    val settledCount = s.players.count {
                        (it.balance?.totalInvoiced
                                ?: 0) > 0 && it.balance?.isDebtor == false
                    }

                    if (s.players.isNotEmpty()) {
                        GlassCard3D {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        "$debtorCount بدهکار",
                                        color = Color(0xFFFF8A80),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        CurrencyUtils.formatCurrency(totalDebt),
                                        color = Color.White.copy(0.6f),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "$settledCount تسویه",
                                        color = Color(0xFF81C784),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        "${s.players.size} کل بازیکنان",
                                        color = Color.White.copy(0.5f),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    var filtered = s.players

                    // فیلتر جستجو
                    if (query.isNotBlank()) {
                        filtered = filtered.filter { p ->
                            p.fullName.contains(
                                query,
                                ignoreCase = true
                            ) || p.nationalCode?.contains(query) == true
                        }
                    }

                    // فیلتر مالی
                    filtered = when (selectedFilter) {
                        PlayerFinanceFilter.ALL      -> filtered
                        PlayerFinanceFilter.DEBTOR   -> filtered.filter { it.balance?.isDebtor == true }
                        PlayerFinanceFilter.SETTLED  -> filtered.filter {
                            (it.balance?.totalInvoiced
                                    ?: 0) > 0 && it.balance?.isDebtor == false
                        }

                        PlayerFinanceFilter.ACTIVE   -> filtered.filter { it.status == "active" }
                        PlayerFinanceFilter.INACTIVE -> filtered.filter { it.status != "active" }
                    }

                    if (filtered.isEmpty()) {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "بازیکنی یافت نشد",
                                    color = Color.White.copy(0.6f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    when (selectedFilter) {
                                        PlayerFinanceFilter.DEBTOR  -> "هیچ بازیکن بدهکاری وجود ندارد"
                                        PlayerFinanceFilter.SETTLED -> "هیچ بازیکن تسویه کرده‌ای وجود ندارد"
                                        else                        -> "بازیکن جدید اضافه کنید یا فیلتر را تغییر دهید"
                                    },
                                    color = Color.White.copy(0.4f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    } else {
                        Text(
                            "${filtered.size} بازیکن",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(0.5f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(
                                filtered,
                                key = { it.id }) { player ->
                                PlayerRow(
                                    player = player,
                                    onClick = { onPlayerClick(player.id) },
                                    onToggle = { viewModel.toggleStatus(player) },
                                    onChat = { userId -> onChat(userId) })
                            }
                        }
                    }
                }
            }
        }
    }
}
