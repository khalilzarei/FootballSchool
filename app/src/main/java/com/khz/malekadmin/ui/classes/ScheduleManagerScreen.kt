package com.khz.malekadmin.ui.classes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.khz.malekadmin.FootballSchoolApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.core.util.DateUtils
import com.khz.malekadmin.domain.model.ClassSchedule
import com.khz.malekadmin.domain.model.FootballClass
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassDropdown
import com.khz.malekadmin.ui.components.GlassTextField
import com.khz.malekadmin.ui.components.TimeWheelField
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

val WEEKDAYS = listOf(
    1 to "شنبه",
    2 to "یکشنبه",
    3 to "دوشنبه",
    4 to "سه‌شنبه",
    5 to "چهارشنبه",
    6 to "پنجشنبه",
    7 to "جمعه"
)

/** نمایش فارسی روز هفته از مقدار عددی schedule.weekday */
fun weekdayLabel(weekday: String): String = weekday.toIntOrNull()
    ?.let { w -> WEEKDAYS.find { it.first == w }?.second }
        ?: "-"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleManagerScreen(
    classId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.classRepository
    val sessionRepo = container.sessionRepository
    val scope = rememberCoroutineScope()

    var schedules by remember { mutableStateOf<List<ClassSchedule>>(emptyList()) }
    var cls by remember { mutableStateOf<FootballClass?>(null) }
    var loading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editSchedule by remember { mutableStateOf<ClassSchedule?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    // ─── تولید خودکار جلسات ───
    var showGenerateConfirm by remember { mutableStateOf(false) }
    var generating by remember { mutableStateOf(false) }
    var generatedCount by remember { mutableStateOf<Int?>(null) }

    fun reload() {
        scope.launch {
            loading = true
            error = null
            when (val r = repo.getSchedules(classId)) {
                is NetworkResult.Success -> schedules = r.data
                is NetworkResult.Error   -> error = r.message
                else                     -> {}
            }
            loading = false
        }
        scope.launch {
            when (val r = repo.getClass(classId)) {
                is NetworkResult.Success -> cls = r.data
                else                     -> {}
            }
        }
    }

    LaunchedEffect(classId) { reload() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "برنامه هفتگی کلاس",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            Icons.Default.Add,
                            "افزودن برنامه",
                            tint = GoldPrimary
                        )
                    }
                })
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
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                error?.let {
                    GlassCard3D(glowColor = Color(0xFFA50044),) {
                        Column(
                            Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                it,
                                color = Color(0xFFFF8A80)
                            )
                            GlassButton(
                                text = "تلاش مجدد",
                                onClick = { reload() },
                                primary = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    return@Column
                }

                if (schedules.isEmpty()) {
                    GlassCard3D(Modifier.fillMaxWidth(),) {
                        Column(
                            Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "برنامه‌ای ثبت نشده است",
                                color = Color.White.copy(0.6f)
                            )
                            Text(
                                "برای تولید خودکار جلسات، حداقل یک برنامه هفتگی فعال لازم است",
                                color = Color.White.copy(0.4f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(
                            schedules,
                            key = { it.id }) { s ->
                            ScheduleCard(
                                schedule = s,
                                busy = busy,
                                onEdit = { editSchedule = s },
                                onToggle = {
                                    scope.launch {
                                        busy = true
                                        when (val r = repo.toggleScheduleStatus(
                                            s.id,
                                            !s.isActive
                                        )) {
                                            is NetworkResult.Success -> reload()
                                            is NetworkResult.Error   -> error = r.message
                                            else                     -> {}
                                        }
                                        busy = false
                                    }
                                })
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ─── دکمه تولید خودکار جلسات ───
                val hasActiveSchedule = schedules.any { it.isActive }
                val classEndDate = cls?.endDate

                if (classEndDate == null) {
                    Text(
                        "برای تولید خودکار جلسات، ابتدا تاریخ پایان کلاس را در ویرایش کلاس وارد کنید",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.5f)
                    )
                } else {
                    GlassButton(
                        text = "تولید خودکار جلسات تا پایان کلاس",
                        onClick = {
                            if (!hasActiveSchedule) {
                                error = "حداقل یک برنامه هفتگی فعال لازم است"
                            } else {
                                showGenerateConfirm = true
                            }
                        },
                        enabled = !generating && hasActiveSchedule,
                        loading = generating,
                        modifier = Modifier.fillMaxWidth()
                    )

                    generatedCount?.let { count ->
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (count > 0) "$count جلسه جدید تولید شد"
                            else "جلسه جدیدی تولید نشد (تمام جلسات این بازه از قبل موجودند)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (count > 0) Color(0xFF81C784) else GoldPrimary
                        )
                    }
                }
            }
        }
    }

    // ─── دیالوگ تأیید تولید جلسات ───
    if (showGenerateConfirm) {
        AlertDialog(
            onDismissRequest = { showGenerateConfirm = false },
            title = { Text("تولید خودکار جلسات") },
            text = {
                val from = maxOf(
                    cls?.startDate
                            ?: "",
                    java.time.LocalDate.now()
                        .toString()
                ).let { if (it.isNotEmpty()) DateUtils.gregorianToJalali(it) else "-" }
                val to = cls?.endDate?.let { DateUtils.gregorianToJalali(it) }
                        ?: "-"
                Text(
                    "جلسات بر اساس برنامه هفتگی فعال، از $from تا $to ساخته می‌شوند.\n\n" + "جلسات تکراری (همان روز و ساعت) به‌صورت خودکار رد می‌شوند."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showGenerateConfirm = false
                    scope.launch {
                        generating = true
                        when (val r = sessionRepo.generateSessionsForClass(classId)) {
                            is NetworkResult.Success -> generatedCount = r.data
                            is NetworkResult.Error   -> error = r.message
                            else                     -> {}
                        }
                        generating = false
                    }
                }) {
                    Text(
                        "تولید کن",
                        color = GoldPrimary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showGenerateConfirm = false }) {
                    Text(
                        "انصراف",
                        color = Color.White.copy(0.7f)
                    )
                }
            },
            containerColor = Color(0xFF241040)
        )
    }

    // ─── دیالوگ افزودن ───
    if (showAddDialog) {
        ScheduleDialog(
            title = "افزودن برنامه جدید",
            initial = null,
            onDismiss = { showAddDialog = false },
            onSave = { weekday, start, end, location ->
                scope.launch {
                    busy = true
                    when (val r = repo.createSchedule(
                        classId,
                        weekday,
                        start,
                        end,
                        location
                    )) {
                        is NetworkResult.Success -> {
                            showAddDialog = false
                            reload()
                        }

                        is NetworkResult.Error   -> error = r.message
                        else                     -> {}
                    }
                    busy = false
                }
            })
    }

    // ─── دیالوگ ویرایش ───
    editSchedule?.let { s ->
        ScheduleDialog(
            title = "ویرایش برنامه",
            initial = s,
            onDismiss = { editSchedule = null },
            onSave = { weekday, start, end, location ->
                scope.launch {
                    busy = true
                    when (val r = repo.updateSchedule(
                        s.id,
                        weekday,
                        start,
                        end,
                        location
                    )) {
                        is NetworkResult.Success -> {
                            editSchedule = null
                            reload()
                        }

                        is NetworkResult.Error   -> error = r.message
                        else                     -> {}
                    }
                    busy = false
                }
            })
    }
}

