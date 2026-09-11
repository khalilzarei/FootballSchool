package com.khz.footballschool.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassSectionTitle
import com.khz.footballschool.ui.components.GlassTextField
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.ErrorGlow
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val vm: SettingsViewModel = appViewModel()
    val settings by vm.settings.collectAsState()
    val edits by vm.edits.collectAsState()
    val loading by vm.loading.collectAsState()

    // دسترسی به AuthRepository برای خروج
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val authRepository = container.authRepository
    val scope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var loggingOut by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "تنظیمات",
                onBack = onBack
            )
        }) { padding ->
        if (loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else {
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item { GlassSectionTitle("تنظیمات سیستم") }

                    items(settings) { s ->
                        GlassCard3D {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    s.key,
                                    color = GoldPrimary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                GlassTextField(
                                    value = edits[s.key]
                                            ?: s.value,
                                    onValueChange = {
                                        vm.edit(
                                            s.key,
                                            it
                                        )
                                    },
                                    label = s.description
                                            ?: s.key
                                )
                            }
                        }
                    }

                    // ─── بخش خروج از حساب ───
                    item {
                        Spacer(Modifier.height(8.dp))
                        GlassSectionTitle("حساب کاربری")
                    }
                    item {
                        GlassCard3D {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "خروج از حساب کاربری",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    "با خروج از حساب، برای ورود مجدد باید نام کاربری و رمز عبور خود را وارد کنید.",
                                    color = Color.White.copy(0.6f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                GlassButton(
                                    text = "خروج از حساب",
                                    onClick = { showLogoutDialog = true },
                                    primary = false,
                                    loading = loggingOut,
                                    enabled = !loggingOut,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // دکمه ذخیره تنظیمات
                GlassButton(
                    text = "ذخیره تنظیمات",
                    onClick = { vm.save() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }

    // ─── دیالوگ تأیید خروج ───
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    Icons.Default.Logout,
                    null,
                    tint = Color(0xFFFF8A80)
                )
            },
            title = {
                Text(
                    "خروج از حساب",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    "آیا مطمئن هستید که می‌خواهید از حساب خود خارج شوید؟",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        loggingOut = true
                        scope.launch {
                            authRepository.logout()
                            loggingOut = false
                            onLoggedOut()
                        }
                    }) {
                    Text(
                        "بله، خارج شو",
                        color = Color(0xFFFF8A80)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(
                        "انصراف",
                        color = GoldPrimary
                    )
                }
            },
            containerColor = Color(0xFF241040)
        )
    }
}