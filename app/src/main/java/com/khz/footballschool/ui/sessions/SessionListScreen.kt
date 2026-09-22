package com.khz.footballschool.ui.sessions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.footballschool.core.util.DateUtils
import com.khz.footballschool.core.util.ServerTime
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.domain.model.Session
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassDropdown
import com.khz.footballschool.ui.components.GlassTextField
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary

/**
 * لیست جلسات — بر اساس کلاس انتخاب‌شده از اسپینر بالای صفحه.
 * تا زمانی که کلاسی انتخاب نشده باشد، به‌جای لیست، پیام «یک کلاس را انتخاب کنید» می‌آید.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionListScreen(onSessionClick: (sessionId: Int, classId: Int) -> Unit) {
    val vm: SessionListViewModel = appViewModel()
    val state by vm.state.collectAsState()
    val classes by vm.classes.collectAsState()
    val classesLoading by vm.classesLoading.collectAsState()
    val classesError by vm.classesError.collectAsState()
    val selectedClassId by vm.selectedClassId.collectAsState()

    // ─── دیالوگ توضیح جلسه ───
    var editTarget by remember { mutableStateOf<Session?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(title = "جلسات تمرین")
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ─── اسپینر انتخاب کلاس ───
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassDropdown(
                    label = "نمایش جلسات کلاس...",
                    options = classes.map { it.id to it.title },
                    selectedValue = selectedClassId,
                    onSelect = { vm.selectClass(it) }
                )

                classesError?.let {
                    Text(it, color = Color(0xFFFF8A80), style = MaterialTheme.typography.bodySmall)
                    GlassButton(
                        text = "تلاش مجدد",
                        onClick = { vm.loadClasses() },
                        primary = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            when (state) {
                is SessionListState.Idle -> Box(
                    Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard3D(Modifier.fillMaxWidth(),) {
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
                                "جلسات هر کلاس به‌صورت جداگانه نمایش داده می‌شود",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(0.5f)
                            )
                        }
                    }
                }

                is SessionListState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }

                is SessionListState.Error -> Box(
                    Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard3D(glowColor = Color(0xFFA50044),) {
                        Column(
                            Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                (state as SessionListState.Error).message,
                                color = Color(0xFFFF8A80)
                            )
                            TextButton(onClick = { vm.load() }) {
                                Text(
                                    "تلاش مجدد",
                                    color = GoldPrimary
                                )
                            }
                        }
                    }
                }

                is SessionListState.Success -> {
                    val sessions = (state as SessionListState.Success).sessions
                    if (sessions.isEmpty()) {
                        Box(
                            Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            GlassCard3D(Modifier.fillMaxWidth(),) {
                                Column(
                                    Modifier.padding(24.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        "این کلاس هنوز جلسه‌ای ندارد",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White
                                    )
                                    Text(
                                        "از بخش برنامه هفتگی کلاس می‌توانید جلسات را تا تاریخ پایان تولید کنید",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(0.5f)
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(sessions, key = { it.id }) { session ->
                                SessionCard(
                                    session = session,
                                    onAttendance = { onSessionClick(session.id, session.classId) },
                                    onEditTopic = { editTarget = session },
                                    onCancel = { vm.cancelSession(session.id) },
                                    onComplete = { vm.completeSession(session.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ─── دیالوگ توضیح جلسه (برای جلسات گذشته فقط-مشاهده) ───
    editTarget?.let { session ->
        SessionInfoDialog(
            session = session,
            readOnly = isBeforeToday(session.sessionDate),
            onDismiss = { editTarget = null },
            onSave = { topic, notes ->
                vm.updateSessionInfo(session.id, topic, notes) { ok ->
                    if (ok) editTarget = null
                }
            }
        )
    }
}

// ═════════════════════════════════════════════
// کارت جلسه — روز، تاریخ شمسی، ساعت، مکان، وضعیت
// ═════════════════════════════════════════════
@Composable
private fun SessionCard(
    session: Session,
    onAttendance: () -> Unit,
    onEditTopic: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    // جلسه‌ای که تاریخش گذشته: فقط نمایش — دکمه‌های ویرایش غیرفعال
    val isPast = remember(session.sessionDate) { isBeforeToday(session.sessionDate) }

    GlassCard3D(Modifier.fillMaxWidth(),) {
        Column(
            Modifier.padding(12.dp)
                .alpha(if (isPast) 0.55f else 1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ─── سربرگ: کلاس + وضعیت ───
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        session.classItem?.title
                                ?: "کلاس",
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                StatusBadge(session.status)
            }

            // ─── مشخصات: روز و تاریخ / ساعت / مکان / یادداشت ───
            InfoLine(
                Icons.Default.Event,
                sessionDateLabel(session.sessionDate)
            )
            InfoLine(
                Icons.Default.Schedule,
                "${session.startTime} تا ${session.endTime}"
            )
            session.location?.takeIf { it.isNotBlank() }
                ?.let {
                    InfoLine(
                        Icons.Default.LocationOn,
                        it
                    )
                }
            session.notes?.takeIf { it.isNotBlank() && it.trim() != "تولیدشده از برنامه هفتگی" }
                ?.let {
                    InfoLine(
                        Icons.Default.Notes,
                        it,
                        maxLines = 2
                    )
                }

            // ─── دکمه‌ها ───
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    4.dp,
                    Alignment.End
                )
            ) {
                if (isPast) {
                    // جلسه گذشته: فقط مشاهده — امکان ویرایش ندارد
                    Text(
                        "جلسه گذشته — فقط قابل مشاهده",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.45f),
                        modifier = Modifier.weight(1f)
                            .align(Alignment.CenterVertically)
                    )
                    TextButton(onClick = onEditTopic) {
                        Text(
                            "توضیح",
                            color = Color.White.copy(0.55f)
                        )
                    }
                } else {
                    if (session.status == "scheduled") {
                        TextButton(onClick = onCancel) {
                            Text(
                                "لغو",
                                color = Color(0xFFFF8A80)
                            )
                        }
                        TextButton(onClick = onComplete) {
                            Text(
                                "پایان",
                                color = GoldPrimary
                            )
                        }
                    }
                    TextButton(onClick = onAttendance) {
                        Text(
                            "حضور و غیاب",
                            color = GoldPrimary
                        )
                    }
                    TextButton(onClick = onEditTopic) {
                        Text(
                            "توضیح",
                            color = Color.White.copy(0.8f)
                        )
                    }
                }
            }
        }
    }
}

/** یک سطر مشخصات با آیکن طلایی */
@Composable
private fun InfoLine(icon: ImageVector, text: String, maxLines: Int = 1) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            null,
            tint = GoldPrimary.copy(0.8f),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(0.75f),
            maxLines = maxLines
        )
    }
}

