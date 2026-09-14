package com.khz.footballschool.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.InfoCard
import com.khz.footballschool.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomListScreen(
    onBack: () -> Unit,
    onRoomClick: (Int) -> Unit
) {
    val viewModel: ChatRoomListViewModel = appViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "گفتگوها",
                onBack = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center
                        ),
                        color = GoldPrimary
                    )
                }

                state.error != null &&
                        state.rooms.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(
                            Alignment.Center
                        ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error
                                    ?: "خطا در دریافت گفتگوها",
                            color = Color.White
                        )

                        TextButton(
                            onClick = viewModel::refresh
                        ) {
                            Text("تلاش مجدد")
                        }
                    }
                }

                state.rooms.isEmpty() -> {
                    Text(
                        text = "هنوز گفتگویی وجود ندارد",
                        modifier = Modifier.align(
                            Alignment.Center
                        ),
                        color = Color.White.copy(
                            alpha = 0.7f
                        )
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = state.rooms,
                            key = { room -> room.id }
                        ) { room ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onRoomClick(room.id)
                                    }
                            ) {
                                InfoCard(
                                    title = room.subject
                                            ?: room.roomType.toChatTitle(),
                                    subtitle = room.lastMessage
                                        ?.body
                                            ?: "هنوز پیامی ارسال نشده",
                                    trailing = room.unreadCount
                                        .takeIf { it > 0 }
                                        ?.let { "$it جدید" }
                                        .orEmpty()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun String.toChatTitle(): String {
    return when (this) {
        "guardian_admin" -> "گفتگو با مدیریت"
        "coach_admin" -> "گفتگو با مدیریت"
        "guardian_coach" -> "گفتگو با مربی"
        else -> "گفتگو"
    }
}