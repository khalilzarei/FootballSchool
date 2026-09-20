package com.khz.footballschool.ui.players

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.People
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.footballschool.domain.model.GuardianPlayer
import com.khz.footballschool.domain.model.Player
import com.khz.footballschool.ui.components.AvatarView
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.theme.GoldPrimary

/**
 * ردیف نمایش بازیکن در لیست
 *
 * دکمه چت و تماس با سرپرست اصلی بازیکن کار می‌کند
 *
 * @param onChat ارسال (userId کاربرِ سرپرست اصلی، شناسه بازیکن) برای چت — player_id در اتاق ثبت می‌شود
 * @param onCall ارسال شماره موبایل سرپرست اصلی برای تماس
 */
@Composable
fun PlayerRow(
    player: Player,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    onChat: (Int) -> Unit = {},
    onCall: (String) -> Unit = {}
) {
    val context = LocalContext.current

    // ─── استخراج سرپرست اصلی از لیست GuardianPlayer ───
    val primaryGuardian: GuardianPlayer? = player.guardians.firstOrNull { it.isPrimary }
            ?: player.guardians.firstOrNull()

    // استخراج اطلاعات سرپرست اصلی
    // chatUserId = شناسه کاربر سرپرست (برای ساخت اتاق چت)
    val chatUserId = player.userId.takeIf { it!! > 0 }
    // شماره تماس سرپرست اصلی: موبایل کاربر سرپرست، در نبود آن شماره اضطراری
    // (نکته: displayMobile به‌جای نال «-» برمی‌گرداند و برای تماس نامعتبر است)
    val guardianPhone: String? = primaryGuardian?.guardian?.user?.mobile
            ?: primaryGuardian?.guardian?.emergencyPhone
    val guardianName = primaryGuardian?.guardianName

    // رنگ آواتار بر اساس جنسیت
    val accentColor = if (player.gender == "female") {
        Color(0xFFF06292)  // صورتی برای دختر
    } else {
        Color(0xFF4FC3F7)  // آبی برای پسر
    }

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
                .padding(
                    vertical = 8.dp,
                    horizontal = 4.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ─── آواتار بازیکن ───
            AvatarView(
                name = player.fullName,
                avatarUrl = player.avatarPath,
                size = 80.dp,
                accentColor = accentColor
            )

            Spacer(Modifier.width(16.dp))

            // ─── اطلاعات بازیکن ───
            Column(Modifier.weight(1f)) {
                Text(
                    player.fullName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    "سن: ${player.age ?: "-"} | ${player.nationalCode ?: "-"}",
                    modifier = Modifier.padding(vertical = 3.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f)
                )

                Text(
                    "کلاس: ${player.currentClass?.title ?: "ثبت‌نام نشده"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldPrimary
                )

                // ─── نمایش نام سرپرست اصلی ───
                if (guardianName != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.People,
                            null,
                            tint = Color.White.copy(0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${guardianName} (${primaryGuardian.relationLabel})",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(0.7f)
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            // ─── دکمه‌های چت و تماس با سرپرست ───
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // دکمه چت با سرپرست اصلی — همیشه کلیک را مصرف می‌کند
                ActionIconButton(
                    icon = Icons.Default.Chat,
                    accentColor = Color(0xFF66BB6A),
                    contentDescription = "پیام",
                    enabled = chatUserId != null,
                    onClick = {
                        Log.d(
                            "TAG",
                            "PlayerRow: $chatUserId"
                        )
                        if (chatUserId != null) {
                            onChat(chatUserId)
                        } else {
                            Toast.makeText(
                                context,
                                "برای این کاربر شناسه کاربری معتبر ثبت نشده است",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    })

                // دکمه تماس با سرپرست اصلی — دیالر پیش‌فرض گوشی؛ همیشه کلیک را مصرف می‌کند
                ActionIconButton(
                    icon = Icons.Default.Phone,
                    accentColor = Color(0xFF42A5F5),   // آبی
                    contentDescription = "تماس با سرپرست",
                    enabled = !guardianPhone.isNullOrBlank(),
                    onClick = {
                        // فقط ارقام و + — هر کاراکتر اضافه‌ای حذف می‌شود
                        val number = guardianPhone?.filter { it.isDigit() || it == '+' }
                        if (!number.isNullOrBlank()) {
                            try {
                                Toast.makeText(
                                    context,
                                    "تماس با $number",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$number")
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "باز کردن تماس ممکن نشد: ${e.message}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "شماره تماس سرپرست این بازیکن ثبت نشده است",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    })
            }

            Spacer(Modifier.width(8.dp))

            // ─── وضعیت و دکمه تغییر ───
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (player.status == "active") "فعال" else "غیرفعال",
                    color = if (player.status == "active") Color(0xFF81C784) else Color(0xFFFF8A80),
                    style = MaterialTheme.typography.labelMedium
                )
                TextButton(onClick = onToggle) {
                    Text(
                        if (player.status == "active") "غیرفعال" else "فعال",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldPrimary
                    )
                }
            }
        }
    }
}

/**
 * دکمه دایره‌ای آیکونی با افکت شیشه‌ای سه‌بعدی.
 *
 * نکته‌ی مهم: دکمه «همیشه» کلیک را مصرف می‌کند (حتی در حالت غیرفعال)
 * تا تاچ به کلیک کارت پدر سرریز نکند. enabled فقط ظاهر را کم‌رنگ می‌کند؛
 * منطق فعال/غیرفعال داخل onClick خود دکمه هندل می‌شود.
 */
@Composable
private fun ActionIconButton(
    icon: ImageVector,
    accentColor: Color,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    size: Int = 44
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
            .clickable { onClick() },
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