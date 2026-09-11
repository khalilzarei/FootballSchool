package com.khz.footballschool.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.domain.model.ChatRoom
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ─── بخش پیام‌های دریافتی ───
@Composable
fun RecentMessagesSection(
    onViewAll: () -> Unit,
    onOpenChat: (Int) -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val chatRepository = container.chatRepository

    var chatRooms by remember { mutableStateOf<List<ChatRoom>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
                when (val r = chatRepository.getRooms()) {
                    is NetworkResult.Success -> chatRooms = r.data
                    else                     -> {}
                }
                withContext(Dispatchers.Main) {
                    loading = false
                }
            }
    }

    GlassCard3D {
        Column {
            // هدر بخش
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "پیام‌های دریافتی",
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldPrimary
                )
                TextButton(onClick = onViewAll) {
                    Text(
                        "مشاهده همه",
                        color = GoldPrimary
                    )
                    Icon(
                        Icons.Default.ChevronLeft,
                        null,
                        tint = GoldPrimary,
                        modifier = Modifier.width(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = GoldPrimary,
                        modifier = Modifier.width(24.dp)
                    )
                }
            } else if (chatRooms.isEmpty()) {
                Text(
                    "پیامی دریافت نشده است",
                    color = Color.White.copy(0.6f),
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                // نمایش حداکثر ۵ پیام آخر
                chatRooms.take(5)
                    .forEach { room ->
                        ChatPreviewItem(
                            room = room,
                            onClick = { onOpenChat(room.id) })
                        Spacer(Modifier.height(6.dp))
                    }
            }
        }
    }
}

@Composable
private fun ChatPreviewItem(
    room: ChatRoom,
    onClick: () -> Unit
) {
    GlassCard3D(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // آواتار یا آیکون
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                GoldPrimary.copy(0.8f),
                                GoldPrimary.copy(0.2f)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Chat,
                    null,
                    tint = Color.White,
                    modifier = Modifier.width(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // متن پیام
            Column(Modifier.weight(1f)) {
                Text(
                    text = room.subject
                            ?: "گفتگو",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = room.lastMessage?.body
                            ?: "بدون پیام",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // تعداد پیام‌های خوانده نشده
            if (room.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .background(
                            Color(0xFFE53935),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = room.unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}