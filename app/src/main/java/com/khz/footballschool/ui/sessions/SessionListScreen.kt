package com.khz.footballschool.ui.sessions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khz.footballschool.core.util.DateUtils
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GenericListScreen
import com.khz.footballschool.ui.components.InfoCard
import com.khz.footballschool.ui.theme.GoldPrimary

@Composable
fun SessionListScreen(onSessionClick: (Int) -> Unit) {
    val vm: SessionListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    val mapped = when (state) {
        is SessionListState.Loading -> com.khz.footballschool.ui.components.ListState.Loading
        is SessionListState.Error   -> com.khz.footballschool.ui.components.ListState.Error((state as SessionListState.Error).message)
        is SessionListState.Success -> com.khz.footballschool.ui.components.ListState.Success((state as SessionListState.Success).sessions)
    }

    GenericListScreen(
        title = "جلسات تمرین",
        state = mapped,
        onRefresh = { vm.load() }) { session ->
        InfoCard(
            title = session.classItem?.title
                    ?: "کلاس -",
            subtitle = " ${session.topic ?: "-"}",
            trailing = session.status
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    androidx.compose.ui.Alignment.End
                )
            ) {
                if (session.status == "scheduled") {
                    TextButton(onClick = { vm.cancelSession(session.id) }) {
                        Text(
                            "لغو",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    TextButton(onClick = { vm.completeSession(session.id) }) {
                        Text(
                            "پایان",
                            color = GoldPrimary
                        )
                    }
                }
                TextButton(onClick = { onSessionClick(session.id) }) {
                    Text(
                        "حضور و غیاب",
                        color = GoldPrimary
                    )
                }
            }
        }
    }
}