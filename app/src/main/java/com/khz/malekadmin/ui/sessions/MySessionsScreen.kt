package com.khz.malekadmin.ui.sessions

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.MalekAdminApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.core.util.DateUtils
import com.khz.malekadmin.core.util.ServerTime
import com.khz.malekadmin.data.dto.request.UpdateSessionRequest
import com.khz.malekadmin.domain.model.MyScheduleItem
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassDropdown
import com.khz.malekadmin.ui.components.GlassTextField
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

// روز هفته از تاریخ میلادی (برای نمایش)
private fun weekdayName(gregorian: String): String = try {
    val dow = java.time.LocalDate.parse(gregorian).dayOfWeek.value // Mon=1..Sun=7
    listOf(
        "دوشنبه",
        "سه‌شنبه",
        "چهارشنبه",
        "پنجشنبه",
        "جمعه",
        "شنبه",
        "یکشنبه"
    )[dow - 1]
} catch (_: Exception) {
    ""
}

private fun statusInfo(status: String): Pair<String, Color> = when (status) {
    "completed" -> "برگزار شد" to Color(0xFF81C784)
    "cancelled" -> "لغو شده" to Color(0xFFFF8A80)
    "makeup"    -> "جبرانی" to Color(0xFF4FC3F7)
    else        -> "در انتظار برگزاری" to GoldPrimary
}

