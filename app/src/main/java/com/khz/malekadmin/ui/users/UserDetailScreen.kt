package com.khz.malekadmin.ui.users

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.ui.components.AvatarView
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: Int,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val vm: UserDetailViewModel = appViewModel()
    val user by vm.user.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val resetResult by vm.resetPasswordResult.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) { vm.load(userId) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = user?.fullName
                        ?: "جزئیات کاربر",
                onBack = onBack,
                actions = {
                    androidx.compose.material3.IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            "ویرایش",
                            tint = GoldPrimary
                        )
                    }
                })
        }) { padding ->
        Box(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            when {
                // ─── لودینگ ───
                loading       -> {
                    CircularProgressIndicator(
                        color = GoldPrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // ─── خطا ───
                error != null -> {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GlassCard3D(glowColor = Color(0x66A50044),) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    null,
                                    tint = Color(0xFFFF8A80),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    error!!,
                                    color = Color.White.copy(0.85f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        GlassButton(
                            text = "تلاش مجدد",
                            onClick = { vm.load(userId) })
                    }
                }

                // ─── محتوای اصلی ───
                user != null  -> {
                    val u = user!!

                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // ═════════════════════════════════════════
                        // بخش آواتار و نام
                        // ═════════════════════════════════════════

                        GlassCard3D() {

                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),

                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AvatarView(
                                    name = u.fullName,
                                    avatarUrl = u.avatarUrl,
                                    size = 150.dp,
                                    accentColor = when (u.role) {
                                        "admin" -> Color(0xFFBA68C8)
                                        "coach" -> Color(0xFF4FC3F7)
                                        else    -> GoldPrimary
                                    }
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            u.fullName,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(
                                            modifier = Modifier.height(8.dp)
                                        )

                                        Text(
                                            roleLabel(u.role),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = GoldPrimary,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    StatusBadge(
                                        isActive = u.status == "active",
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }

                        // ═════════════════════════════════════════
                        // بخش اطلاعات کاربر
                        // ═════════════════════════════════════════
                        GlassCard3D() {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    "اطلاعات کاربر",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(4.dp))

                                InfoRow(
                                    "شماره موبایل",
                                    u.mobile
                                            ?: "-"
                                )
                                InfoRow(
                                    "کد ملی",
                                    u.nationalCode
                                            ?: "-"
                                )
                                InfoRow(
                                    "تاریخ ایجاد",
                                    u.createdAt
                                            ?: "-"
                                )
                                InfoRow(
                                    "آخرین ورود",
                                    u.lastLoginAt
                                            ?: "هنوز وارد نشده"
                                )
                            }
                        }

                        // ═════════════════════════════════════════
                        // بخش عملیات
                        // ═════════════════════════════════════════
                        GlassCard3D() {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    "عملیات",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(4.dp))
                                Row() {

                                    // ─── تغییر وضعیت ───
                                    GlassButton(
                                        text = if (u.status == "active") "غیرفعال کردن کاربر" else "فعال کردن کاربر",
                                        onClick = { showStatusDialog = true },
                                        primary = u.status != "active",
                                        modifier = Modifier.fillMaxWidth()
                                            .weight(1f)
                                    )
                                    Spacer(Modifier.width(16.dp))
                                    // ─── ریست رمز عبور ───
                                    GlassButton(
                                        text = "ریست رمز عبور",
                                        onClick = { showResetDialog = true },
                                        primary = false,
                                        modifier = Modifier.fillMaxWidth()
                                            .weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // ═════════════════════════════════════════
    // دیالوگ تأیید تغییر وضعیت
    // ═════════════════════════════════════════
    if (showStatusDialog) {
        val isActive = user?.status == "active"
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            icon = {
                Icon(
                    Icons.Default.PersonOff,
                    null,
                    tint = if (isActive) Color(0xFFFF8A80) else Color(0xFF81C784)
                )
            },
            title = {
                Text(
                    if (isActive) "غیرفعال کردن کاربر" else "فعال کردن کاربر",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    if (isActive) "آیا مطمئن هستید که می‌خواهید این کاربر را غیرفعال کنید؟ کاربر دیگر نمی‌تواند وارد سیستم شود."
                    else "آیا مطمئن هستید که می‌خواهید این کاربر را فعال کنید؟",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showStatusDialog = false
                        vm.toggleStatus()
                    }) {
                    Text(
                        if (isActive) "بله، غیرفعال کن" else "بله، فعال کن",
                        color = if (isActive) Color(0xFFFF8A80) else Color(0xFF81C784)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text(
                        "انصراف",
                        color = Color.White.copy(0.7f)
                    )
                }
            },
            containerColor = Color(0xFF241040)
        )
    }

    // ═════════════════════════════════════════
    // دیالوگ تأیید ریست رمز عبور
    // ═════════════════════════════════════════
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = {
                Icon(
                    Icons.Default.Key,
                    null,
                    tint = GoldPrimary
                )
            },
            title = {
                Text(
                    "ریست رمز عبور",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    "رمز عبور جدید به‌صورت تصادفی ساخته و پس از تأیید نمایش داده می‌شود. آیا ادامه می‌دهید؟",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        vm.resetPassword()
                    }) {
                    Text(
                        "بله، ریست کن",
                        color = GoldPrimary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(
                        "انصراف",
                        color = Color.White.copy(0.7f)
                    )
                }
            },
            containerColor = Color(0xFF241040)
        )
    }

    // ═════════════════════════════════════════
    // دیالوگ نمایش رمز عبور جدید
    // ═════════════════════════════════════════
    resetResult?.let { newPassword ->
        AlertDialog(
            onDismissRequest = { vm.clearResetPasswordResult() },
            icon = {
                Icon(
                    Icons.Default.Key,
                    null,
                    tint = GoldPrimary
                )
            },
            title = {
                Text(
                    "رمز عبور جدید",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "رمز عبور جدید کاربر:",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    GlassCard3D() {
                        Text(
                            newPassword,
                            color = GoldPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                        )
                    }

                    Text(
                        "این رمز را در جای امنی ذخیره کنید.",
                        color = Color(0xFFFF8A80),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { vm.clearResetPasswordResult() }) {
                    Text(
                        "باشه",
                        color = GoldPrimary
                    )
                }
            },
            containerColor = Color(0xFF241040)
        )
    }
}

// ═════════════════════════════════════════
// کامپوننت ردیف اطلاعات
// ═════════════════════════════════════════
@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = Color.White.copy(0.6f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            value,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f),
            textAlign = TextAlign.End
        )
    }
}

// ═════════════════════════════════════════
// کامپوننت وضعیت کاربر
// ═════════════════════════════════════════
@Composable
private fun StatusBadge(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isActive) Color(0x3381C784) else Color(0x33FF8A80),
                androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 4.dp
            )
    ) {
        Text(
            if (isActive) "فعال" else "غیرفعال",
            color = if (isActive) Color(0xFF81C784) else Color(0xFFFF8A80),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// ═════════════════════════════════════════
// تبدیل نقش به برچسب فارسی
// ═════════════════════════════════════════
private fun roleLabel(role: String): String = when (role) {
    "admin"    -> "مدیر سیستم"
    "coach"    -> "مربی"
    "guardian" -> "سرپرست"
    else       -> role
}
