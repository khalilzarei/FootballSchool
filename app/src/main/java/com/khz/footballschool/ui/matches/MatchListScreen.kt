package com.khz.footballschool.ui.matches

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.khz.footballschool.core.util.DateUtils
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GenericListScreen
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.InfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchListScreen(onBack: () -> Unit) {
    val vm: MatchListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { GlassTopBar(title = "مسابقات", onBack = onBack) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            GenericListScreen(
                title = "مسابقات",
                state = state,
                onRefresh = vm::refresh
            ) { m ->
                val score = if (m.homeScore != null && m.awayScore != null) "${m.homeScore} - ${m.awayScore}" else ""
                InfoCard(
                    title = m.title,
                    subtitle = "${m.opponentTeam ?: "-"} | ${m.matchDate}",
                    trailing = score.ifEmpty { m.status }
                )
            }
        }
    }
}