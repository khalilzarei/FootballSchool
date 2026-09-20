package com.khz.footballschool.ui.coaches

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GenericListScreen
import com.khz.footballschool.ui.components.InfoCard

@Composable
fun CoachListScreen(onBack: () -> Unit) {
    val vm: CoachListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    GenericListScreen(
        title = "مربیان",
        state = state,
        onRefresh = vm::refresh,
        onBack = onBack
    ) { c ->
        InfoCard(
            title = c.user.fullName,
            subtitle = c.specialty
                    ?: "-",
            trailing = c.licenseLevel
                    ?: "-"
        )
    }
}