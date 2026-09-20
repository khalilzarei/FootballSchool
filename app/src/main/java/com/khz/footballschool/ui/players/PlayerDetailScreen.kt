package com.khz.footballschool.ui.players

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.khz.footballschool.core.util.DateUtils
import androidx.compose.ui.unit.dp
import com.khz.footballschool.core.util.CurrencyUtils
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.domain.model.GuardianPlayer
import com.khz.footballschool.ui.components.AvatarView
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(
    playerId: Int,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onAttachGuardian: () -> Unit,
    onChat: (Int) -> Unit
) {
    val vm: PlayerDetailViewModel = appViewModel()

    LaunchedEffect(playerId) { vm.load(playerId) }

    PlayerDetailContent(
        vm = vm,
        onBack = onBack,
        onEdit = onEdit,
        onAttachGuardian = onAttachGuardian,
        onChat = onChat
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerDetailContent(
    vm: PlayerDetailViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onAttachGuardian: () -> Unit,
    onChat: (Int) -> Unit
) {
    val player by vm.player.collectAsState()
    val guardians by vm.guardians.collectAsState()
    val balance by vm.balance.collectAsState()
    val invoices by vm.invoices.collectAsState()
    val loading by vm.loading.collectAsState()
    val context = LocalContext.current

    // رنگ accent بر اساس جنسیت
    val accentColor = when (player?.gender) {
        "female" -> Color(0xFFF06292)   // صورتی برای دختر
        else     -> Color(0xFF4FC3F7)       // آبی برای پسر
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = player?.fullName
                        ?: "جزئیات بازیکن",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            "ویرایش",
                            tint = GoldPrimary
                        )
                    }
                })
        },
        floatingActionButton = {
            // اگر بازیکن سرپرست دارد → آیکون مدیریت؛ در غیر این صورت → افزودن
            val hasGuardian = guardians.isNotEmpty()
            FloatingActionButton(
                onClick = onAttachGuardian,
                containerColor = GoldPrimary,
                contentColor = Color(0xFF1A0533)
            ) {
                Icon(
                    if (hasGuardian) Icons.Default.Edit else Icons.Default.PersonAdd,
                    if (hasGuardian) "مدیریت سرپرست" else "افزودن سرپرست"
                )
            }
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
            LazyColumn(
                Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                player?.let { p ->

                    // ═════════════════════════════════════════
                    // هدر (آواتار + نام + badges)
                    // ═════════════════════════════════════════
                    item {
                        GlassCard3D {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // آواتار بزرگ
                                AvatarView(
                                    name = p.fullName,
                                    avatarUrl = p.avatarPath,
                                    size = 150.dp,
                                    accentColor = accentColor
                                )

                                Spacer(Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {

                                        // نام کامل
                                        Text(
                                            p.fullName,
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )

                                        // کلاس فعلی
                                        if (p.currentClass != null) {
                                            Spacer(Modifier.height(10.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.School,
                                                    null,
                                                    tint = GoldPrimary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    p.currentClass!!.title,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = GoldPrimary
                                                )
                                            }
                                        }
                                    }
                                    // Badges
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        GenderBadge(
                                            gender = p.gender,
                                            accentColor = accentColor
                                        )
                                        StatusBadge(isActive = p.status == "active")
                                    }
                                }

                            }
                        }
                    }

                    // ═════════════════════════════════════════
                    // اطلاعات شخصی
                    // ═════════════════════════════════════════
                    item {
                        GlassCard3D {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                SectionTitle(
                                    title = "اطلاعات شخصی",
                                    icon = Icons.Default.Person,
                                    color = accentColor
                                )

                                Spacer(Modifier.height(8.dp))

                                InfoRowWithIcon(
                                    icon = Icons.Default.Badge,
                                    label = "کد ملی",
                                    value = p.nationalCode
                                            ?: "-"
                                )
                                InfoRowWithIcon(
                                    icon = Icons.Default.Cake,
                                    label = "تاریخ تولد",
                                    value = p.birthDate
                                            ?: "-"
                                )

                                InfoRowWithIcon(
                                    icon = Icons.Default.AccessTime,
                                    label = "سن",
                                    value = if (!p.birthDate.isNullOrBlank()) {
                                        DateUtils.calculateAgeFromGregorian(p.birthDate)
                                    } else {
                                        "-"
                                    }
                                )

                                InfoRowWithIcon(
                                    icon = if (p.gender == "female") Icons.Default.Female else Icons.Default.Male,
                                    label = "جنسیت",
                                    value = when (p.gender) {
                                        "male"   -> "پسر"
                                        "female" -> "دختر"
                                        else     -> "-"
                                    }
                                )
                                InfoRowWithIcon(
                                    icon = Icons.Default.Person,
                                    label = "وضعیت",
                                    value = if (p.status == "active") "فعال" else "غیرفعال",
                                    valueColor = if (p.status == "active") Color(0xFF81C784) else Color(0xFFFF8A80)
                                )
                            }
                        }
                    }

                    // ═════════════════════════════════════════
                    // یادداشت‌ها (فقط اگر وجود داشتند)
                    // ═════════════════════════════════════════
                    if (!p.medicalNotes.isNullOrBlank() || !p.notes.isNullOrBlank()) {
                        item {
                            GlassCard3D {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    SectionTitle(
                                        title = "یادداشت‌ها",
                                        icon = Icons.Default.Notes,
                                        color = Color(0xFFFF8A65)
                                    )

                                    if (!p.medicalNotes.isNullOrBlank()) {
                                        NoteItem(
                                            title = "یادداشت پزشکی",
                                            content = p.medicalNotes,
                                            color = Color(0xFFFF8A80)
                                        )
                                    }

                                    if (!p.notes.isNullOrBlank()) {
                                        NoteItem(
                                            title = "یادداشت عمومی",
                                            content = p.notes,
                                            color = Color(0xFF81C784)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ═════════════════════════════════════════
                // وضعیت مالی
                // ═════════════════════════════════════════
                balance?.let { b ->
                    item {
                        GlassCard3D(
                            glowColor = if (b.balance > 0) Color(0x44FF8A80) else Color(0x4481C784)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SectionTitle(
                                    title = "وضعیت مالی",
                                    icon = Icons.Default.AccountBalanceWallet,
                                    color = GoldPrimary
                                )

                                Spacer(Modifier.height(4.dp))

                                InfoRowWithIcon(
                                    icon = Icons.Default.Receipt,
                                    label = "کل فاکتور",
                                    value = CurrencyUtils.formatCurrency(b.totalInvoiced)
                                )
                                InfoRowWithIcon(
                                    icon = Icons.Default.AccountBalanceWallet,
                                    label = "پرداخت‌شده",
                                    value = CurrencyUtils.formatCurrency(b.totalPaid),
                                    valueColor = Color(0xFF81C784)
                                )

                                // خط جداکننده
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .padding(vertical = 4.dp)
                                        .background(Color.White.copy(0.1f))
                                )

                                InfoRowWithIcon(
                                    icon = Icons.Default.AccountBalanceWallet,
                                    label = "مانده",
                                    value = CurrencyUtils.formatCurrency(b.balance),
                                    valueColor = if (b.balance > 0) Color(0xFFFF8A80) else Color(0xFF81C784),
                                    isBold = true
                                )
                            }
                        }
                    }
                }

                // ═════════════════════════════════════════
                // سرپرست‌ها
                // ═════════════════════════════════════════
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionTitle(
                            title = "سرپرست‌ها (${guardians.size})",
                            icon = Icons.Default.People,
                            color = Color(0xFFFF8A65)
                        )
                    }
                }

                if (guardians.isEmpty()) {
                    item {
                        GlassCard3D {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.People,
                                        null,
                                        tint = Color.White.copy(0.3f),
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "هنوز سرپرستی ثبت نشده است",
                                        color = Color.White.copy(0.5f),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(guardians) { g ->
                        GuardianRow(
                            guardianPlayer = g,
                            onChat = onChat,
                            onCall = { phone ->
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:$phone")
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                }
                            })
                    }
                }

                // ═════════════════════════════════════════
                // فاکتورها
                // ═════════════════════════════════════════
                if (invoices.isNotEmpty()) {
                    item {
                        SectionTitle(
                            title = "فاکتورها (${invoices.size})",
                            icon = Icons.Default.Receipt,
                            color = Color(0xFFBA68C8)
                        )
                    }

                    items(invoices) { inv ->
                        GlassCard3D {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Receipt,
                                            null,
                                            tint = Color(0xFFBA68C8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            inv.invoiceType,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    InvoiceStatusBadge(status = inv.status)
                                }

                                Spacer(Modifier.height(4.dp))

                                InfoRowWithIcon(
                                    icon = Icons.Default.Receipt,
                                    label = "مبلغ کل",
                                    value = CurrencyUtils.formatCurrency(inv.totalAmount)
                                )
                                InfoRowWithIcon(
                                    icon = Icons.Default.AccountBalanceWallet,
                                    label = "پرداخت‌شده",
                                    value = CurrencyUtils.formatCurrency(inv.paidAmount),
                                    valueColor = Color(0xFF81C784)
                                )

                                // خط جداکننده
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color.White.copy(0.08f))
                                )

                                val remaining = inv.totalAmount - inv.paidAmount
                                InfoRowWithIcon(
                                    icon = Icons.Default.AccountBalanceWallet,
                                    label = "مانده",
                                    value = CurrencyUtils.formatCurrency(remaining),
                                    valueColor = if (remaining > 0) Color(0xFFFF8A80) else Color(0xFF81C784),
                                    isBold = true
                                )
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

// ═════════════════════════════════════════
// کامپوننت‌های کمکی
// ═════════════════════════════════════════

@Composable
private fun SectionTitle(
    title: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun InfoRowWithIcon(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = Color.White,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(0.5f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            label,
            color = Color.White.copy(0.65f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.45f)
        )
        Text(
            value,
            color = valueColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(0.55f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun StatusBadge(isActive: Boolean) {
    Box(
        modifier = Modifier
            .background(
                if (isActive) Color(0x3381C784) else Color(0x33FF8A80),
                RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 4.dp
            )
    ) {
        Text(
            if (isActive) "فعال" else "غیرفعال",
            color = if (isActive) Color(0xFF81C784) else Color(0xFFFF8A80),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GenderBadge(
    gender: String?,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                accentColor.copy(alpha = 0.20f),
                RoundedCornerShape(12.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 4.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (gender == "female") Icons.Default.Female else Icons.Default.Male,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                if (gender == "female") "دختر" else "پسر",
                color = accentColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InvoiceStatusBadge(status: String) {
    val (label, color) = when (status) {
        "paid"              -> "پرداخت شده" to Color(0xFF81C784)
        "partial"           -> "نیمه‌پرداخت" to Color(0xFFFF8A65)
        "pending", "unpaid" -> "پرداخت نشده" to Color(0xFFFF8A80)
        "cancelled"         -> "لغو شده" to Color.White.copy(0.5f)
        else                -> status to GoldPrimary
    }
    Box(
        modifier = Modifier
            .background(
                color.copy(alpha = 0.20f),
                RoundedCornerShape(10.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 4.dp
            )
    ) {
        Text(
            label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NoteItem(
    title: String,
    content: String,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color,
                        CircleShape
                    )
            )
            Text(
                title,
                color = color,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            content,
            color = Color.White.copy(0.85f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 14.dp)
        )
    }
}

@Composable
private fun GuardianRow(
    guardianPlayer: GuardianPlayer,
    onChat: (Int) -> Unit,
    onCall: (String) -> Unit
) {
    val phone = guardianPlayer.guardian?.displayMobile
            ?: guardianPlayer.guardian?.user?.mobile

    GlassCard3D(shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // آواتار سرپرست
            AvatarView(
                name = guardianPlayer.guardianName,
                avatarUrl = guardianPlayer.guardian?.user?.avatarUrl,
                size = 52.dp,
                accentColor = GoldPrimary
            )

            Spacer(Modifier.width(12.dp))

            // اطلاعات سرپرست
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        guardianPlayer.guardianName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (guardianPlayer.isPrimary) {
                        Box(
                            modifier = Modifier
                                .background(
                                    GoldPrimary.copy(alpha = 0.25f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(
                                    horizontal = 6.dp,
                                    vertical = 2.dp
                                )
                        ) {
                            Text(
                                "اصلی",
                                color = GoldPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    guardianPlayer.relationLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (!phone.isNullOrBlank()) {
                    Text(
                        phone,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.7f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // دکمه‌های چت و تماس
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // چت درون‌برنامه‌ای با کاربرِ سرپرست
                GuardianActionButton(
                    icon = Icons.Default.Chat,
                    accentColor = Color(0xFF66BB6A),
                    enabled = (guardianPlayer.guardian?.userId ?: 0) != 0,
                    onClick = {
                        val uid = guardianPlayer.guardian?.userId
                        if (uid != null && uid != 0) onChat(uid)
                    })

                GuardianActionButton(
                    icon = Icons.Default.Phone,
                    accentColor = Color(0xFF42A5F5),
                    enabled = !phone.isNullOrBlank(),
                    onClick = {
                        if (!phone.isNullOrBlank()) onCall(phone)
                    })
            }
        }
    }
}

@Composable
private fun GuardianActionButton(
    icon: ImageVector,
    accentColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
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
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}
