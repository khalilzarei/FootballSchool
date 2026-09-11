package com.khz.footballschool.ui.chat

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
fun ChatRoomListScreen(onBack: () -> Unit) {
    val vm: ChatRoomListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { GlassTopBar(title = "گفتگوها", onBack = onBack) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            GenericListScreen(
                title = "گفتگوها",
                state = state,
                onRefresh = vm::refresh
            ) { room ->
                InfoCard(
                    title = room.subject ?: "گفتگو",
                    subtitle = room.lastMessage?.body ?: "بدون پیام",
                    trailing = if (room.unreadCount > 0) "${room.unreadCount} جدید" else ""
                )
            }
        }
    }
}