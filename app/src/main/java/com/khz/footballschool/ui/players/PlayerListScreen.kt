package com.khz.footballschool.ui.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassSearchField
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerListScreen(
    onPlayerClick: (Int) -> Unit,
    onAddPlayer: () -> Unit,

    // فقط userId سرپرست برای ساخت/پیدا کردن اتاق خصوصی
    onChat: (Int) -> Unit = {}
) {
    val viewModel: PlayerListViewModel = appViewModel()
    val state by viewModel.state.collectAsState()

    var query by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    // هر بار که صفحه دوباره بالا بیاید (مثلاً بعد از افزودن/ویرایش بازیکن)
    // لیست به‌صورت خاموش (بدون اسپینر) تازه‌بارگذاری شود
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.load(silent = true)
            }
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
                    IconButton(
                        onClick = onAddPlayer
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "افزودن بازیکن",
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

            // ═════════════════════════════════════════
            // جستجو
            // ═════════════════════════════════════════

            GlassSearchField(
                value = query,
                onValueChange = {
                    query = it
                },
                label = "جستجوی بازیکن"
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // ═════════════════════════════════════════
            // محتوای اصلی
            // ═════════════════════════════════════════

            when (val s = state) {

                // ─────────────────────────────────────
                // Loading
                // ─────────────────────────────────────

                is PlayerListState.Loading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = GoldPrimary
                        )
                    }
                }

                // ─────────────────────────────────────
                // Error
                // ─────────────────────────────────────

                is PlayerListState.Error   -> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        GlassCard3D(
                            glowColor = Color(0x66A50044),
                        ) {
                            Text(
                                text = s.message,
                                color = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        GlassButton(
                            text = "تلاش مجدد",
                            onClick = {
                                viewModel.load()
                            })
                    }
                }

                // ─────────────────────────────────────
                // Success
                // ─────────────────────────────────────

                is PlayerListState.Success -> {

                    val filteredPlayers = if (query.isBlank()) {

                        s.players

                    } else {

                        s.players.filter { player ->

                            player.fullName.contains(
                                query,
                                ignoreCase = true
                            ) || player.nationalCode?.contains(
                                query
                            ) == true
                        }
                    }

                    // ─────────────────────────────────
                    // Empty
                    // ─────────────────────────────────

                    if (filteredPlayers.isEmpty()) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = "بازیکنی یافت نشد",
                                    color = Color.White.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "بازیکن جدید اضافه کنید یا عبارت جستجو را تغییر دهید",
                                    color = Color.White.copy(alpha = 0.4f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                    } else {

                        // ─────────────────────────────
                        // Result count
                        // ─────────────────────────────

                        Text(
                            text = "${filteredPlayers.size} بازیکن",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.padding(
                                bottom = 8.dp
                            )
                        )

                        // ─────────────────────────────
                        // Players
                        // ─────────────────────────────

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            items(
                                items = filteredPlayers,
                                key = { it.id }) { player ->

                                PlayerRow(
                                    player = player,

                                    onClick = {
                                        onPlayerClick(
                                            player.id
                                        )
                                    },

                                    onToggle = {
                                        viewModel.toggleStatus(
                                            player
                                        )
                                    },

                                    // فقط userId سرپرست
                                    onChat = { userId ->
                                        onChat(userId)
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}