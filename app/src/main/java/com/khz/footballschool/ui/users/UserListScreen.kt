package com.khz.footballschool.ui.users

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.khz.footballschool.ui.components.GlassChip3D
import com.khz.footballschool.ui.components.GlassSearchField
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    onUserClick: (Int) -> Unit,
    onAddUser: () -> Unit,
    onChat: (Int) -> Unit
) {
    val viewModel: UserListViewModel = appViewModel()
    val state by viewModel.state.collectAsState()
    val query by viewModel.query.collectAsState()
    val roleFilter by viewModel.roleFilter.collectAsState()

    // هر بار که صفحه دوباره بالا بیاید (مثلاً بعد از افزودن/ویرایش کاربر)
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
                title = "مدیریت کاربران",
                actions = {
                    IconButton(onClick = onAddUser) {
                        Icon(
                            Icons.Default.PersonAdd,
                            "افزودن کاربر",
                            tint = GoldPrimary
                        )
                    }
                })
        }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // ─── جستجو ───
            GlassSearchField(
                value = query,
                onValueChange = { viewModel.search(it) },
                label = "جستجوی کاربر"
            )
            Spacer(Modifier.height(12.dp))

            // ─── فیلتر نقش‌ها با GlassChip3D ───
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                GlassChip3D(
                    label = "همه",
                    selected = roleFilter == null,
                    onClick = { viewModel.setRoleFilter(null) })
                GlassChip3D(
                    label = "مدیر",
                    selected = roleFilter == "admin",
                    onClick = { viewModel.setRoleFilter("admin") },
                    accentColor = Color(0xFFBA68C8)  // بنفش
                )
                GlassChip3D(
                    label = "مربی",
                    selected = roleFilter == "coach",
                    onClick = { viewModel.setRoleFilter("coach") },
                    accentColor = Color(0xFF4FC3F7)  // آبی
                )
            }

            Spacer(Modifier.height(16.dp))

            // ─── محتوا ───
            when (val s = state) {
                is UserListState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }

                is UserListState.Error   -> Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    GlassCard3D(glowColor = Color(0x66A50044),) {
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

                is UserListState.Success -> {
                    if (s.users.isEmpty()) {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "کاربری یافت نشد",
                                color = Color.White.copy(0.6f)
                            )
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(s.users) { user ->
                                UserRow(
                                    user = user,
                                    onClick = { onUserClick(user.id) },
                                    onToggle = { viewModel.toggleStatus(user) },
                                    onChat = { onChat(user.id) }, // ← اضافه شد
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}