package com.khz.malekadmin.ui.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.ui.components.GenericListScreen
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.components.InfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(onBack: () -> Unit) {
    val vm: NotificationListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { GlassTopBar(title = "اعلان‌ها", onBack = onBack) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            GenericListScreen(
                title = "اعلان‌ها",
                state = state,
                onRefresh = vm::refresh
            ) { n ->
                InfoCard(
                    title = n.title,
                    subtitle = n.body,
                    trailing = if (n.isRead) "خوانده" else "جدید"
                )
            }
        }
    }
}