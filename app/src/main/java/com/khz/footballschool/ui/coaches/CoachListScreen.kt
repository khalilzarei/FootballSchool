package com.khz.footballschool.ui.coaches

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GenericListScreen
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.InfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoachListScreen(onBack: () -> Unit) {
    val vm: CoachListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { GlassTopBar(title = "مربیان", onBack = onBack) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            GenericListScreen(
                title = "مربیان",
                state = state,
                onRefresh = vm::refresh
            ) { c ->
                InfoCard(
                    title = c.user.fullName,
                    subtitle = c.specialty ?: "-",
                    trailing = c.licenseLevel ?: "-"
                )
            }
        }
    }
}