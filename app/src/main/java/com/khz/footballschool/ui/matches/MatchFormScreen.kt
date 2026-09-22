package com.khz.footballschool.ui.matches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.khz.footballschool.data.dto.request.CreateMatchRequest
import com.khz.footballschool.data.dto.request.UpdateMatchRequest
import com.khz.footballschool.domain.model.AgeGroup
import com.khz.footballschool.domain.model.FootballClass
import com.khz.footballschool.ui.components.GlassBackground
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTextField
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.JalaliDateField
import com.khz.footballschool.ui.components.TimeWheelField
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchFormScreen(
    matchId: Int?,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val matchRepo = container.matchRepository
    val classRepo = container.classRepository
    val ageGroupRepo = container.ageGroupRepository
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var opponent by remember { mutableStateOf("") }
    var matchDate by remember { mutableStateOf("") }
    var matchTime by remember { mutableStateOf("17:00") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var matchType by remember { mutableStateOf("friendly") }
    var status by remember { mutableStateOf("planned") }

    var classes by remember { mutableStateOf<List<FootballClass>>(emptyList()) }
    var ageGroups by remember { mutableStateOf<List<AgeGroup>>(emptyList()) }
    var selectedClassId by remember { mutableStateOf<Int?>(null) }
    var selectedAgeGroupId by remember { mutableStateOf<Int?>(null) }

    var loading by remember { mutableStateOf(matchId != null) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var classExpanded by remember { mutableStateOf(false) }
    var ageGroupExpanded by remember { mutableStateOf(false) }

    val matchTypes = listOf(
        "friendly" to "دوستانه",
        "official" to "رسمی",
        "league" to "لیگ",
        "cup" to "جام",
        "festival" to "جشنواره",
        "internal" to "داخلی"
    )
    val statuses = listOf(
        "planned" to "برنامه‌ریزی",
        "confirmed" to "تأیید شده",
        "finished" to "برگزارشده",
        "cancelled" to "لغو شده"
    )

    LaunchedEffect(Unit) {
        when (val r = classRepo.getClasses()) {
            is NetworkResult.Success -> classes = r.data.items
            else                     -> {}
        }
        when (val r = ageGroupRepo.getAgeGroups()) {
            is NetworkResult.Success -> ageGroups = r.data
            else                     -> {}
        }
    }

    LaunchedEffect(matchId) {
        if (matchId != null) {
            loading = true
            when (val r = matchRepo.getMatch(matchId)) {
                is NetworkResult.Success -> {
                    val m = r.data
                    title = m.title
                    opponent = m.opponentTeam
                            ?: ""
                    matchDate = m.matchDate
                    matchTime = m.matchTime?.take(5)
                            ?: "17:00"
                    location = m.location
                            ?: ""
                    notes = m.notes
                            ?: ""
                    matchType = m.matchType
                    status = m.status
                    selectedClassId = m.classId
                    selectedAgeGroupId = m.ageGroupId
                }

                is NetworkResult.Error   -> error = r.message
                else                     -> {}
            }
            loading = false
        }
    }

    GlassBackground {
        Box(Modifier.fillMaxSize()) {
            GlassTopBar(
                title = if (matchId == null) "افزودن مسابقه" else "ویرایش مسابقه",
                onBack = onBack
            )
            if (loading) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(top = 56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 56.dp)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        GlassCard3D {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                GlassTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    label = "عنوان مسابقه *"
                                )
                                GlassTextField(
                                    value = opponent,
                                    onValueChange = { opponent = it },
                                    label = "تیم حریف"
                                )

                                ExposedDropdownMenuBox(
                                    expanded = typeExpanded,
                                    onExpandedChange = { typeExpanded = !typeExpanded }) {
                                    GlassTextField(
                                        value = matchTypes.find { it.first == matchType }?.second
                                                ?: matchType,
                                        onValueChange = {},
                                        label = "نوع مسابقه",
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = typeExpanded,
                                        onDismissRequest = { typeExpanded = false }) {
                                        matchTypes.forEach { (v, l) ->
                                            DropdownMenuItem(
                                                text = { Text(l) },
                                                onClick = { matchType = v; typeExpanded = false })
                                        }
                                    }
                                }

                                JalaliDateField(
                                    label = "تاریخ مسابقه *",
                                    gregorianValue = matchDate.takeIf { it.isNotBlank() },
                                    onDatePicked = { matchDate = it })

                                // دیالوگ ساعت که قبلا نوشتیم
                                TimeWheelField(
                                    label = "ساعت مسابقه *",
                                    value = matchTime,
                                    onTimePicked = { matchTime = it })

                                GlassTextField(
                                    value = location,
                                    onValueChange = { location = it },
                                    label = "محل برگزاری"
                                )

                                // کلاس - الزامی
                                ExposedDropdownMenuBox(
                                    expanded = classExpanded,
                                    onExpandedChange = { classExpanded = !classExpanded }) {
                                    GlassTextField(
                                        value = classes.find { it.id == selectedClassId }?.title
                                                ?: "انتخاب کلاس *",
                                        onValueChange = {},
                                        label = "کلاس *",
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        isError = selectedClassId == null && error != null
                                    )
                                    ExposedDropdownMenu(
                                        expanded = classExpanded,
                                        onDismissRequest = { classExpanded = false }) {
                                        classes.forEach { c ->
                                            DropdownMenuItem(
                                                text = { Text(c.title) },
                                                onClick = { selectedClassId = c.id; classExpanded = false })
                                        }
                                    }
                                }

                                // گروه سنی - الزامی
                                ExposedDropdownMenuBox(
                                    expanded = ageGroupExpanded,
                                    onExpandedChange = { ageGroupExpanded = !ageGroupExpanded }) {
                                    GlassTextField(
                                        value = ageGroups.find { it.id == selectedAgeGroupId }?.title
                                                ?: "انتخاب گروه سنی *",
                                        onValueChange = {},
                                        label = "گروه سنی *",
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ageGroupExpanded) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        isError = selectedAgeGroupId == null && error != null
                                    )
                                    ExposedDropdownMenu(
                                        expanded = ageGroupExpanded,
                                        onDismissRequest = { ageGroupExpanded = false }) {
                                        ageGroups.forEach { ag ->
                                            DropdownMenuItem(
                                                text = { Text(ag.title) },
                                                onClick = { selectedAgeGroupId = ag.id; ageGroupExpanded = false })
                                        }
                                    }
                                }

                                ExposedDropdownMenuBox(
                                    expanded = statusExpanded,
                                    onExpandedChange = { statusExpanded = !statusExpanded }) {
                                    GlassTextField(
                                        value = statuses.find { it.first == status }?.second
                                                ?: status,
                                        onValueChange = {},
                                        label = "وضعیت",
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = statusExpanded,
                                        onDismissRequest = { statusExpanded = false }) {
                                        statuses.forEach { (v, l) ->
                                            DropdownMenuItem(
                                                text = { Text(l) },
                                                onClick = { status = v; statusExpanded = false })
                                        }
                                    }
                                }

                                GlassTextField(
                                    value = notes,
                                    onValueChange = { notes = it },
                                    label = "توضیحات",
                                    singleLine = false
                                )

                                error?.let {
                                    Text(
                                        it,
                                        color = Color(0xFFFF8A80),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Spacer(Modifier.height(8.dp))
                                GlassButton(
                                    text = if (matchId == null) "ایجاد مسابقه" else "ذخیره تغییرات",
                                    onClick = {
                                        if (title.trim().length < 3) {
                                            error = "عنوان حداقل ۳ حرف"; return@GlassButton
                                        }
                                        if (matchDate.isBlank()) {
                                            error = "تاریخ را انتخاب کنید"; return@GlassButton
                                        }
                                        if (matchTime.isBlank()) {
                                            error = "ساعت را انتخاب کنید"; return@GlassButton
                                        }
                                        if (selectedClassId == null) {
                                            error = "کلاس را انتخاب کنید (الزامی)"; return@GlassButton
                                        }
                                        if (selectedAgeGroupId == null) {
                                            error = "گروه سنی را انتخاب کنید (الزامی)"; return@GlassButton
                                        }
                                        busy = true
                                        error = null
                                        scope.launch {
                                            val result = if (matchId == null) {
                                                matchRepo.createMatch(
                                                    CreateMatchRequest(
                                                    title.trim(),
                                                    matchType,
                                                    selectedClassId,
                                                    selectedAgeGroupId,
                                                    opponent.trim()
                                                        .ifEmpty { null },
                                                    matchDate,
                                                    matchTime,
                                                    location.trim()
                                                        .ifEmpty { null },
                                                    status,
                                                    notes.trim()
                                                        .ifEmpty { null }))
                                            } else {
                                                matchRepo.updateMatch(
                                                    matchId,
                                                    UpdateMatchRequest(
                                                        title.trim(),
                                                        matchType,
                                                        selectedClassId,
                                                        selectedAgeGroupId,
                                                        opponent.trim()
                                                            .ifEmpty { null },
                                                        matchDate,
                                                        matchTime,
                                                        location.trim()
                                                            .ifEmpty { null },
                                                        status,
                                                        notes.trim()
                                                            .ifEmpty { null }))
                                            }
                                            when (result) {
                                                is NetworkResult.Success -> {
                                                    MatchRefreshBus.refresh()
                                                    onSaved()
                                                }

                                                is NetworkResult.Error   -> {
                                                    error = result.message; busy = false
                                                }

                                                else                     -> busy = false
                                            }
                                        }
                                    },
                                    loading = busy,
                                    enabled = !busy,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
