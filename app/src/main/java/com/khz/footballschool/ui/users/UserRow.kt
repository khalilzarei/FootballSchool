package com.khz.footballschool.ui.users

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khz.footballschool.domain.model.User
import com.khz.footballschool.ui.components.AvatarView
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.theme.GoldPrimary

@Composable
fun UserRow(
    user: User,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    onChat: (Int) -> Unit = {},       // ← جدید: ارسال به چت با userId
    onCall: (String) -> Unit = {}     // ← جدید: ارسال به تماس با شماره
) {
    val context = LocalContext.current

    GlassCard3D(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(
            topStart = 50.dp,
            bottomStart = 50.dp,
            topEnd = 10.dp,
            bottomEnd = 10.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ─── آواتار با رنگ نقش ───
            AvatarView(
                name = user.fullName,
                avatarUrl = user.avatarUrl,
                size = 80.dp,
                accentColor = when (user.role) {
                    "admin" -> Color(0xFFBA68C8)
                    "coach" -> Color(0xFF4FC3F7)
                    else    -> GoldPrimary
                }
            )

            Spacer(Modifier.width(16.dp))

            // ─── اطلاعات کاربر ───
            Column(Modifier.weight(1f)) {
                Text(
                    user.fullName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    user.mobile ?: user.nationalCode ?: "-",
                    modifier = Modifier.padding(vertical = 5.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f)
                )
                Text(
                    roleLabel(user.role),
                    style = MaterialTheme.typography.bodySmall,
                    color = when (user.role) {
                        "admin" -> Color(0xFFBA68C8)
                        "coach" -> Color(0xFF4FC3F7)
                        else    -> GoldPrimary
                    }
                )
            }

            Spacer(Modifier.width(8.dp))

            // ─── دکمه‌های چت و تماس ───
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // دکمه چت
                ActionIconButton(
                    icon = Icons.Default.Chat,
                    accentColor = Color(0xFF66BB6A),   // سبز
                    contentDescription = "پیام",
                    enabled = true,
                    onClick = { onChat(user.id) }
                )

                // دکمه تماس (فقط اگر موبایل داشت)
                val phone = user.mobile
                ActionIconButton(
                    icon = Icons.Default.Phone,
                    accentColor = Color(0xFF42A5F5),   // آبی
                    contentDescription = "تماس",
                    enabled = !phone.isNullOrBlank(),
                    onClick = {
                        if (!phone.isNullOrBlank()) {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$phone")
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                onCall(phone)
                            }
                        }
                    }
                )
            }

            Spacer(Modifier.width(8.dp))

            // ─── وضعیت و دکمه تغییر ───
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (user.status == "active") "فعال" else "غیرفعال",
                    color = if (user.status == "active") Color(0xFF81C784) else Color(0xFFFF8A80),
                    style = MaterialTheme.typography.labelMedium
                )
                TextButton(onClick = onToggle) {
                    Text(
                        if (user.status == "active") "غیرفعال" else "فعال",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary
                    )
                }
            }
        }
    }
}

/**
 * دکمه دایره‌ای آیکونی با افکت شیشه‌ای سه‌بعدی
 * قابل استفاده برای چت، تماس و سایر اکشن‌های سریع
 */
@Composable
private fun ActionIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    size: Int = 40
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        accentColor.copy(alpha = if (enabled) 0.85f else 0.25f),
                        accentColor.copy(alpha = if (enabled) 0.35f else 0.10f)
                    )
                )
            )
            .then(
                if (enabled) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size((size / 2).dp)
        )
    }
}

private fun roleLabel(role: String): String = when (role) {
    "admin" -> "مدیر سیستم"
    "coach" -> "مربی"
    else    -> role
}

@Preview(showBackground = false)
@Composable
fun UserRowPreview() {
    UserRow(
        user = User(
            id = 1,
            fullName = "علی محمدی",
            mobile = "09121234567",
            nationalCode = "1234567890",
            role = "coach",
            status = "active",
            mustChangePassword = false,
            failedLoginCount = 0,
            lockedUntil = null,
            lastLoginAt = "2026-09-11 10:00:00",
            createdBy = 1,
            createdAt = "2026-09-01",
            avatarUrl = null,
            updatedAt = "2026-09-11"
        ),
        onClick = {},
        onToggle = {},
        onChat = {},
        onCall = {}
    )
}