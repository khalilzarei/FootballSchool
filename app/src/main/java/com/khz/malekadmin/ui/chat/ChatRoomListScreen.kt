package com.khz.malekadmin.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.MalekAdminApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.domain.model.ChatRoom
import com.khz.malekadmin.ui.components.AvatarView
import com.khz.malekadmin.ui.components.GenericListScreen
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.ListState
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

/**
 * لیست گفتگوها (اپ ادمین).
 *
 * - دکمه + در نوار بالا: شروع گفتگوی جدید (مخاطبین)
 * - FAB: ساخت گروه گفتگو
 * - long-press روی گفتگو: مدیریت (قفل/باز، تغییر عنوان، حذف) — فقط ادمین
 */
@Composable
fun ChatRoomListScreen(
    onBack: () -> Unit,
    onOpenChat: (Int) -> Unit,
    onOpenContacts: () -> Unit,
    onOpenCreateGroup: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val container = (context.applicationContext as MalekAdminApp).container

    val chatRepo = container.chatRepository
    val scope = rememberCoroutineScope()

    var state by remember {
        mutableStateOf<ListState<ChatRoom>>(ListState.Loading)
    }

    /* گفتگوی انتخاب‌شده برای منوی مدیریت */
    var manageRoom by remember {
        mutableStateOf<ChatRoom?>(null)
    }

    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(error) {
        if (error != null) {
            kotlinx.coroutines.delay(3000)
            error = null
        }
    }

    fun reload() {
        state = ListState.Loading

        scope.launch {
            state = when (val result = chatRepo.getRooms()) {
                is NetworkResult.Success -> {/*
                     * گفتگویی که آخرین پیام را داشته
                     * بالای لیست بیاید.
                     */
                    ListState.Success(
                        result.data.sortedByDescending { room ->
                            room.lastMessage?.createdAt.orEmpty()
                        })
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

    fun runManage(action: suspend () -> Unit) {
        scope.launch {
            busy = true
            error = null
            try {
                action()
            } catch (_: Exception) {
            } finally {
                busy = false
                manageRoom = null
                reload()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GenericListScreen(
            title = "گفتگوها",
            state = state,
            onRefresh = { reload() },
            onBack = onBack,
            onAdd = onOpenContacts
        ) { room ->

            ChatRoomItem(
                room = room,
                onClick = { onOpenChat(room.id) },
                onLongClick = { manageRoom = room })
        }

        /* ساخت گروه گفتگو */
        FloatingActionButton(
            onClick = onOpenCreateGroup,
            containerColor = GoldPrimary.copy(alpha = 0.85f),
            contentColor = Color(0xFF1A0533),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = "ساخت گروه گفتگو"
            )
        }

        /* منوی مدیریت گفتگو — فقط ادمین */
        manageRoom?.let { room ->
            ManageRoomDialog(
                room = room,
                busy = busy,
                error = error,
                onLock = {
                    runManage {
                        val result = chatRepo.lockRoom(room.id)
                        if (result is NetworkResult.Error) error = result.message
                    }
                },
                onUnlock = {
                    runManage {
                        val result = chatRepo.unlockRoom(room.id)
                        if (result is NetworkResult.Error) error = result.message
                    }
                },
                onRename = { newTitle ->
                    runManage {
                        val result = chatRepo.updateRoom(
                            room.id,
                            newTitle,
                            null
                        )
                        if (result is NetworkResult.Error) error = result.message
                    }
                },
                onDelete = {
                    runManage {
                        val result = chatRepo.deleteRoom(room.id)
                        if (result is NetworkResult.Error) error = result.message
                    }
                },
                onDismiss = { manageRoom = null })
        }
    }
}

@Composable
private fun ChatRoomItem(
    room: ChatRoom,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val title = room.title.takeIf { it.isNotBlank() }
            ?: "گفتگو"

    val avatarUrl = room.image?.takeIf { it.isNotBlank() }

    GlassCard3D(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
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

                    /* وضعیت قفل */
                    if (room.isLocked) {
                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "قفل شده",
                            tint = Color(0xFFFF8A80),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(text = room.lastMessage?.body?.takeIf { it.isNotBlank() }
                        ?: "بدون پیام",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp))
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

/**
 * منوی مدیریت گفتگو (قفل/باز، تغییر عنوان، حذف)
 */
@Composable
private fun ManageRoomDialog(
    room: ChatRoom,
    busy: Boolean,
    error: String?,
    onLock: () -> Unit,
    onUnlock: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var showRename by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf(room.title) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "مدیریت گفتگو",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = room.title,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (error != null) {
                    Text(
                        text = error,
                        color = Color(0xFFFF8A80),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (busy) {
                    CircularProgressIndicator(
                        color = GoldPrimary,
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Column {
                if (room.isLocked) {
                    TextButton(onClick = { if (!busy) onUnlock() }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.LockOpen,
                                null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "باز کردن قفل",
                                color = GoldPrimary
                            )
                        }
                    }
                } else {
                    TextButton(onClick = { if (!busy) onLock() }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Lock,
                                null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "قفل گفتگو",
                                color = GoldPrimary
                            )
                        }
                    }
                }

                TextButton(onClick = { showRename = true }) {
                    Text(
                        "تغییر عنوان",
                        color = GoldPrimary
                    )
                }

                TextButton(onClick = { showDeleteConfirm = true }) {
                    Text(
                        "حذف گفتگو",
                        color = Color(0xFFFF8A80)
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        "بستن",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        })

    /* دیالوگ تغییر عنوان */
    if (showRename) {
        AlertDialog(
            onDismissRequest = { showRename = false },
            title = {
                Text(
                    "تغییر عنوان",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val t = newTitle.trim()
                        if (t.isNotEmpty()) {
                            showRename = false
                            onRename(t)
                        }
                    }) {
                    Text(
                        "ثبت",
                        color = GoldPrimary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showRename = false }) {
                    Text(
                        "انصراف",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            })
    }

    /* تأیید حذف */
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = {
                Text(
                    "حذف گفتگو",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "این گفتگو حذف (غیرفعال) می‌شود. ادامه می‌دهید؟",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(onClick = onDelete) {
                    Text(
                        "حذف",
                        color = Color(0xFFFF8A80)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(
                        "انصراف",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            })
    }
}