@Composable
private fun ScheduleCard(
    schedule: ClassSchedule,
    busy: Boolean,
    onEdit: () -> Unit,
    onToggle: () -> Unit
) {
    GlassCard3D(modifier = Modifier.fillMaxWidth(),) {
        Row(
            Modifier.fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    weekdayLabel(schedule.weekday),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${schedule.startTime} تا ${schedule.endTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GoldPrimary
                )
                schedule.location?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.6f)
                    )
                }
            }

            Text(
                if (schedule.isActive) "فعال" else "غیرفعال",
                color = if (schedule.isActive) Color(0xFF81C784) else Color(0xFFFF8A80),
                style = MaterialTheme.typography.labelMedium
            )

            IconButton(
                onClick = onEdit,
                enabled = !busy
            ) {
                Icon(
                    Icons.Default.Edit,
                    "ویرایش",
                    tint = GoldPrimary.copy(alpha = 0.8f)
                )
            }

            TextButton(
                onClick = onToggle,
                enabled = !busy
            ) {
                Text(
                    if (schedule.isActive) "غیرفعال" else "فعال",
                    color = GoldPrimary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

/**
 * دیالوگ افزودن/ویرایش برنامه هفتگی
 * با اعتبارسنجی فرمت ساعت (HH:mm) و شروع < پایان
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleDialog(
    title: String,
    initial: ClassSchedule?,
    onDismiss: () -> Unit,
    onSave: (weekday: Int, startTime: String, endTime: String, location: String?) -> Unit
) {
    var weekday by remember {
        mutableStateOf(
            initial?.weekday?.toIntOrNull()
                    ?: 1
        )
    }
    var startTime by remember {
        mutableStateOf(
            initial?.startTime
                    ?: "17:00"
        )
    }
    var endTime by remember {
        mutableStateOf(
            initial?.endTime
                    ?: "18:30"
        )
    }
    var location by remember {
        mutableStateOf(
            initial?.location
                    ?: ""
        )
    }
    var localError by remember { mutableStateOf<String?>(null) }

    fun validTime(t: String): Boolean = Regex("^([01]?\\d|2[0-3]):[0-5]\\d$").matches(t.trim())

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val s = startTime.trim()
                val e = endTime.trim()
                localError = when {
                    !validTime(s) -> "ساعت شروع باید با فرمت HH:mm باشد (مثلاً 17:00)"
                    !validTime(e) -> "ساعت پایان باید با فرمت HH:mm باشد (مثلاً 18:30)"
                    s >= e        -> "ساعت شروع باید قبل از ساعت پایان باشد"
                    else          -> null
                }
                if (localError == null) {
                    onSave(
                        weekday,
                        s,
                        e,
                        location.takeIf { it.isNotBlank() })
                }
            }) {
                Text(
                    "ذخیره",
                    color = GoldPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        },
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassDropdown(
                    label = "روز هفته",
                    options = WEEKDAYS,
                    selectedValue = weekday,
                    onSelect = { weekday = it })
                TimeWheelField(
                    label = "ساعت شروع",
                    value = startTime,
                    onTimePicked = { startTime = it })
                TimeWheelField(
                    label = "ساعت پایان",
                    value = endTime,
                    onTimePicked = { endTime = it })
                GlassTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = "مکان (اختیاری)"
                )
                localError?.let {
                    Text(
                        it,
                        color = Color(0xFFFF8A80),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        containerColor = Color(0xFF241040)
    )
}
