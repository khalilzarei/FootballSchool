package com.khz.footballschool.ui.classes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.CreateScheduleRequest
import com.khz.footballschool.domain.model.ClassSchedule
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassDropdown
import com.khz.footballschool.ui.components.GlassTextField
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.ErrorGlow
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

private val WEEKDAYS = listOf(
    1 to "شنبه",
    2 to "یکشنبه",
    3 to "دوشنبه",
    4 to "سه‌شنبه",
    5 to "چهارشنبه",
    6 to "پنجشنبه",
    7 to "جمعه"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleManagerScreen(
    classId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.classRepository
    val scope = rememberCoroutineScope()

    var schedules by remember { mutableStateOf<List<ClassSchedule>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    fun reload() {
        scope.launch {
            loading = true
//            when (val r = repo.getSchedules(classId)) {
//                is NetworkResult.Success -> schedules = r.data
//                is NetworkResult.Error -> error = r.message
//                else -> {}
//            }
            loading = false
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
                    GlassCard3D {
                        Text(
                            it,
                            color = Color(0xFFFF8A80)
                        )
                    }
                }
                if (schedules.isEmpty()) {
                    GlassCard3D {
                        Text(
                            "برنامه‌ای ثبت نشده است",
                            color = Color.White.copy(0.6f)
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(schedules) { s ->
                            ScheduleCard(s)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddScheduleDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { weekday, start, end, location ->
                scope.launch {
//                    val r = repo.createSchedule(
//                        classId,
//                        CreateScheduleRequest(weekday, start, end, location, "active")
//                    )
//                    if (r is NetworkResult.Success) {
//                        showAddDialog = false
//                        reload()
//                    } else if (r is NetworkResult.Error) {
//                        error = r.message
//                    }
                }
            })
    }
}

@Composable
private fun ScheduleCard(schedule: ClassSchedule) {
    GlassCard3D(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    WEEKDAYS.find { it.first.toString() == schedule.weekday }?.second
                            ?: "-",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onAdd: (Int, String, String, String?) -> Unit
) {
    var weekday by remember { mutableStateOf(1) }
    var startTime by remember { mutableStateOf("17:00") }
    var endTime by remember { mutableStateOf("18:30") }
    var location by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onAdd(
                    weekday,
                    startTime,
                    endTime,
                    location.takeIf { it.isNotBlank() })
            }) {
                Text(
                    "افزودن",
                    color = GoldPrimary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        },
        title = { Text("افزودن برنامه جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassDropdown(
                    label = "روز هفته",
                    options = WEEKDAYS,
                    selectedValue = weekday,
                    onSelect = { weekday = it })
                GlassTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = "ساعت شروع"
                )
                GlassTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = "ساعت پایان"
                )
                GlassTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = "مکان (اختیاری)"
                )
            }
        },
        containerColor = Color(0xFF241040)
    )
}