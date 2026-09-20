package com.khz.footballschool.ui.matches

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GenericListScreen
import com.khz.footballschool.ui.components.InfoCard

@Composable
fun MatchListScreen(onBack: () -> Unit) {
    val vm: MatchListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    GenericListScreen(
        title = "مسابقات",
        state = state,
        onRefresh = vm::refresh,
        onBack = onBack
    ) { m ->
        val score = if (m.homeScore != null && m.awayScore != null) "${m.homeScore} - ${m.awayScore}" else ""
        InfoCard(
            title = m.title,
            subtitle = "${m.opponentTeam ?: "-"} | ${m.matchDate}",
            trailing = score.ifEmpty { m.status }
        )
    }
}