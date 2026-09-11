package com.khz.footballschool.ui.attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(sessionId: Int, classId: Int, onBack: () -> Unit) {
    val vm: AttendanceViewModel = appViewModel()
    val rows by vm.rows.collectAsState()
    val loading by vm.loading.collectAsState()
    val saved by vm.saved.collectAsState()

    LaunchedEffect(sessionId) { vm.load(sessionId, classId) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "حضور و غیاب",
                onBack = onBack,
                actions = {
                    TextButton(onClick = { vm.save(sessionId) }) {
                        Text("ثبت", color = GoldPrimary)
                    }
                }
            )
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(rows) { row ->
                    GlassCard3D {
                        Column {
                            Text(
                                row.playerName,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatusChip(
                                    "حاضر",
                                    "present",
                                    row,
                                    vm
                                )
                                StatusChip(
                                    "غایب",
                                    "absent",
                                    row,
                                    vm
                                )
                                StatusChip(
                                    "موجه",
                                    "excused",
                                    row,
                                    vm
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = row.isBillable,
                                    onCheckedChange = { vm.toggleBillable(row.playerId) })
                                Text(
                                    "مشمول شهریه",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (saved) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {},
            confirmButton = { TextButton(onClick = onBack) { Text("باشه") } },
            title = { Text("ثبت شد") },
            text = { Text("حضور و غیاب با موفقیت ثبت شد.") }
        )
    }
}

@Composable
private fun StatusChip(label: String, status: String, row: AttendanceRow, vm: AttendanceViewModel) {
    FilterChip(
        selected = row.status == status,
        onClick = { vm.setStatus(row.playerId, status) },
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GoldPrimary.copy(alpha = 0.3f),
            selectedLabelColor = GoldPrimary,
            labelColor = Color.White.copy(0.7f),
            containerColor = Color.White.copy(0.06f)
        )
    )
}