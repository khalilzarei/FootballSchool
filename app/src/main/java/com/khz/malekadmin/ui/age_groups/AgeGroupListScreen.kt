package com.khz.malekadmin.ui.age_groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.FootballSchoolApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.core.util.DateUtils
import com.khz.malekadmin.data.dto.request.CreateAgeGroupRequest
import com.khz.malekadmin.data.dto.request.UpdateAgeGroupRequest
import com.khz.malekadmin.domain.model.AgeGroup
import com.khz.malekadmin.domain.model.Player
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassTextField
import com.khz.malekadmin.ui.components.JalaliDateField
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.components.ListState
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

private val DATE_REGEX = Regex("^\\d{4}-\\d{2}-\\d{2}$")

private fun jalali(date: String?): String = date?.let { DateUtils.toPersianDigits(DateUtils.gregorianToJalali(it)) }
        ?: "-"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeGroupListScreen(onBack: () -> Unit) {
    val vm: AgeGroupListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.ageGroupRepository
    val scope = rememberCoroutineScope()

    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    var showAddDialog by remember { mutableStateOf(false) }
    var addServerError by remember { mutableStateOf<String?>(null) }

    var editTarget by remember { mutableStateOf<AgeGroup?>(null) }
    var editServerError by remember { mutableStateOf<String?>(null) }

    var playersTarget by remember { mutableStateOf<AgeGroup?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "گروه‌بندی سنی",
                onBack = onBack,
                actions = {
                    IconButton(onClick = {
                        addServerError = null
                        showAddDialog = true
                    }) {
                        Icon(
                            Icons.Default.Add,
                            "افزودن گروه سنی",
                            tint = GoldPrimary
                        )
                    }
                })
        }) { padding ->
        Box(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            when (val s = state) {
                is ListState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }

                is ListState.Error   -> GlassCard3D(Modifier.fillMaxWidth(),) {
                    Column(
                        Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            s.message,
                            color = Color(0xFFFF8A80)
                        )
                        Spacer(Modifier.height(12.dp))
                        GlassButton(
                            "تلاش مجدد",
                            onClick = { vm.refresh() })
                    }
                }

                is ListState.Success -> {
                    // مرتب‌سازی بر اساس سن: جوان‌ترین (سال تولد جدیدتر) اول
                    val groups = s.items.sortedWith(compareByDescending<AgeGroup> { it.birthDateFrom }.thenByDescending { it.birthDateTo }
                        .thenBy { it.sortOrder })
                    Column(Modifier.fillMaxSize()) {
                        Text(
                            "هر بازیکنی که تاریخ تولدش در بازه‌ی یک گروه باشد، عضو همان گروه سنی است",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.5f)
                        )
                        Spacer(Modifier.height(12.dp))
                        if (groups.isEmpty()) {
                            GlassCard3D(Modifier.fillMaxWidth(),) {
                                Column(
                                    Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "گروه سنی‌ای ثبت نشده است",
                                        color = Color.White.copy(0.6f)
                                    )
                                    Text(
                                        "با دکمه + گروه سنی جدید بسازید",
                                        color = Color.White.copy(0.4f),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(
                                    groups,
                                    key = { it.id }) { g ->
                                    AgeGroupCard(
                                        group = g,
                                        busy = busy,
                                        onShowPlayers = { playersTarget = g },
                                        onEdit = {
                                            editServerError = null
                                            editTarget = g
                                        },
                                        onToggle = {
                                            scope.launch {
                                                busy = true
                                                when (val r = repo.toggleStatus(
                                                    g.id,
                                                    !g.isActive
                                                )) {
                                                    is NetworkResult.Error -> error = r.message
                                                    else                   -> {}
                                                }
                                                busy = false
                                                vm.refresh()
                                            }
                                        })
                                }
                            }
                        }
                        error?.let {
                            Spacer(Modifier.height(8.dp))
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
    }

    // ─── دیالوگ ایجاد - پیام تداخل داخل همین دیالوگ نمایش داده می‌شود ───
    if (showAddDialog) {
        AgeGroupDialog(
            initial = null,
            serverError = addServerError,
            busy = busy,
            onDismiss = {
                showAddDialog = false
                addServerError = null
            },
            onSave = { title, from, to, sortOrder ->
                scope.launch {
                    busy = true
                    addServerError = null
                    when (val r = repo.createAgeGroup(
                        CreateAgeGroupRequest(
                            title,
                            from,
                            to,
                            null,
                            null,
                            sortOrder,
                            "active"
                        )
                    )) {
                        is NetworkResult.Success -> {
                            showAddDialog = false
                            addServerError = null
                            vm.refresh()
                        }

                        is NetworkResult.Error   -> {
                            // همین پیام سرور را داخل دیالوگ نمایش بده
                            addServerError = r.message
                        }

                        else                     -> {}
                    }
                    busy = false
                }
            })
    }

    // ─── دیالوگ ویرایش - پیام تداخل داخل همین دیالوگ ───
    editTarget?.let { target ->
        AgeGroupDialog(
            initial = target,
            serverError = editServerError,
            busy = busy,
            onDismiss = {
                editTarget = null
                editServerError = null
            },
            onSave = { title, from, to, sortOrder ->
                scope.launch {
                    busy = true
                    editServerError = null
                    when (val r = repo.updateAgeGroup(
                        target.id,
                        UpdateAgeGroupRequest(
                            title,
                            from,
                            to,
                            null,
                            null,
                            sortOrder,
                            null
                        )
                    )) {
                        is NetworkResult.Success -> {
                            editTarget = null
                            editServerError = null
                            vm.refresh()
                        }

                        is NetworkResult.Error   -> {
                            editServerError = r.message
                        }

                        else                     -> {}
                    }
                    busy = false
                }
            })
    }

    playersTarget?.let { target ->
        AgeGroupPlayersDialog(
            group = target,
            onDismiss = { playersTarget = null })
    }
}

@Composable
private fun AgeGroupCard(
    group: AgeGroup,
    busy: Boolean,
    onShowPlayers: () -> Unit,
    onEdit: () -> Unit,
    onToggle: () -> Unit
) {
    GlassCard3D(Modifier.fillMaxWidth(),) {
        Column(
            Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        group.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${group.playersCount ?: "-"} بازیکن",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Text(
                        if (group.isActive) "فعال" else "غیرفعال",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (group.isActive) Color(0xFF81C784) else Color(0xFFFF8A80)
                    )
                }
            }
            Text(
                "تولد: ${jalali(group.birthDateFrom)} تا ${jalali(group.birthDateTo)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(0.6f)
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    4.dp,
                    Alignment.End
                )
            ) {
                TextButton(
                    onClick = onShowPlayers,
                    enabled = !busy
                ) {
                    Text(
                        "بازیکنان",
                        color = GoldPrimary
                    )
                }
                TextButton(
                    onClick = onEdit,
                    enabled = !busy
                ) {
                    Text(
                        "ویرایش",
                        color = Color.White.copy(0.8f)
                    )
                }
                TextButton(
                    onClick = onToggle,
                    enabled = !busy
                ) {
                    Text(
                        if (group.isActive) "غیرفعال‌سازی" else "فعال‌سازی",
                        color = if (group.isActive) Color(0xFFFF8A80) else Color(0xFF81C784)
                    )
                }
            }
        }
    }
}

