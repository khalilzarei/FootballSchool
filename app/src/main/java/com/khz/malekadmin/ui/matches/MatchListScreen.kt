package com.khz.malekadmin.ui.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.khz.malekadmin.core.util.DateUtils
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.domain.model.Match
import com.khz.malekadmin.ui.components.GlassBackground
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.components.ListState
import com.khz.malekadmin.ui.components.ErrorContent
import com.khz.malekadmin.ui.components.LoadingContent
import com.khz.malekadmin.ui.theme.GlassBorderSoft
import com.khz.malekadmin.ui.theme.GoldPrimary
import com.khz.malekadmin.ui.theme.GreenSuccess
import com.khz.malekadmin.ui.theme.RedError

private enum class MatchTab(val label: String) {
    Upcoming("پیش‌رو"),
    Past("برگزارشده"),
    All("همه")
}

@Composable
fun MatchListScreen(
    onBack: () -> Unit,
    onAdd: (() -> Unit)? = null,
    onDetail: ((Int) -> Unit)? = null
) {
    val vm: MatchListViewModel = appViewModel()
    val state by vm.state.collectAsState()
    val busTrigger by MatchRefreshBus.trigger.collectAsState()
    var tab by remember { mutableStateOf(MatchTab.All) }

    LaunchedEffect(busTrigger) {
        if (busTrigger != 0L) vm.refresh()
    }

    GlassBackground {
        Box(Modifier.fillMaxSize()) {
            GlassTopBar(
                title = "مسابقات",
                onBack = onBack,
                actions = {
                    if (onAdd != null) {
                        IconButton(onClick = onAdd) {
                            Icon(
                                Icons.Default.Add,
                                "افزودن",
                                tint = GoldPrimary
                            )
                        }
                    }
                })
            Column(Modifier.padding(top = 56.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabChip(
                        MatchTab.All.label,
                        tab == MatchTab.All,
                        { tab = MatchTab.All },
                        Modifier.weight(1f)
                    )
                    TabChip(
                        MatchTab.Upcoming.label,
                        tab == MatchTab.Upcoming,
                        { tab = MatchTab.Upcoming },
                        Modifier.weight(1f)
                    )
                    TabChip(
                        MatchTab.Past.label,
                        tab == MatchTab.Past,
                        { tab = MatchTab.Past },
                        Modifier.weight(1f)
                    )
                }
                when (state) {
                    is ListState.Loading -> Box(Modifier.fillMaxSize()) { LoadingContent() }
                    is ListState.Error   -> ErrorContent((state as ListState.Error).message) { vm.refresh() }
                    is ListState.Success -> {
                        val all = (state as ListState.Success<Match>).items
                        val filtered = when (tab) {
                            MatchTab.Upcoming -> all.filter { it.status == "planned" || it.status == "confirmed" }
                            MatchTab.Past     -> all.filter { it.status == "finished" || it.status == "completed" || it.status == "cancelled" }
                            MatchTab.All      -> all
                        }
                        if (filtered.isEmpty()) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.SportsSoccer,
                                        null,
                                        tint = Color.White.copy(0.3f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        "موردی برای نمایش وجود ندارد",
                                        color = Color.White.copy(0.6f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    filtered,
                                    key = { it.id }) { m ->
                                    MatchCard(
                                        m,
                                        onClick = { onDetail?.invoke(m.id) })
                                }
                                item { Spacer(Modifier.height(80.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) GoldPrimary.copy(0.3f) else Color.White.copy(0.08f))
            .border(
                0.5.dp,
                if (selected) GoldPrimary.copy(0.4f) else GlassBorderSoft,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = if (selected) GoldPrimary else Color.White.copy(0.7f),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        )
    }
}

@Composable
private fun MatchCard(
    m: Match,
    onClick: (() -> Unit)? = null
) {
    GlassCard3D(
        modifier = Modifier.clickable(onClick = { onClick?.invoke() }),
    ) {
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
                        Icon(
                            Icons.Default.SportsSoccer,
                            null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            m.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        if (!m.opponentTeam.isNullOrBlank()) Text(
                            "حریف: ${m.opponentTeam}",
                            color = Color.White.copy(0.7f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
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
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    val jalali = DateUtils.gregorianToJalali(m.matchDate)
                        .ifEmpty { m.matchDate }
                    Text(
                        DateUtils.toPersianDigits(jalali),
                        color = Color.White.copy(0.85f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                m.matchTime?.take(5)
                    ?.let {
                        Text(
                            DateUtils.toPersianDigits(it),
                            color = GoldPrimary,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
            }
            if (!m.location.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        tint = Color.White.copy(0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        m.location,
                        color = Color.White.copy(0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            val classInfo = listOfNotNull(
                m.classTitle
                        ?: m.classItem?.title,
                m.ageGroupTitle
                        ?: m.ageGroup?.title
            ).joinToString(" - ")
            if (classInfo.isNotBlank()) Text(
                classInfo,
                color = Color.White.copy(0.5f),
                style = MaterialTheme.typography.labelSmall
            )

            if (m.players.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(0.08f))
                            .padding(
                                horizontal = 6.dp,
                                vertical = 3.dp
                            )
                    ) {
                        Text(
                            "${DateUtils.toPersianDigits(m.players.size.toString())} بازیکن دعوت شده",
                            color = Color.White.copy(0.7f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    val goals = m.players.sumOf { it.goals }
                    if (goals > 0) Box(
                        Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GreenSuccess.copy(0.15f))
                            .padding(
                                horizontal = 6.dp,
                                vertical = 3.dp
                            )
                    ) {
                        Text(
                            "گل: ${DateUtils.toPersianDigits(goals.toString())}",
                            color = GreenSuccess,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            if (m.hasResult) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenSuccess.copy(0.15f))
                        .border(
                            0.5.dp,
                            GreenSuccess.copy(0.25f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "نتیجه: ${DateUtils.toPersianDigits(m.resultText ?: "")}",
                        color = GreenSuccess,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            } else if (!m.result.isNullOrBlank()) {
                Text(
                    "نتیجه: ${DateUtils.toPersianDigits(m.result)}",
                    color = Color.White.copy(0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (onClick != null) Text(
                "برای جزئیات لمس کنید",
                color = Color.White.copy(0.35f),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun StatusBadge(status: String?) {
    val (text, color) = when (status) {
        "planned"               -> "برنامه‌ریزی" to GoldPrimary
        "confirmed"             -> "تأیید شده" to GoldPrimary
        "finished", "completed" -> "برگزارشده" to GreenSuccess
        "cancelled"             -> "لغوشده" to RedError
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