/**
 * «جلسات من» — پنل بازیکن/سرپرست (و مربی):
 * جلسات کلاس‌های ثبت‌نام‌شده به همراه موضوع و یادداشتی که مربی ثبت کرده است.
 * داده از GET me/schedule (۱۴ روز گذشته تا آینده).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySessionsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val container = (context.applicationContext as MalekAdminApp).container
    val repo = container.clientRepository

    var sessions by remember { mutableStateOf<List<MyScheduleItem>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()

    // نال = هنوز کلاسی انتخاب نشده؛ -1 = همه کلاس‌ها
    var selectedClass by remember { mutableStateOf<Int?>(null) }

    // دیالوگ ویرایش توضیح جلسه
    var editTarget by remember { mutableStateOf<MyScheduleItem?>(null) }

    // گزینه‌های فیلتر: کلاس‌های موجود در جلسات + «همه» در انتها
    val classOptions = remember(sessions) {
        sessions.orEmpty()
            .map { it.classId to it.classTitle }
            .distinctBy { it.first }
            .sortedBy { it.second } + listOf(-1 to "همه کلاس‌ها")
    }
    val visibleSessions = remember(
        sessions,
        selectedClass
    ) {
        if (selectedClass == null || selectedClass == -1) sessions.orEmpty()
        else sessions.orEmpty()
            .filter { it.classId == selectedClass }
    }

    LaunchedEffect(reloadKey) {
        error = null
        when (val r = repo.getMySchedule()) {
            is NetworkResult.Success -> sessions = r.data
            is NetworkResult.Error   -> error = r.message
            else                     -> {}
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "جلسات من",
                onBack = onBack
            )
        }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // ─── اسپینر فیلتر کلاس ───
            GlassDropdown(
                label = "نمایش جلسات کلاس...",
                options = classOptions,
                selectedValue = selectedClass,
                onSelect = { selectedClass = it })
            Spacer(Modifier.height(12.dp))

            when {
                sessions == null && error == null -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }

                error != null                     -> GlassCard3D(Modifier.fillMaxWidth(),) {
                    Column(
                        Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            error
                                    ?: "",
                            color = Color(0xFFFF8A80)
                        )
                        Spacer(Modifier.height(12.dp))
                        GlassButton(
                            "تلاش مجدد",
                            onClick = {
                                error = null
                                sessions = null
                                reloadKey++
                            })
                    }
                }

                sessions.orEmpty()
                    .isEmpty()                    -> GlassCard3D(Modifier.fillMaxWidth(),) {
                    Column(
                        Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "هنوز جلسه‌ای برای شما ثبت نشده است",
                            color = Color.White.copy(0.6f)
                        )
                        Text(
                            "پس از ثبت‌نام در کلاس، جلسات اینجا نمایش داده می‌شوند",
                            color = Color.White.copy(0.4f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                selectedClass == null             -> GlassCard3D(Modifier.fillMaxWidth(),) {
                    Column(
                        Modifier.padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Event,
                            null,
                            tint = GoldPrimary.copy(0.8f),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "برای نمایش جلسات، ابتدا یک کلاس را انتخاب کنید",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Text(
                            "یا از انتهای لیست، «همه کلاس‌ها» را انتخاب کنید",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.5f)
                        )
                    }
                }

                visibleSessions.isEmpty()         -> GlassCard3D(Modifier.fillMaxWidth(),) {
                    Column(
                        Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "کلاس انتخاب‌شده در بازه‌ی فعلی جلسه‌ای ندارد",
                            color = Color.White.copy(0.6f)
                        )
                    }
                }

                else                              -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(
                        visibleSessions,
                        key = { it.id }) { s ->
                        MySessionCard(
                            s,
                            onEdit = { editTarget = s })
                    }
                }
            }
        }
    }

    // ─── دیالوگ ویرایش توضیح جلسه ───
    editTarget?.let { s ->
        MySessionEditDialog(
            item = s,
            readOnly = isBeforeToday(s.sessionDate),
            onDismiss = { editTarget = null },
            onSave = { topic, notes ->
                scope.launch {
                    val r = container.sessionRepository.updateSession(
                        s.id,
                        UpdateSessionRequest(
                            sessionDate = null,
                            startTime = null,
                            endTime = null,
                            location = null,
                            topic = topic.takeIf { it.isNotBlank() },
                            status = null,
                            notes = notes.takeIf { it.isNotBlank() }))
                    if (r is NetworkResult.Success) {
                        editTarget = null
                        reloadKey++
                    }
                }
            })
    }
}

@Composable
private fun MySessionCard(
    s: MyScheduleItem,
    onEdit: () -> Unit
) {
    val (statusLabel, statusColor) = statusInfo(s.status)

    GlassCard3D(Modifier.fillMaxWidth(),) {
        Column(
            Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // ─── سربرگ: تاریخ + وضعیت ───
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "${DateUtils.toPersianDigits(DateUtils.gregorianToJalali(s.sessionDate))} (${weekdayName(s.sessionDate)})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "ساعت ${s.startTimeShort} تا ${s.endTimeShort}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.6f)
                    )
                }
                Text(
                    statusLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // ─── کلاس و محل ───
            Text("کلاس: ${s.classTitle}" + (s.location?.takeIf { it.isNotBlank() }
                ?.let { " — $it" }
                    ?: ""),
                style = MaterialTheme.typography.bodyMedium,
                color = GoldPrimary)

            // ─── موضوع و یادداشت مربی ───
            s.topic?.takeIf { it.isNotBlank() }
                ?.let { topic ->
                    Text(
                        "موضوع: $topic",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            s.notes?.takeIf { it.isNotBlank() }
                ?.let { notes ->
                    Text(
                        "توضیح مربی: $notes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.65f)
                    )
                }

            if (s.topic.isNullOrBlank() && s.notes.isNullOrBlank() && s.status == "scheduled") {
                Text(
                    "موضوع و توضیح این جلسه هنوز توسط مربی ثبت نشده است",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(0.35f)
                )
            }

            // ─── ویرایش توضیح ───
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text(
                        "توضیح",
                        color = GoldPrimary
                    )
                }
            }
        }
    }
}

// ═════════════════════════════════════════════
// دیالوگ ویرایش توضیح جلسه (موضوع + یادداشت)
// ═════════════════════════════════════════════
@Composable
private fun MySessionEditDialog(
    item: MyScheduleItem,
    onDismiss: () -> Unit,
    onSave: (topic: String, notes: String) -> Unit,
    readOnly: Boolean = false
) {
    var topic by remember(item.id) {
        mutableStateOf(
            item.topic
                    ?: ""
        )
    }
    var notes by remember(item.id) {
        mutableStateOf(
            item.notes
                    ?: ""
        )
    }
    var saving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (readOnly) "توضیح جلسه (فقط مشاهده)" else "توضیح جلسه") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "${DateUtils.toPersianDigits(DateUtils.gregorianToJalali(item.sessionDate))} (${weekdayName(item.sessionDate)}) — ${item.classTitle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f)
                )
                if (readOnly) {
                    // جلسه گذشته: فقط نمایش
                    Text("موضوع: ${item.topic?.takeIf { it.isNotBlank() } ?: "—"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White)
                    Text("یادداشت: ${item.notes?.takeIf { it.isNotBlank() } ?: "—"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(0.75f))
                } else {
                    GlassTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = "موضوع جلسه",
                        singleLine = false
                    )
                    GlassTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = "یادداشت برای بازیکنان/سرپرستان",
                        singleLine = false
                    )
                }
            }
        },
        confirmButton = {
            if (!readOnly) {
                TextButton(
                    onClick = {
                        saving = true
                        onSave(
                            topic,
                            notes
                        )
                    },
                    enabled = !saving
                ) {
                    Text(
                        "ذخیره",
                        color = GoldPrimary
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    if (readOnly) "بستن" else "انصراف",
                    color = Color.White.copy(0.7f)
                )
            }
        },
        containerColor = Color(0xFF241040)
    )
}

/**
 * آیا تاریخ جلسه قبل از امروز است؟ (بر اساس ساعت سرور — مثل صفحه‌ی جلسات)
 */
private fun isBeforeToday(date: String): Boolean = try {
    java.time.LocalDate.parse(date)
        .isBefore(ServerTime.today())
} catch (_: Exception) {
    false
}