package com.khz.malekadmin.ui.players

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.core.util.CurrencyUtils
import com.khz.malekadmin.domain.model.FootballClass
import com.khz.malekadmin.domain.model.GuardianPlayer
import com.khz.malekadmin.domain.model.Player
import com.khz.malekadmin.domain.model.PlayerBalance
import com.khz.malekadmin.ui.components.AvatarView
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.theme.FootballSchoolTheme
import com.khz.malekadmin.ui.theme.GoldPrimary

/**
 * ردیف نمایش بازیکن در لیست با وضعیت مالی
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

    val primaryGuardian: GuardianPlayer? = player.guardians.firstOrNull { it.isPrimary }
            ?: player.guardians.firstOrNull()

    val chatUserId = player.userId.takeIf { it!! > 0 }
    val guardianPhone: String? = primaryGuardian?.guardian?.user?.mobile
            ?: primaryGuardian?.guardian?.emergencyPhone
    val guardianName = primaryGuardian?.guardianName

    val accentColor = if (player.gender == "female") {
        Color(0xFFF06292)
    } else {
        Color(0xFF4FC3F7)
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
        Column(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarView(
                    name = player.fullName,
                    avatarUrl = player.avatarPath,
                    size = 70.dp,
                    accentColor = accentColor
                )

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {

                    Text(
                        player.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,

                    )



                    Text(
                        "سن: ${player.age ?: "-"} | ${player.nationalCode ?: "-"}",
                        modifier = Modifier.padding(vertical = 2.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.6f)
                    )

                    Text(
                        "کلاس: ${player.currentClass?.title ?: "ثبت‌نام نشده"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GoldPrimary
                    )



                    if (guardianName != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.People,
                                null,
                                tint = Color.White.copy(0.5f),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${guardianName} (${primaryGuardian.relationLabel})",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(0.6f)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

                    ActionIconButton(
                        icon = Icons.Default.Phone,
                        accentColor = Color(0xFF42A5F5),
                        contentDescription = "تماس با سرپرست",
                        enabled = !guardianPhone.isNullOrBlank(),
                        onClick = {
                            val number = guardianPhone?.filter { it.isDigit() || it == '+' }
                            if (!number.isNullOrBlank()) {
                                try {
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


            }

            // نوار پایین برای فاکتور و بدهی (اگر بدهکار باشد)
            player.balance?.let { bal ->
                if (bal.isDebtor && bal.debt > 0) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFF8A80).copy(0.08f))
                            .padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                null,
                                tint = Color(0xFFFF8A80),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "بدهی:",
                                color = Color.White.copy(0.6f),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                CurrencyUtils.formatCurrency(bal.debt),
                                color = Color(0xFFFF8A80),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        if (bal.openInvoices > 0) {
                            Text(
                                "${bal.openInvoices} فاکتور باز",
                                color = Color.White.copy(0.5f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionIconButton(
    icon: ImageVector,
    accentColor: Color,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    size: Int = 38
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

@Preview(
    name = "PlayerRow - Debtor Player",
    showBackground = true,
    backgroundColor = 0xFF0F1419,
)
@Composable
private fun PlayerRowPreview() {
    FootballSchoolTheme {

        PlayerRow(
            player = mockPlayer(),
            onClick = {},
            onToggle = {})

    }
}

private fun mockPlayer(
    id: Int = 1,
    fullName: String = "علی محمدی",
    nationalCode: String? = "0012345678",
    age: Int? = 12,
    gender: String = "male",
    status: String = "active",
    avatarPath: String? = null,
    className: String? = "کلاس A - زیر ۱۳ سال",
    guardians: List<GuardianPlayer> = emptyList(),
    balance: PlayerBalance? = null,
    userId: Int? = 1001
) = Player(
    id = id,
    userId = userId,
    firstName = fullName.substringBefore(" "),
    lastName = fullName.substringAfter(
        " ",
        ""
    ),
    fullName = fullName,
    nationalCode = nationalCode,
    mobile = "09121112233",
    birthDate = "2012-05-10",
    age = age,
    gender = gender,
    status = status,
    medicalNotes = null,
    notes = null,
    avatarPath = avatarPath,
    createdBy = 1,
    createdAt = "2024-01-01",
    updatedAt = "2024-06-01",
    guardians = guardians,
    currentClass = null,
    balance = balance
)