@Composable
private fun AgeGroupDialog(
    initial: AgeGroup?,
    serverError: String?,
    busy: Boolean,
    onDismiss: () -> Unit,
    onSave: (title: String, fromDate: String, toDate: String, sortOrder: Int) -> Unit
) {
    val isEdit = initial != null
    var title by remember {
        mutableStateOf(
            initial?.title
                    ?: ""
        )
    }
    var fromDate by remember {
        mutableStateOf(
            initial?.birthDateFrom
                    ?: ""
        )
    }
    var toDate by remember {
        mutableStateOf(
            initial?.birthDateTo
                    ?: ""
        )
    }
    var sortOrderText by remember {
        mutableStateOf(
            (initial?.sortOrder
                    ?: 0).toString()
        )
    }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isEdit) "ویرایش گروه سنی" else "گروه سنی جدید",
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassTextField(
                    value = title,
                    onValueChange = { title = it; localError = null },
                    label = "نام گروه (مثلاً: زیر ۹ سال)"
                )
                JalaliDateField(
                    label = "از تاریخ تولد",
                    gregorianValue = fromDate.takeIf { it.isNotBlank() },
                    onDatePicked = { fromDate = it; localError = null })
                JalaliDateField(
                    label = "تا تاریخ تولد",
                    gregorianValue = toDate.takeIf { it.isNotBlank() },
                    onDatePicked = { toDate = it; localError = null })
                GlassTextField(
                    value = sortOrderText,
                    onValueChange = { sortOrderText = it.filter { c -> c.isDigit() } },
                    label = "ترتیب نمایش (اختیاری)"
                )

                localError?.let {
                    Text(
                        it,
                        color = Color(0xFFFF8A80),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // ─── پیام خطای سرور (مثل تداخل بازه) داخل همین دیالوگ ───
                serverError?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFF8A80).copy(alpha = 0.15f))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    null,
                                    tint = Color(0xFFFF8A80),
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height(18.dp)
                                )
                                Text(
                                    "خطا در ثبت",
                                    color = Color(0xFFFF8A80),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                msg,
                                color = Color.White.copy(0.9f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (msg.contains("تداخل")) {
                                Spacer(Modifier.height(4.dp))
                                androidx.compose.material3.HorizontalDivider(color = Color.White.copy(0.1f))
                                Text(
                                    "• هر گروه باید بازه یکتا داشته باشد\n• حتی ۱ روز مشترک هم تداخل است\n• گروه متداخل را ویرایش یا غیرفعال کنید",
                                    color = Color.White.copy(0.65f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !busy,
                onClick = {
                    val f = fromDate.trim()
                    val t = toDate.trim()
                    localError = when {
                        title.trim().length < 3 -> "نام گروه باید حداقل ۳ حرف باشد"
                        !DATE_REGEX.matches(f)  -> "تاریخ شروع بازه را از تقویم انتخاب کنید"
                        !DATE_REGEX.matches(t)  -> "تاریخ پایان بازه را از تقویم انتخاب کنید"
                        f > t                   -> "تاریخ شروع بازه باید قبل از تاریخ پایان باشد"
                        else                    -> null
                    }
                    if (localError == null) {
                        onSave(
                            title.trim(),
                            f,
                            t,
                            sortOrderText.toIntOrNull()
                                    ?: 0
                        )
                    }
                }) {
                if (busy) CircularProgressIndicator(
                    color = GoldPrimary,
                    modifier = Modifier
                        .width(18.dp)
                        .height(18.dp),
                    strokeWidth = 2.dp
                )
                else Text(
                    "ذخیره",
                    color = GoldPrimary
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !busy
            ) {
                Text(
                    "انصراف",
                    color = Color.White.copy(0.7f)
                )
            }
        },
        containerColor = Color(0xFF241040)
    )
}

@Composable
private fun AgeGroupPlayersDialog(
    group: AgeGroup,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.ageGroupRepository
    var players by remember(group.id) { mutableStateOf<List<Player>?>(null) }
    var loadError by remember(group.id) { mutableStateOf<String?>(null) }

    LaunchedEffect(group.id) {
        when (val r = repo.getPlayers(group.id)) {
            is NetworkResult.Success -> players = r.data
            is NetworkResult.Error   -> loadError = r.message
            else                     -> {}
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "بازیکنان «${group.title}»",
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "تولد: ${jalali(group.birthDateFrom)} تا ${jalali(group.birthDateTo)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f)
                )
                when {
                    players == null && loadError == null -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = GoldPrimary,
                            modifier = Modifier
                                .height(18.dp)
                                .width(18.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            "در حال بارگذاری...",
                            color = Color.White.copy(0.6f)
                        )
                    }

                    loadError != null                    -> Text(
                        loadError
                                ?: "",
                        color = Color(0xFFFF8A80),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    players.orEmpty()
                        .isEmpty()                       -> Text(
                        "هیچ بازیکنی با تاریخ تولد در این بازه یافت نشد",
                        color = Color.White.copy(0.6f)
                    )

                    else                                 -> LazyColumn(
                        modifier = Modifier.height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(
                            players.orEmpty(),
                            key = { it.id }) { p ->
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        p.fullName,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "${p.age ?: "-"} سال — تولد ${jalali(p.birthDate)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(0.5f)
                                    )
                                }
                                Text(
                                    if (p.status == "active") "فعال" else "غیرفعال",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (p.status == "active") Color(0xFF81C784) else Color(0xFFFF8A80)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "بستن",
                    color = GoldPrimary
                )
            }
        },
        containerColor = Color(0xFF241040)
    )
}
