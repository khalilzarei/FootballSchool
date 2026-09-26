package com.khz.malekadmin.ui.classes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
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
import com.khz.malekadmin.MalekAdminApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.dto.response.EnrollAgeGroupResultDto
import com.khz.malekadmin.domain.model.Enrollment
import com.khz.malekadmin.domain.model.FootballClass
import com.khz.malekadmin.domain.model.Player
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassChip3D
import com.khz.malekadmin.ui.components.GlassSearchField
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentManagerScreen(classId: Int, onBack: () -> Unit, onEnrollPlayer: () -> Unit) {
    val context = LocalContext.current
    val container = (context.applicationContext as MalekAdminApp).container
    val repo = container.classRepository
    val scope = rememberCoroutineScope()

    var enrollments by remember { mutableStateOf<List<Enrollment>>(emptyList()) }
    var cls by remember { mutableStateOf<FootballClass?>(null) }
    var loading by remember { mutableStateOf(true) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<String?>(null) }
    var endTarget by remember { mutableStateOf<Enrollment?>(null) }

    // ─── ثبت‌نام گروهی ───
    var showBulkConfirm by remember { mutableStateOf(false) }
    var bulkRunning by remember { mutableStateOf(false) }
    var bulkResult by remember { mutableStateOf<EnrollAgeGroupResultDto?>(null) }

    // ─── افزودن بازیکن (انتخاب از بازیکنان ثبت‌نام‌نشده) ───
    val playerRepo = container.playerRepository
    var allPlayers by remember { mutableStateOf<List<Player>>(emptyList()) }
    var playersLoading by remember { mutableStateOf(false) }
    var addQuery by remember { mutableStateOf("") }
    var selectedAddIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var enrolling by remember { mutableStateOf(false) }
    var addError by remember { mutableStateOf<String?>(null) }

    fun reload() {
        scope.launch {
            loading = true
            error = null
            when (val r = repo.getClassPlayers(
                classId = classId,
                perPage = 100,
                query = query.takeIf { it.isNotBlank() },
                status = statusFilter
            )) {
                is NetworkResult.Success -> enrollments = r.data.items
                is NetworkResult.Error -> error = r.message
                else -> {}
            }
            loading = false
        }
        scope.launch {
            when (val r = repo.getClass(classId)) {
                is NetworkResult.Success -> cls = r.data
                else -> {}
            }
        }
    }

    fun enrollSelected() {
        scope.launch {
            enrolling = true
            addError = null
            var ok = 0
            var failMsg: String? = null
            selectedAddIds.forEach { pid ->
                when (val r = repo.enrollPlayer(classId, pid)) {
                    is NetworkResult.Success -> ok++
                    is NetworkResult.Error -> if (failMsg == null) failMsg = r.message
                    else -> {}
                }
            }
            if (ok > 0) selectedAddIds = emptySet()
            addError = failMsg?.let { "خطا در ثبت‌نام برخی بازیکنان: $it" }
            enrolling = false
            reload()
        }
    }

    LaunchedEffect(classId) { reload() }

    // بازیکنان فعال برای افزودن (جستجوی سمت سرور)
    LaunchedEffect(addQuery) {
        playersLoading = true
        when (val r = playerRepo.getPlayers(
            query = addQuery.takeIf { it.isNotBlank() },
            perPage = 100,
            status = "active"
        )) {
            is NetworkResult.Success -> allPlayers = r.data.items
            is NetworkResult.Error -> addError = r.message
            else -> {}
        }
        playersLoading = false
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "بازیکنان ثبت‌نام‌شده",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onEnrollPlayer) {
                        Icon(Icons.Default.PersonAdd, "ثبت‌نام بازیکن", tint = GoldPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(Modifier.padding(16.dp)) {
                GlassSearchField(
                    value = query,
                    onValueChange = {
                        query = it
                        reload()
                    },
                    label = "جستجو در ثبت‌نام‌شده‌ها"
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    GlassChip3D(
                        label = "همه",
                        selected = statusFilter == null,
                        onClick = { statusFilter = null; reload() }
                    )
                    GlassChip3D(
                        label = "فعال",
                        selected = statusFilter == "active",
                        onClick = { statusFilter = "active"; reload() },
                        accentColor = Color(0xFF81C784)
                    )
                    GlassChip3D(
                        label = "در انتظار",
                        selected = statusFilter == "pending",
                        onClick = { statusFilter = "pending"; reload() }
                    )
                    GlassChip3D(
                        label = "پایان‌یافته",
                        selected = statusFilter == "completed",
                        onClick = { statusFilter = "completed"; reload() },
                        accentColor = Color(0xFFFF8A80)
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    "${enrollments.size} بازیکن",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(0.5f)
                )

                // ─── ثبت‌نام گروهی (فقط کلاس‌های دارای گروه سنی) ───
                val ageGroupTitle = cls?.ageGroupTitle
                if (ageGroupTitle != null) {
                    Spacer(Modifier.height(12.dp))
                    GlassCard3D(Modifier.fillMaxWidth(),) {
                        Column(
                            Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column {
                                Text(
                                    "ثبت‌نام گروهی — گروه سنی «$ageGroupTitle»",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "همه بازیکنان فعالی که تاریخ تولدشان در بازه‌ی این گروه است، یک‌جا ثبت‌نام می‌شوند؛ بازیکنانی که قبلاً ثبت‌نام دارند رد می‌شوند.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(0.6f)
                                )
                                cls?.ageGroup?.let { ag ->
                                    ag.playersCount?.let { count ->
                                        Text(
                                            "$count بازیکن با تاریخ تولد در این بازه",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White.copy(0.5f)
                                        )
                                    }
                                }
                            }
                            GlassButton(
                                text = "ثبت‌نام گروهی بازیکنان گروه سنی",
                                onClick = { showBulkConfirm = true },
                                enabled = !bulkRunning,
                                loading = bulkRunning,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // ─── افزودن بازیکن — انتخاب از بازیکنان ثبت‌نام‌نشده ───
                Spacer(Modifier.height(12.dp))
                GlassCard3D(Modifier.fillMaxWidth(),) {
                    Column(
                        Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "افزودن بازیکن",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "بازیکنان فعالی که هنوز در این کلاس ثبت‌نام ندارند",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(0.6f)
                                )
                            }
                            Text(
                                "${selectedAddIds.size} انتخاب‌شده",
                                style = MaterialTheme.typography.labelMedium,
                                color = GoldPrimary
                            )
                        }

                        GlassSearchField(
                            value = addQuery,
                            onValueChange = { addQuery = it },
                            label = "جستجوی بازیکن برای افزودن"
                        )

                        val enrolledIds = enrollments.mapNotNull { e ->
                            e.player?.id
                                    ?: e.playerId.takeIf { it != 0 }
                        }
                            .toSet()
                        val addable = allPlayers.filter { p -> p.id !in enrolledIds }

                        when {
                            playersLoading    -> Box(
                                Modifier.fillMaxWidth()
                                    .height(72.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = GoldPrimary,
                                    strokeWidth = 2.dp
                                )
                            }

                            addable.isEmpty() -> Text(
                                if (allPlayers.isEmpty()) "بازیکنی یافت نشد"
                                else "بازیکن ثبت‌نام‌نشده‌ای باقی نمانده است",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(0.5f),
                                modifier = Modifier.padding(vertical = 6.dp)
                            )

                            else              -> LazyColumn(
                                modifier = Modifier.fillMaxWidth()
                                    .heightIn(max = 180.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                items(
                                    addable,
                                    key = { it.id }) { p ->
                                    Row(
                                        Modifier.fillMaxWidth()
                                        .clickable {
                                            selectedAddIds = if (selectedAddIds.contains(p.id)) selectedAddIds - p.id
                                            else selectedAddIds + p.id
                                        }
                                        .padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = selectedAddIds.contains(p.id),
                                            onCheckedChange = { chk ->
                                                selectedAddIds = if (chk) selectedAddIds + p.id
                                                else selectedAddIds - p.id
                                            })
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                p.fullName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White
                                            )
                                            Text(
                                                "${p.age ?: "-"} سال",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(0.4f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (selectedAddIds.isNotEmpty()) {
                            GlassButton(
                                text = "ثبت‌نام ${selectedAddIds.size} بازیکن انتخاب‌شده",
                                onClick = { enrollSelected() },
                                enabled = !enrolling && !busy && !loading,
                                loading = enrolling,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        addError?.let {
                            Text(
                                it,
                                color = Color(0xFFFF8A80),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            when {
                loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }

                error != null -> Box(
                    Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard3D(glowColor = Color(0xFFA50044),) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                error!!,
                                color = Color(0xFFFF8A80)
                            )
                            Spacer(Modifier.height(8.dp))
                            TextButton(onClick = { reload() }) {
                                Text(
                                    "تلاش مجدد",
                                    color = GoldPrimary
                                )
                            }
                        }
                    }
                }

                enrollments.isEmpty() -> Box(
                    Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard3D(Modifier.fillMaxWidth(),) {
                        Column(
                            Modifier.padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "بازیکنی ثبت‌نام نکرده است",
                                color = Color.White.copy(0.6f)
                            )
                            Text(
                                "از بخش «افزودن بازیکن» بالای صفحه یا دکمه + در نوار بالا، بازیکن به این کلاس اضافه کنید",
                                color = Color.White.copy(0.4f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(enrollments, key = { it.id }) { e ->
                        EnrollmentCard(
                            enrollment = e,
                            busy = busy,
                            onToggle = {
                                scope.launch {
                                    busy = true
                                    when (val r = repo.toggleEnrollmentStatus(e.id, !e.isActive)) {
                                        is NetworkResult.Success -> reload()
                                        is NetworkResult.Error -> error = r.message
                                        else -> {}
                                    }
                                    busy = false
                                }
                            },
                            onEnd = { endTarget = e }
                        )
                    }
                }
            }
        }
    }

    // ─── دیالوگ تأیید پایان ثبت‌نام ───
    endTarget?.let { e ->
        AlertDialog(
            onDismissRequest = { endTarget = null },
            title = { Text("پایان ثبت‌نام") },
            text = {
                Text("ثبت‌نام «${e.playerFullName}» در این کلاس پایان داده شود؟ (وضعیت به «پایان‌یافته» تغییر می‌کند)")
            },
            confirmButton = {
                TextButton(onClick = {
                    val target = endTarget
                    endTarget = null
                    if (target != null) {
                        scope.launch {
                            busy = true
                            when (val r = repo.endEnrollment(target.id)) {
                                is NetworkResult.Success -> reload()
                                is NetworkResult.Error -> error = r.message
                                else -> {}
                            }
                            busy = false
                        }
                    }
                }) {
                    Text("بله، پایان بده", color = Color(0xFFFF8A80))
                }
            },
            dismissButton = {
                TextButton(onClick = { endTarget = null }) {
                    Text("انصراف", color = Color.White.copy(0.7f))
                }
            },
            containerColor = Color(0xFF241040)
        )
    }

    // ─── دیالوگ تأیید ثبت‌نام گروهی ───
    if (showBulkConfirm) {
        AlertDialog(
            onDismissRequest = { showBulkConfirm = false },
            title = { Text("ثبت‌نام گروهی") },
            text = {
                Text(
                    "همه بازیکنان فعالی که تاریخ تولدشان در بازه‌ی گروه سنی «${cls?.ageGroupTitle ?: ""}» است، در این کلاس ثبت‌نام شوند؟\n\n" +
                            "بازیکنان دارای ثبت‌نام فعال/در انتظار رد می‌شوند و در صورت پر بودن ظرفیت، ثبت‌نام متوقف می‌شود."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showBulkConfirm = false
                    scope.launch {
                        bulkRunning = true
                        when (val r = repo.enrollAgeGroup(classId)) {
                            is NetworkResult.Success -> {
                                bulkResult = r.data
                                reload()
                            }
                            is NetworkResult.Error -> error = r.message
                            else -> {}
                        }
                        bulkRunning = false
                    }
                }) {
                    Text("بله، ثبت‌نام گروهی", color = GoldPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkConfirm = false }) {
                    Text("انصراف", color = Color.White.copy(0.7f))
                }
            },
            containerColor = Color(0xFF241040)
        )
    }

    // ─── دیالوگ نتیجه ثبت‌نام گروهی ───
    bulkResult?.let { res ->
        AlertDialog(
            onDismissRequest = { bulkResult = null },
            title = { Text("نتیجه ثبت‌نام گروهی") },
            text = {
                Text(
                    buildString {
                        append("${res.created} بازیکن جدید ثبت‌نام شد")
                        if (res.skippedExisting > 0) append("\n${res.skippedExisting} نفر از قبل ثبت‌نام داشتند")
                        if (res.skippedCapacity > 0) append("\n${res.skippedCapacity} نفر به‌دلیل تکمیل ظرفیت ثبت نشدند")
                        if (res.created == 0 && res.skippedExisting == 0 && res.skippedCapacity == 0) {
                            append("\nهیچ بازیکن فعالی برای این گروه سنی یافت نشد")
                        }
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { bulkResult = null }) {
                    Text("باشه", color = GoldPrimary)
                }
            },
            containerColor = Color(0xFF241040)
        )
    }
}

@Composable
private fun EnrollmentCard(
    enrollment: Enrollment,
    busy: Boolean,
    onToggle: () -> Unit,
    onEnd: () -> Unit
) {
    GlassCard3D(Modifier.fillMaxWidth(),) {
        Row(
            Modifier.fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    enrollment.playerFullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "تاریخ ثبت‌نام: ${enrollment.enrolledAt ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f)
                )
                enrollment.endedAt?.let {
                    Text(
                        "تاریخ پایان: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF8A80).copy(0.8f)
                    )
                }
                enrollment.monthlyFeeOverride?.let { fee ->
                    if (fee > 0) {
                        Text(
                            "شهریه اختصاصی: $fee ریال",
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldPrimary.copy(0.85f)
                        )
                    }
                }
                enrollment.notes?.let { n ->
                    if (n.isNotBlank()) {
                        Text(
                            n,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.5f),
                            maxLines = 1
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    enrollment.statusLabel,
                    color = when (enrollment.status) {
                        "active"    -> Color(0xFF81C784)
                        "completed" -> Color(0xFFFF8A80)
                        else        -> Color.White.copy(0.7f)
                    },
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.height(4.dp))
                Row {
                    TextButton(
                        onClick = onToggle,
                        enabled = !busy
                    ) {
                        Text(
                            if (enrollment.isActive) "غیرفعال" else "فعال",
                            color = GoldPrimary,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    if (enrollment.status != "completed") {
                        TextButton(
                            onClick = onEnd,
                            enabled = !busy
                        ) {
                            Text(
                                "پایان",
                                color = Color(0xFFFF8A80),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}