/** نشان وضعیت جلسه با رنگ */
@Composable
private fun StatusBadge(status: String) {
    val (label, color) = when (status) {
        "completed" -> "برگزار شد" to Color(0xFF81C784)
        "cancelled" -> "لغو شد" to Color(0xFFFF8A80)
        "makeup" -> "جبرانی" to Color(0xFFFFD54F)
        else -> "برگزار می‌شود" to GoldPrimary
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * تاریخ جلسه به‌صورت «نام روز + تاریخ شمسی» — مثلاً «شنبه ۱۴۰۵/۰۷/۰۱»
 */
private fun sessionDateLabel(gregorian: String): String {
    val jalali = DateUtils.toPersianDigits(DateUtils.gregorianToJalali(gregorian))
    val dayName = try {
        when (java.time.LocalDate.parse(gregorian).dayOfWeek.value) {
            6 -> "شنبه"
            7 -> "یکشنبه"
            1 -> "دوشنبه"
            2 -> "سه‌شنبه"
            3 -> "چهارشنبه"
            4 -> "پنجشنبه"
            5 -> "جمعه"
            else -> ""
        }
    } catch (_: Exception) {
        ""
    }
    return if (dayName.isEmpty()) jalali else "$dayName $jalali"
}

@Composable
private fun SessionInfoDialog(
    session: Session,
    onDismiss: () -> Unit,
    onSave: (topic: String, notes: String) -> Unit,
    readOnly: Boolean = false
) {
    var topic by remember(session.id) { mutableStateOf(session.topic ?: "") }
    var notes by remember(session.id) { mutableStateOf(session.notes ?: "") }
    var saving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (readOnly) "توضیح جلسه (فقط مشاهده)" else "توضیح جلسه") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "${sessionDateLabel(session.sessionDate)} — ${session.classItem?.title ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f)
                )
                if (readOnly) {
                    // جلسه گذشته: فقط نمایش موضوع و یادداشت
                    Text(
                        "موضوع: ${session.topic?.takeIf { it.isNotBlank() } ?: "—"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        "یادداشت: ${session.notes?.takeIf { it.isNotBlank() } ?: "—"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(0.75f)
                    )
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
                        onSave(topic, notes)
                    },
                    enabled = !saving
                ) {
                    Text("ذخیره", color = GoldPrimary)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (readOnly) "بستن" else "انصراف", color = Color.White.copy(0.7f))
            }
        },
        containerColor = Color(0xFF241040)
    )
}

/**
 * آیا تاریخ جلسه قبل از امروز است؟ (جلسات گذشته فقط قابل مشاهده‌اند)
 */
private fun isBeforeToday(date: String): Boolean = try {
    // امروز بر اساس ساعت سرور است (نه ساعت گوشی) — در صورت خطای گوشی هم درست کار می‌کند
    java.time.LocalDate.parse(date).isBefore(ServerTime.today())
} catch (_: Exception) {
    false
}