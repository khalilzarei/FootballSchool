package com.khz.footballschool.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.ApiErrorHandler
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.core.util.DateUtils
import com.khz.footballschool.data.dto.request.AddMatchPlayerRequest
import com.khz.footballschool.data.dto.request.UpdateMatchPlayerRequest
import com.khz.footballschool.domain.model.Match
import com.khz.footballschool.domain.model.MatchPlayer
import com.khz.footballschool.domain.model.Player
import com.khz.footballschool.ui.components.GlassBackground
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTextField
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary
import com.khz.footballschool.ui.theme.GreenSuccess
import com.khz.footballschool.ui.theme.RedError
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchPlayersScreen(
    matchId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val matchRepo = container.matchRepository
    val playerRepo = container.playerRepository
    val ageGroupRepo = container.ageGroupRepository
    val scope = rememberCoroutineScope()

    var match by remember { mutableStateOf<Match?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<MatchPlayer?>(null) }

    // بازیکنان گروه سنی
    var ageGroupPlayers by remember { mutableStateOf<List<Player>>(emptyList()) }
    var ageGroupLoading by remember { mutableStateOf(false) }

    // جستجوی عمومی
    var playerQuery by remember { mutableStateOf("") }
    var playerResults by remember { mutableStateOf<List<Player>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }

    // فرم
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }
    var goals by remember { mutableStateOf("0") }
    var assists by remember { mutableStateOf("0") }
    var yellow by remember { mutableStateOf("0") }
    var red by remember { mutableStateOf("0") }
    var minutes by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var jersey by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var invitation by remember { mutableStateOf("invited") }
    var attendance by remember { mutableStateOf("present") }
    var notes by remember { mutableStateOf("") }

    var invitationExpanded by remember { mutableStateOf(false) }
    var attendanceExpanded by remember { mutableStateOf(false) }

    val invitationOptions = listOf(
        "invited" to "دعوت شده",
        "accepted" to "پذیرفته",
        "declined" to "رد شده",
        "pending" to "در انتظار"
    )
    val attendanceOptions = listOf(
        "present" to "حاضر",
        "absent" to "غایب",
        "late" to "تأخیر",
        "injured" to "مصدوم"
    )

    // نگاشت id -> Player برای نمایش نام حتی وقتی player نال است
    fun playerMap(): Map<Int, Player> {
        val map = mutableMapOf<Int, Player>()
        ageGroupPlayers.forEach { map[it.id] = it }
        playerResults.forEach { map[it.id] = it }
        match?.players?.forEach { mp -> mp.player?.let { map[mp.playerId] = it } }
        selectedPlayer?.let { map[it.id] = it }
        return map
    }

    fun resolveName(mp: MatchPlayer): String {
        mp.player?.fullName?.takeIf { it.isNotBlank() && !it.startsWith("بازیکن #") }
            ?.let { return it }
        val fromMap = playerMap()[mp.playerId]?.fullName
        if (!fromMap.isNullOrBlank()) return fromMap
        return "بازیکن #${DateUtils.toPersianDigits(mp.playerId.toString())}"
    }

    fun load() {
        scope.launch {
            loading = true
            when (val r = matchRepo.getMatchDetail(matchId)) {
                is NetworkResult.Success -> {
                    match = r.data
                    error = null
                    r.data.ageGroupId?.let { agId ->
                        ageGroupLoading = true
                        when (val pr = ageGroupRepo.getPlayers(agId)) {
                            is NetworkResult.Success -> ageGroupPlayers = pr.data
                            is NetworkResult.Error   -> error = pr.message
                            else                     -> {}
                        }
                        ageGroupLoading = false
                    }
                }

                is NetworkResult.Error   -> error = r.message
                else                     -> {}
            }
            loading = false
        }
    }

    fun searchPlayers(q: String) {
        // همیشه سرچ عمومی را انجام بده برای بخش "سایر بازیکنان"
        if (q.length < 2) {
            playerResults = emptyList(); return
        }
        scope.launch {
            searching = true
            when (val r = playerRepo.getPlayers(
                page = 1,
                perPage = 30,
                query = q
            )) {
                is NetworkResult.Success -> playerResults = r.data.items
                else                     -> playerResults = emptyList()
            }
            searching = false
        }
    }

    fun resetForm() {
        selectedPlayer = null
        goals = "0"; assists = "0"; yellow = "0"; red = "0"
        minutes = ""; rating = ""; jersey = ""; position = ""
        invitation = "invited"; attendance = "present"; notes = ""
        playerQuery = ""; playerResults = emptyList()
    }

    fun openAdd() {
        resetForm(); showAddDialog = true; editTarget = null
    }

    fun openEdit(mp: MatchPlayer) {
        editTarget = mp
        // سعی کن نام را از کش پیدا کنی
        selectedPlayer = mp.player
                ?: playerMap()[mp.playerId]
        goals = mp.goals.toString()
        assists = mp.assists.toString()
        yellow = mp.yellowCards.toString()
        red = mp.redCards.toString()
        minutes = mp.minutesPlayed?.toString()
                ?: ""
        rating = mp.rating?.toString()
                ?: ""
        jersey = mp.jerseyNumber?.toString()
                ?: ""
        position = mp.position
                ?: ""
        invitation = mp.invitationStatus
                ?: "invited"
        attendance = mp.attendanceStatus
                ?: "present"
        notes = mp.notes
                ?: ""
        showAddDialog = true
    }

    LaunchedEffect(matchId) { load() }

    GlassBackground {
        Box(Modifier.fillMaxSize()) {
            GlassTopBar(
                title = "بازیکنان مسابقه",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { openAdd() }) {
                        Icon(
                            Icons.Default.Add,
                            "افزودن",
                            tint = GoldPrimary
                        )
                    }
                })

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
                    modifier = Modifier.padding(top = 56.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    match?.let { m ->
                        item {
                            MatchHeaderCard(
                                m,
                                ageGroupPlayersCount = ageGroupPlayers.size
                            )
                        }
                        if (m.players.isEmpty()) {
                            item {
                                GlassCard3D {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            null,
                                            tint = Color.White.copy(0.3f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            "بازیکنی اضافه نشده است",
                                            color = Color.White.copy(0.6f),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        } else {
                            item {
                                Text(
                                    "${DateUtils.toPersianDigits(m.players.size.toString())} بازیکن دعوت شده | گل کل: ${
                                        DateUtils.toPersianDigits(m.players.sumOf { it.goals }
                                            .toString())
                                    }",
                                    color = Color.White.copy(0.6f),
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            items(
                                m.players,
                                key = { it.id }) { mp ->
                                MatchPlayerCard(
                                    mp = mp,
                                    displayName = resolveName(mp),
                                    isFinished = m.status == "finished" || m.status == "completed",
                                    onEdit = { openEdit(mp) },
                                    onDelete = {
                                        scope.launch {
                                            busy = true
                                            when (val r = matchRepo.removePlayer(mp.id)) {
                                                is NetworkResult.Success -> load()
                                                is NetworkResult.Error   -> error = r.message
                                                else                     -> {}
                                            }
                                            busy = false
                                        }
                                    })
                            }
                        }

                        if (m.ageGroupId != null && ageGroupPlayers.isNotEmpty()) {
                            val invitedIds = m.players.map { it.playerId }
                                .toSet()
                            val notInvited = ageGroupPlayers.filter { it.id !in invitedIds }
                            if (notInvited.isNotEmpty()) {
                                item {
                                    Text(
                                        "بازیکنان گروه سنی دعوت نشده (${DateUtils.toPersianDigits(notInvited.size.toString())}):",
                                        color = GoldPrimary.copy(0.8f),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                                items(
                                    notInvited.take(10),
                                    key = { it.id }) { p ->
                                    GlassCard3D(onClick = { selectedPlayer = p; openAdd(); selectedPlayer = p }) {
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                p.fullName,
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                "افزودن +",
                                                color = GoldPrimary,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    error?.let {
                        item {
                            GlassCard3D {
                                Text(
                                    it,
                                    color = Color(0xFFFF8A80),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    item { Spacer(Modifier.height(40.dp)) }
                }
            }

            if (showAddDialog) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard3D(modifier = Modifier.fillMaxWidth(0.94f)) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            item {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            if (editTarget == null) "افزودن بازیکن" else "ویرایش آمار بازیکن",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        if (editTarget == null && match?.ageGroupId != null) {
                                            Text(
                                                "گروه سنی: ${match?.ageGroup?.title ?: match?.ageGroupTitle ?: ""} (${DateUtils.toPersianDigits(ageGroupPlayers.size.toString())} نفر) - سایر بازیکنان هم قابل انتخاب هستند",
                                                color = GoldPrimary.copy(0.7f),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                    IconButton(onClick = { showAddDialog = false }) {
                                        Text(
                                            "✕",
                                            color = Color.White.copy(0.6f)
                                        )
                                    }
                                }
                            }

                            if (editTarget == null) {
                                item {
                                    GlassTextField(
                                        value = playerQuery,
                                        onValueChange = { playerQuery = it; searchPlayers(it) },
                                        label = "جستجو (نام بازیکن) - گروه سنی اولویت دارد"
                                    )
                                    if (ageGroupLoading) {
                                        Text(
                                            "در حال بارگذاری بازیکنان گروه...",
                                            color = Color.White.copy(0.5f),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    } else {
                                        val invitedIds = match?.players?.map { it.playerId }
                                            ?.toSet()
                                                ?: emptySet()
                                        // بخش ۱: بازیکنان گروه سنی
                                        val filteredAge = ageGroupPlayers.filter { p ->
                                            p.id !in invitedIds && (playerQuery.isBlank() || p.fullName.contains(
                                                playerQuery,
                                                ignoreCase = true
                                            ))
                                        }
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(
                                                "★ بازیکنان گروه سنی (${DateUtils.toPersianDigits(filteredAge.size.toString())}) - اولویت",
                                                color = GoldPrimary,
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            if (filteredAge.isEmpty()) {
                                                Text(
                                                    "همه بازیکنان گروه دعوت شده‌اند یا یافت نشد",
                                                    color = Color.White.copy(0.5f),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            } else {
                                                Column(
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(GoldPrimary.copy(0.08f))
                                                        .border(
                                                            0.5.dp,
                                                            GoldPrimary.copy(0.2f),
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(4.dp),
                                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    filteredAge.take(20)
                                                        .forEach { p ->
                                                            Row(
                                                                Modifier
                                                                    .fillMaxWidth()
                                                                    .clip(RoundedCornerShape(6.dp))
                                                                    .background(if (selectedPlayer?.id == p.id) GoldPrimary.copy(0.25f) else Color.Transparent)
                                                                    .padding(8.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Column(Modifier.weight(1f)) {
                                                                    Text(
                                                                        p.fullName,
                                                                        color = Color.White,
                                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                                                    )
                                                                    Text(
                                                                        "کد: ${DateUtils.toPersianDigits(p.id.toString())} - گروه سنی",
                                                                        color = GoldPrimary.copy(0.6f),
                                                                        style = MaterialTheme.typography.labelSmall
                                                                    )
                                                                }
                                                                if (selectedPlayer?.id == p.id) Icon(
                                                                    Icons.Default.CheckCircle,
                                                                    null,
                                                                    tint = GoldPrimary,
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                                Spacer(Modifier.width(6.dp))
                                                                GlassButton(
                                                                    text = if (selectedPlayer?.id == p.id) "انتخاب شد" else "انتخاب",
                                                                    onClick = { selectedPlayer = p },
                                                                    modifier = Modifier
                                                                )
                                                            }
                                                        }
                                                }
                                            }

                                            // بخش ۲: سایر بازیکنان (سرچ عمومی)
                                            Spacer(Modifier.height(4.dp))
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    "سایر بازیکنان",
                                                    color = Color.White.copy(0.7f),
                                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                                )
                                                if (searching) CircularProgressIndicator(
                                                    color = GoldPrimary,
                                                    modifier = Modifier.size(14.dp),
                                                    strokeWidth = 2.dp
                                                )
                                            }
                                            if (playerQuery.length < 2) {
                                                Text(
                                                    "برای جستجوی سایر بازیکنان حداقل ۲ حرف بنویسید",
                                                    color = Color.White.copy(0.4f),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            } else {
                                                val otherFiltered = playerResults.filter { p ->
                                                    p.id !in invitedIds && p.id !in filteredAge.map { it.id }
                                                        .toSet()
                                                }
                                                if (otherFiltered.isEmpty()) {
                                                    Text(
                                                        "بازیکنی خارج از گروه یافت نشد",
                                                        color = Color.White.copy(0.5f),
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                } else {
                                                    Column(
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color.White.copy(0.06f))
                                                            .padding(4.dp),
                                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                                    ) {
                                                        otherFiltered.take(20)
                                                            .forEach { p ->
                                                                Row(
                                                                    Modifier
                                                                        .fillMaxWidth()
                                                                        .clip(RoundedCornerShape(6.dp))
                                                                        .background(if (selectedPlayer?.id == p.id) GoldPrimary.copy(0.2f) else Color.Transparent)
                                                                        .padding(8.dp),
                                                                    verticalAlignment = Alignment.CenterVertically
                                                                ) {
                                                                    Column(Modifier.weight(1f)) {
                                                                        Text(
                                                                            p.fullName,
                                                                            color = Color.White,
                                                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                                                        )
                                                                        Text(
                                                                            "کد: ${DateUtils.toPersianDigits(p.id.toString())}",
                                                                            color = Color.White.copy(0.5f),
                                                                            style = MaterialTheme.typography.labelSmall
                                                                        )
                                                                    }
                                                                    GlassButton(
                                                                        text = if (selectedPlayer?.id == p.id) "انتخاب شد" else "انتخاب",
                                                                        onClick = { selectedPlayer = p },
                                                                        modifier = Modifier
                                                                    )
                                                                }
                                                            }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    selectedPlayer?.let {
                                        Text(
                                            "انتخاب شده: ${it.fullName}",
                                            color = GoldPrimary,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            } else {
                                item {
                                    val name = editTarget?.let { resolveName(it) }
                                            ?: "بازیکن #${editTarget?.playerId}"
                                    Text(
                                        name,
                                        color = GoldPrimary,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    if (match?.status == "finished" || match?.status == "completed") {
                                        Text(
                                            "مسابقه پایان یافته - آمار نهایی را ثبت کنید",
                                            color = GreenSuccess.copy(0.8f),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }

                            item {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    GlassTextField(
                                        value = jersey,
                                        onValueChange = { jersey = it.filter { c -> c.isDigit() } },
                                        label = "شماره پیراهن",
                                        modifier = Modifier.weight(1f)
                                    )
                                    GlassTextField(
                                        value = position,
                                        onValueChange = { position = it },
                                        label = "پست",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            item {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ExposedDropdownMenuBox(
                                        expanded = invitationExpanded,
                                        onExpandedChange = { invitationExpanded = !invitationExpanded },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        GlassTextField(
                                            value = invitationOptions.find { it.first == invitation }?.second
                                                    ?: invitation,
                                            onValueChange = {},
                                            label = "دعوت",
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = invitationExpanded) },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = invitationExpanded,
                                            onDismissRequest = { invitationExpanded = false }) {
                                            invitationOptions.forEach { (v, l) ->
                                                DropdownMenuItem(
                                                    text = { Text(l) },
                                                    onClick = { invitation = v; invitationExpanded = false })
                                            }
                                        }
                                    }
                                    ExposedDropdownMenuBox(
                                        expanded = attendanceExpanded,
                                        onExpandedChange = { attendanceExpanded = !attendanceExpanded },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        GlassTextField(
                                            value = attendanceOptions.find { it.first == attendance }?.second
                                                    ?: attendance,
                                            onValueChange = {},
                                            label = "حضور",
                                            readOnly = true,
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = attendanceExpanded) },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = attendanceExpanded,
                                            onDismissRequest = { attendanceExpanded = false }) {
                                            attendanceOptions.forEach { (v, l) ->
                                                DropdownMenuItem(
                                                    text = { Text(l) },
                                                    onClick = { attendance = v; attendanceExpanded = false })
                                            }
                                        }
                                    }
                                }
                            }
                            item {
                                Text(
                                    "آمار بازی (بعد از پایان مسابقه قابل ویرایش):",
                                    color = Color.White.copy(0.6f),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            item {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    GlassTextField(
                                        value = goals,
                                        onValueChange = { goals = it.filter { c -> c.isDigit() } },
                                        label = "گل",
                                        modifier = Modifier.weight(1f)
                                    )
                                    GlassTextField(
                                        value = assists,
                                        onValueChange = { assists = it.filter { c -> c.isDigit() } },
                                        label = "پاس گل",
                                        modifier = Modifier.weight(1f)
                                    )
                                    GlassTextField(
                                        value = yellow,
                                        onValueChange = { yellow = it.filter { c -> c.isDigit() } },
                                        label = "زرد",
                                        modifier = Modifier.weight(1f)
                                    )
                                    GlassTextField(
                                        value = red,
                                        onValueChange = { red = it.filter { c -> c.isDigit() } },
                                        label = "قرمز",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            item {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    GlassTextField(
                                        value = minutes,
                                        onValueChange = { minutes = it.filter { c -> c.isDigit() } },
                                        label = "دقایق بازی",
                                        modifier = Modifier.weight(1f)
                                    )
                                    GlassTextField(
                                        value = rating,
                                        onValueChange = { rating = it },
                                        label = "امتیاز (0-10)",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            item {
                                GlassTextField(
                                    value = notes,
                                    onValueChange = { notes = it },
                                    label = "یادداشت",
                                    singleLine = false
                                )
                            }
                            item {
                                var localError by remember { mutableStateOf<String?>(null) }
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    localError?.let {
                                        Text(
                                            it,
                                            color = Color(0xFFFF8A80),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        GlassButton(
                                            text = "انصراف",
                                            onClick = { showAddDialog = false },
                                            modifier = Modifier.weight(1f)
                                        )
                                        GlassButton(
                                            text = if (editTarget == null) "افزودن" else "ذخیره آمار",
                                            loading = busy,
                                            enabled = !busy,
                                            onClick = {
                                                if (editTarget == null && selectedPlayer == null) {
                                                    localError = "بازیکن را انتخاب کنید"; return@GlassButton
                                                }
                                                val g = goals.toIntOrNull()
                                                        ?: 0
                                                val a = assists.toIntOrNull()
                                                        ?: 0
                                                val y = yellow.toIntOrNull()
                                                        ?: 0
                                                val r = red.toIntOrNull()
                                                        ?: 0
                                                val min = minutes.toIntOrNull()
                                                val rat = rating.toDoubleOrNull()
                                                val jer = jersey.toIntOrNull()
                                                scope.launch {
                                                    busy = true
                                                    try {
                                                        val result = if (editTarget == null) {
                                                            matchRepo.addPlayer(
                                                                matchId,
                                                                AddMatchPlayerRequest(
                                                                    playerId = selectedPlayer!!.id,
                                                                    invitationStatus = invitation,
                                                                    attendanceStatus = attendance,
                                                                    jerseyNumber = jer,
                                                                    position = position.ifEmpty { null },
                                                                    goals = g,
                                                                    assists = a,
                                                                    yellowCards = y,
                                                                    redCards = r,
                                                                    minutesPlayed = min,
                                                                    rating = rat,
                                                                    notes = notes.ifEmpty { null }))
                                                        } else {
                                                            matchRepo.updatePlayer(
                                                                editTarget!!.id,
                                                                UpdateMatchPlayerRequest(
                                                                    invitationStatus = invitation,
                                                                    attendanceStatus = attendance,
                                                                    jerseyNumber = jer,
                                                                    position = position.ifEmpty { null },
                                                                    goals = g,
                                                                    assists = a,
                                                                    yellowCards = y,
                                                                    redCards = r,
                                                                    minutesPlayed = min,
                                                                    rating = rat,
                                                                    notes = notes.ifEmpty { null }))
                                                        }
                                                        when (result) {
                                                            is NetworkResult.Success -> {
                                                                showAddDialog = false; load()
                                                            }

                                                            is NetworkResult.Error   -> localError = result.message
                                                            else                     -> {}
                                                        }
                                                    } catch (e: Exception) {
                                                        localError = ApiErrorHandler.extractMessage(e)
                                                    }
                                                    busy = false
                                                }
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchHeaderCard(
    m: Match,
    ageGroupPlayersCount: Int = 0
) {
    GlassCard3D {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldPrimary.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.SportsSoccer,
                        null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        m.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    m.opponentTeam?.let {
                        Text(
                            "حریف: $it",
                            color = GoldPrimary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    val classInfo = listOfNotNull(
                        m.classTitle
                                ?: m.classItem?.title,
                        m.ageGroupTitle
                                ?: m.ageGroup?.title
                    ).joinToString(" - ")
                    if (classInfo.isNotBlank()) Text(
                        classInfo,
                        color = Color.White.copy(0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (ageGroupPlayersCount > 0) Text(
                        "بازیکنان گروه سنی: ${DateUtils.toPersianDigits(ageGroupPlayersCount.toString())} نفر",
                        color = GoldPrimary.copy(0.6f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                StatusBadge(m.status)
            }
            if (m.hasResult) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GreenSuccess.copy(0.15f))
                        .border(
                            0.5.dp,
                            GreenSuccess.copy(0.25f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "نتیجه: ${DateUtils.toPersianDigits(m.resultText ?: "")}",
                        color = GreenSuccess,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchPlayerCard(
    mp: MatchPlayer,
    displayName: String,
    isFinished: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard3D(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldPrimary.copy(0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            DateUtils.toPersianDigits(
                                mp.jerseyNumber?.toString()
                                        ?: "?"
                            ),
                            color = GoldPrimary,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            displayName,
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            mp.position?.takeIf { it.isNotBlank() }
                                ?.let {
                                    Box(
                                        Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(0.08f))
                                            .padding(
                                                horizontal = 6.dp,
                                                vertical = 2.dp
                                            )
                                    ) {
                                        Text(
                                            it,
                                            color = Color.White.copy(0.7f),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            mp.attendanceStatus?.let {
                                val (txt, col) = when (it) {
                                    "present" -> "حاضر" to GreenSuccess
                                    "absent"  -> "غایب" to RedError
                                    "late"    -> "تأخیر" to GoldPrimary
                                    "injured" -> "مصدوم" to RedError
                                    else      -> it to Color.White.copy(0.6f)
                                }
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(col.copy(0.15f))
                                        .padding(
                                            horizontal = 6.dp,
                                            vertical = 2.dp
                                        )
                                ) {
                                    Text(
                                        txt,
                                        color = col,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                            if (isFinished && (mp.goals > 0 || mp.assists > 0)) {
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GreenSuccess.copy(0.15f))
                                        .padding(
                                            horizontal = 6.dp,
                                            vertical = 2.dp
                                        )
                                ) {
                                    Text(
                                        "گل:${DateUtils.toPersianDigits(mp.goals.toString())} پاس:${DateUtils.toPersianDigits(mp.assists.toString())}",
                                        color = GreenSuccess,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            if (isFinished) "ثبت آمار" else "ویرایش",
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            "حذف",
                            tint = RedError.copy(0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StatBadge(
                    "گل",
                    mp.goals
                )
                StatBadge(
                    "پاس",
                    mp.assists
                )
                if (mp.yellowCards > 0) StatBadge(
                    "زرد",
                    mp.yellowCards,
                    RedError.copy(0.3f)
                )
                if (mp.redCards > 0) StatBadge(
                    "قرمز",
                    mp.redCards,
                    RedError
                )
                mp.minutesPlayed?.let {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(0.08f))
                            .padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                    ) {
                        Text(
                            "${DateUtils.toPersianDigits(it.toString())} دقیقه",
                            color = Color.White.copy(0.7f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                mp.rating?.let {
                    Row(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GoldPrimary.copy(0.15f))
                            .padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            DateUtils.toPersianDigits(it.toString()),
                            color = GoldPrimary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            mp.notes?.takeIf { it.isNotBlank() }
                ?.let {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(0.05f))
                            .padding(8.dp)
                    ) {
                        Text(
                            it,
                            color = Color.White.copy(0.75f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
        }
    }
}

@Composable
private fun StatBadge(
    label: String,
    value: Int,
    bg: Color = Color.White.copy(0.08f)
) {
    Box(
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
    ) {
        Text(
            "$label: ${DateUtils.toPersianDigits(value.toString())}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(0.85f)
        )
    }
}

@Composable
private fun StatusBadge(status: String?) {
    val (text, color) = when (status) {
        "planned"               -> "برنامه‌ریزی" to GoldPrimary
        "confirmed"             -> "تأیید شده" to GoldPrimary
        "finished", "completed" -> "پایان یافته" to GreenSuccess
        "cancelled"             -> "لغو شده" to RedError
        else                    -> (status
                ?: "نامشخص") to Color.White.copy(0.6f)
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(0.25f))
            .padding(
                horizontal = 10.dp,
                vertical = 4.dp
            )
    ) {
        Text(
            text,
            color = color,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}
