package com.khz.footballschool.ui.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.AvatarView
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTextField
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary

// رنگ وضعیت‌های حضور
private val PresentColor = Color(0xFF81C784)
private val AbsentColor = Color(0xFFFF8A80)
private val ExcusedColor = Color(0xFFFFD54F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    sessionId: Int,
    classId: Int,
    onBack: () -> Unit
) {
    val vm: AttendanceViewModel = appViewModel()
    val rows by vm.rows.collectAsState()
    val loading by vm.loading.collectAsState()
    val saving by vm.saving.collectAsState()
    val saved by vm.saved.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(sessionId) {
        vm.load(
            sessionId,
            classId
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "حضور و غیاب",
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
        } else if (error != null && rows.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassCard3D(glowColor = Color(0xFFA50044),) {
                    Column(
                        Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            error!!,
                            color = Color(0xFFFF8A80)
                        )
                        TextButton(onClick = {
                            vm.load(
                                sessionId,
                                classId
                            )
                        }) {
                            Text(
                                "تلاش مجدد",
                                color = GoldPrimary
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // ─── خلاصه‌ی وضعیت ───
                if (rows.isNotEmpty()) {
                    SummaryBar(
                        present = rows.count { it.status == "present" },
                        absent = rows.count { it.status == "absent" },
                        excused = rows.count { it.status == "excused" })
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        rows,
                        key = { it.playerId }) { row ->
                        PlayerAttendanceCard(
                            row = row,
                            onStatus = {
                                vm.setStatus(
                                    row.playerId,
                                    it
                                )
                            },
                            onNote = {
                                vm.setNote(
                                    row.playerId,
                                    it
                                )
                            })
                    }
                }

                // ─── دکمه‌ی ثبت پایین صفحه ───
                Column(Modifier.padding(16.dp)) {
                    GlassButton(
                        text = "ثبت حضور و غیاب",
                        onClick = { vm.save(sessionId) },
                        enabled = !saving && rows.isNotEmpty(),
                        loading = saving,
                        modifier = Modifier.fillMaxWidth()
                    )
                    error?.let {
                        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 6.dp))
                        Text(
                            it,
                            color = Color(0xFFFF8A80),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    if (saved) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = onBack) {
                    Text(
                        "باشه",
                        color = GoldPrimary
                    )
                }
            },
            title = { Text("ثبت شد") },
            text = { Text("حضور و غیاب با موفقیت ثبت شد.") },
            containerColor = Color(0xFF241040)
        )
    }
}

// ═════════════════════════════════════════════
// خلاصه — تعداد حاضر/غایب/موجه
// ═════════════════════════════════════════════
@Composable
private fun SummaryBar(
    present: Int,
    absent: Int,
    excused: Int
) {
    GlassCard3D(Modifier.fillMaxWidth(),) {
        Row(
            Modifier.fillMaxWidth()
                .padding(
                    vertical = 10.dp,
                    horizontal = 6.dp
                )
        ) {
            SummaryItem(
                "حاضر",
                present,
                PresentColor,
                Modifier.weight(1f)
            )
            SummaryItem(
                "غایب",
                absent,
                AbsentColor,
                Modifier.weight(1f)
            )
            SummaryItem(
                "موجه",
                excused,
                ExcusedColor,
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(0.6f)
        )
    }
}

// ═════════════════════════════════════════════
// کارت بازیکن — عکس و نام و وضعیت در یک ردیف
// ═════════════════════════════════════════════
@Composable
private fun PlayerAttendanceCard(
    row: AttendanceRow,
    onStatus: (String) -> Unit,
    onNote: (String) -> Unit
) {
    // فیلد توضیح به‌صورت پیش‌فرض بسته است؛ با دکمه باز و بسته می‌شود
    var noteOpen by remember(row.playerId) { mutableStateOf(false) }

    GlassCard3D(Modifier.fillMaxWidth(),) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ─── عکس + نام + وضعیت (یک ردیف) ───
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarView(
                    name = row.playerName,
                    avatarUrl = row.playerAvatar,
                    size = 44.dp
                )
                Text(
                    row.playerName,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    StatusPill(
                        "حاضر",
                        PresentColor,
                        row.status == "present"
                    ) { onStatus("present") }
                    StatusPill(
                        "غایب",
                        AbsentColor,
                        row.status == "absent"
                    ) { onStatus("absent") }
                    StatusPill(
                        "موجه",
                        ExcusedColor,
                        row.status == "excused"
                    ) { onStatus("excused") }
                }
            }

            // ─── دکمه نمایش/مخفی کردن توضیح ───
            NoteTogglePill(
                expanded = noteOpen,
                hasNote = !row.note.isNullOrBlank(),
                onToggle = { noteOpen = !noteOpen })

            // ─── توضیح (چندخطی) — فقط وقتی باز باشد ───
            if (noteOpen) {
                GlassTextField(
                    value = row.note
                            ?: "",
                    onValueChange = onNote,
                    label = "توضیح (اختیاری)",
                    singleLine = false,
                    modifier = Modifier.heightIn(min = 96.dp)
                )
            }
        }
    }
}

/** دکمه توضیحات — باز و بسته کردن فیلد توضیح هر بازیکن */
@Composable
private fun NoteTogglePill(
    expanded: Boolean,
    hasNote: Boolean,
    onToggle: () -> Unit
) {
    val tint = if (hasNote) GoldPrimary else Color.White.copy(0.65f)
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(if (hasNote) GoldPrimary.copy(0.18f) else Color.White.copy(0.06f))
            .clickable { onToggle() }
            .padding(
                horizontal = 12.dp,
                vertical = 5.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            "توضیحات",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = tint
        )
        Icon(
            if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            null,
            tint = tint,
            modifier = Modifier.size(14.dp)
        )
    }
}

/** دکمه‌ی وضعیت جمع‌وجور — کنار نام بازیکن جا می‌شود */
@Composable
private fun StatusPill(
    label: String,
    color: Color,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) color.copy(alpha = 0.30f) else Color.White.copy(alpha = 0.06f))
            .clickable { onSelect() }
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (selected) color else Color.White.copy(0.65f)
        )
    }
}