package com.khz.malekadmin.ui.seasons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.ui.components.GenericListScreen
import com.khz.malekadmin.ui.components.InfoCard

@Composable
fun SeasonListScreen(onBack: () -> Unit) {
    val vm: SeasonListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    GenericListScreen(
        title = "فصل‌ها",
        state = state,
        onRefresh = vm::refresh,
        onBack = onBack
    ) { s ->
        InfoCard(
            title = s.title,
            subtitle = "${s.startDate ?: "-"} تا ${s.endDate ?: "-"}",
            trailing = if (s.isActive) "فعال" else "غیرفعال"
        )
    }
}