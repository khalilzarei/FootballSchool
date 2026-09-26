package com.khz.malekadmin.ui.matches

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.CircularProgressIndicator
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
import com.khz.malekadmin.MalekAdminApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.core.util.DateUtils
import com.khz.malekadmin.domain.model.Match
import com.khz.malekadmin.ui.components.GlassBackground
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary
import com.khz.malekadmin.ui.theme.GreenSuccess
import com.khz.malekadmin.ui.theme.RedError
import kotlinx.coroutines.launch

@Composable
fun MatchDetailScreen(
    matchId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    onPlayers: (Int) -> Unit,
    onSetResult: (Int) -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as MalekAdminApp).container
    val repo = container.matchRepository
    val scope = rememberCoroutineScope()

    var match by remember { mutableStateOf<Match?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    fun load() {
        scope.launch {
            loading = true
            when (val r = repo.getMatch(matchId)) {
                is NetworkResult.Success -> match = r.data
                is NetworkResult.Error   -> error = r.message
                else                     -> {}
            }
            loading = false
        }
    }

    LaunchedEffect(matchId) { load() }

    GlassBackground {
        Box(Modifier.fillMaxSize()) {
            GlassTopBar(
                title = match?.title
                        ?: "جزئیات مسابقه",
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onEdit(matchId) }) {
                        Icon(
                            Icons.Default.Edit,
                            "ویرایش",
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
                match?.let { m ->
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.padding(top = 56.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            GlassCard3D() {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            Modifier.size(48.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(GoldPrimary.copy(0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.SportsSoccer,
                                                null,
                                                tint = GoldPrimary,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                m.title,
                                                color = Color.White,
                                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                            )
                                            m.opponentTeam?.let {
                                                Text(
                                                    "حریف: $it",
                                                    color = GoldPrimary,
                                                    style = MaterialTheme.typography.bodyMedium
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
                                        }
                                        StatusBadge(m.status)
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.CalendarMonth,
                                                null,
                                                tint = Color.White.copy(0.5f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            val jalali = DateUtils.gregorianToJalali(m.matchDate)
                                                .ifEmpty { m.matchDate }
                                            Text(
                                                "${DateUtils.toPersianDigits(jalali)} - ${DateUtils.toPersianDigits(m.matchTime?.take(5) ?: "")}",
                                                color = Color.White.copy(0.85f),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    m.location?.let {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.LocationOn,
                                                null,
                                                tint = Color.White.copy(0.5f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                it,
                                                color = Color.White.copy(0.7f),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    m.notes?.takeIf { it.isNotBlank() }
                                        ?.let {
                                            Box(
                                                Modifier.fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color.White.copy(0.06f))
                                                    .padding(10.dp)
                                            ) {
                                                Text(
                                                    it,
                                                    color = Color.White.copy(0.8f),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }

                                    if (m.hasResult) {
                                        Box(
                                            Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(GreenSuccess.copy(0.15f))
                                                .border(
                                                    0.5.dp,
                                                    GreenSuccess.copy(0.25f),
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .padding(12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    "نتیجه نهایی",
                                                    color = GreenSuccess.copy(0.7f),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                                Text(
                                                    DateUtils.toPersianDigits(
                                                        m.resultText
                                                                ?: m.result
                                                                ?: ""
                                                    ),
                                                    color = GreenSuccess,
                                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GlassButton(
                                    text = "ثبت نتیجه",
                                    onClick = { onSetResult(m.id) },
                                    modifier = Modifier.weight(1f)
                                )
                                GlassButton(
                                    text = "بازیکنان (${DateUtils.toPersianDigits(m.players.size.toString())})",
                                    onClick = { onPlayers(m.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        if (m.players.isNotEmpty()) {
                            item {
                                Text(
                                    "گلزنان و آمار:",
                                    color = Color.White.copy(0.7f),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            items(m.players.size) { idx ->
                                val mp = m.players[idx]
                                GlassCard3D() {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                mp.player?.fullName
                                                        ?: "بازیکن #${DateUtils.toPersianDigits(mp.playerId.toString())}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                if (mp.goals > 0) Box(
                                                    Modifier.clip(RoundedCornerShape(6.dp))
                                                        .background(GreenSuccess.copy(0.15f))
                                                        .padding(
                                                            horizontal = 6.dp,
                                                            vertical = 2.dp
                                                        )
                                                ) {
                                                    Text(
                                                        "گل: ${DateUtils.toPersianDigits(mp.goals.toString())}",
                                                        color = GreenSuccess,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                                if (mp.assists > 0) Box(
                                                    Modifier.clip(RoundedCornerShape(6.dp))
                                                        .background(GoldPrimary.copy(0.15f))
                                                        .padding(
                                                            horizontal = 6.dp,
                                                            vertical = 2.dp
                                                        )
                                                ) {
                                                    Text(
                                                        "پاس: ${DateUtils.toPersianDigits(mp.assists.toString())}",
                                                        color = GoldPrimary,
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                                mp.position?.let {
                                                    Text(
                                                        it,
                                                        color = Color.White.copy(0.5f),
                                                        style = MaterialTheme.typography.labelSmall
                                                    )
                                                }
                                            }
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                mp.invitationStatus
                                                        ?: "-",
                                                color = GoldPrimary,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                            mp.rating?.let {
                                                Text(
                                                    "امتیاز: ${DateUtils.toPersianDigits(it.toString())}",
                                                    color = Color.White.copy(0.6f),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GlassButton(
                                    text = "لغو مسابقه",
                                    onClick = {
                                        scope.launch {
                                            busy = true
                                            when (val r = repo.cancelMatch(m.id)) {
                                                is NetworkResult.Success -> {
                                                    load()
                                                }

                                                is NetworkResult.Error   -> error = r.message
                                                else                     -> {}
                                            }
                                            busy = false
                                        }
                                    },
                                    enabled = !busy && m.status != "cancelled",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            error?.let {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    it,
                                    color = Color(0xFFFF8A80),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(Modifier.height(40.dp))
                        }
                    }
                }
                        ?: run {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(top = 56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    error
                                            ?: "مسابقه یافت نشد",
                                    color = Color.White.copy(0.6f)
                                )
                            }
                        }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String?) {
    val (text, color) = when (status) {
        "planned"               -> "برنامه‌ریزی" to GoldPrimary
        "confirmed"             -> "تأیید شده" to GoldPrimary
        "finished", "completed" -> "برگزارشده" to GreenSuccess
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
