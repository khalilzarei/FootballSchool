package com.khz.malekadmin.ui.dashboard.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.MalekAdminApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.domain.model.ChatRoom
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.AvatarView
import com.khz.malekadmin.ui.theme.GoldPrimary

@Composable
fun RecentMessagesSection(
    onViewAll: () -> Unit,
    onOpenChat: (Int) -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as MalekAdminApp).container

    val chatRepository = container.chatRepository

    var chatRooms by remember {
        mutableStateOf<List<ChatRoom>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        loading = true

        when (val result = chatRepository.getRooms()) {
            is NetworkResult.Success -> {/*
                 * گفتگویی که آخرین پیام را داشته
                 * بالا بیاید.
                 */
                chatRooms = result.data.sortedByDescending { room ->
                    room.lastMessage?.createdAt.orEmpty()
                }
            }

            is NetworkResult.Error   -> {
                chatRooms = emptyList()
            }

            is NetworkResult.Loading -> Unit
        }

        loading = false
    }

    GlassCard3D() {
        Column {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "پیام‌های دریافتی",
                        style = MaterialTheme.typography.titleMedium,
                        color = GoldPrimary
                    )

                    /*
                         * تعداد کل پیام‌های دریافتی‌نشده
                         * (فقط وقتی بیشتر از صفر است نمایش داده
                         * می‌شود).
                         */
                    val totalUnread = chatRooms.sumOf { it.unreadCount }

                    if (totalUnread > 0) {
                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Box(
                            modifier = Modifier.size(22.dp)
                                .background(
                                    color = Color(0xFFE53935),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (totalUnread > 99) {
                                    "99+"
                                } else {
                                    totalUnread.toString()
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }

                TextButton(
                    onClick = onViewAll
                ) {
                    Text(
                        text = "مشاهده همه",
                        color = GoldPrimary
                    )

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            when {

                loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                chatRooms.isEmpty() -> {
                    Text(
                        text = "پیامی دریافت نشده است",
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(8.dp)
                    )
                }

                else -> {

                    /*
                         * نمایش حداکثر ۲ گفتگوی فعال (جدیدترین‌ها)
                         */
                    chatRooms.filter { it.lastMessage != null }
                        .take(2)
                        .forEach { room ->

                            ChatPreviewItem(
                                room = room,
                                onClick = {
                                    // مدل جدید:
                                    // مستقیماً با roomId وارد ChatScreen می‌شویم.
                                    onOpenChat(room.id)
                                })

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )
                        }
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
    val title = room.title.takeIf { it.isNotBlank() }
            ?: "گفتگو"

    val lastMessage = room.lastMessage?.body?.takeIf { it.isNotBlank() }
            ?: "بدون پیام"

    GlassCard3D(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /*
                 * آواتار گفتگو:
                 * در گفتگوی خصوصی = آواتار کاربر مقابل،
                 * در گروه = عکس گروه (حرف اول نام به‌عنوان fallback).
                 */
            AvatarView(
                name = title,
                avatarUrl = room.image,
                size = 40.dp,
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
                        color = Color.White,
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

                Text(
                    text = lastMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (room.unreadCount > 0) {
                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Box(
                    modifier = Modifier.size(24.dp)
                        .background(
                            color = Color(0xFFE53935),
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
                        color = Color.White
                    )
                }
            }
        }
    }
}
