package com.khz.footballschool.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.domain.model.ChatRoom
import com.khz.footballschool.ui.components.AvatarView
import com.khz.footballschool.ui.components.GenericListScreen
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.ListState
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@Composable
fun ChatRoomListScreen(
    onBack: () -> Unit,
    onOpenChat: (Int) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container

    val chatRepo = container.chatRepository
    val scope = rememberCoroutineScope()

    var state by remember {
        mutableStateOf<ListState<ChatRoom>>(ListState.Loading)
    }

    fun reload() {
        state = ListState.Loading

        scope.launch {
            state = when (val result = chatRepo.getRooms()) {
                is NetworkResult.Success -> {
                    ListState.Success(result.data)
                }

                is NetworkResult.Error   -> {
                    ListState.Error(result.message)
                }

                is NetworkResult.Loading -> {
                    ListState.Loading
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        reload()
    }

    GenericListScreen(
        title = "گفتگوها",
        state = state,
        onRefresh = { reload() },
        onBack = onBack
    ) { room ->

        ChatRoomItem(
            room = room,
            onClick = {
                // در مدل جدید، ChatScreen مستقیماً با roomId
                // اتاق موجود را باز می‌کند.
                onOpenChat(room.id)
            })
    }
}

@Composable
private fun ChatRoomItem(
    room: ChatRoom,
    onClick: () -> Unit
) {
    val title = room.title.takeIf { it.isNotBlank() }
            ?: "گفتگو"

    val avatarUrl = room.image?.takeIf { it.isNotBlank() }

    GlassCard3D(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AvatarView(
                name = title,
                avatarUrl = avatarUrl,
                size = 48.dp,
                accentColor = GoldPrimary
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (room.isGroup) {
                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(
                            text = "گروه",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldPrimary
                        )
                    }
                }

                Text(text = room.lastMessage?.body?.takeIf { it.isNotBlank() }
                        ?: "بدون پیام",
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp))
            }

            if (room.unreadCount > 0) {
                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = androidx.compose.ui.graphics.Color(0xFFE53935),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (room.unreadCount > 99) {
                            "99+"
                        } else {
                            room.unreadCount.toString()
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }
    }
}
