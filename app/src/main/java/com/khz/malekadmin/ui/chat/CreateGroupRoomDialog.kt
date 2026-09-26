package com.khz.malekadmin.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.khz.malekadmin.MalekAdminApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.domain.model.ChatContact
import com.khz.malekadmin.ui.components.AvatarView
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@Composable
fun CreateGroupRoomDialog(
    onDismiss: () -> Unit,
    onCreated: (roomId: Int) -> Unit
) {
    val context = LocalContext.current
    val chatRepo = remember {
        (context.applicationContext as MalekAdminApp).container.chatRepository
    }
    val scope = rememberCoroutineScope()

    val dialogBackground = Color(0xFF1E1E1E)
    val errorColor = Color(0xFFFF8A80)

    var title by remember { mutableStateOf("") }
    var contacts by remember { mutableStateOf<List<ChatContact>?>(null) }
    var selected by remember { mutableStateOf(setOf<Int>()) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var saveError by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var reloadKey by remember { mutableStateOf(0) }

    LaunchedEffect(reloadKey) {
        loadError = null
        contacts = null
        when (val result = chatRepo.getContacts()) {
            is NetworkResult.Success -> contacts = result.data
            is NetworkResult.Error   -> loadError = result.message
            is NetworkResult.Loading -> Unit
        }
    }

    fun submit() {
        val t = title.trim()
        if (t.isEmpty()) {
            saveError = "عنوان گروه الزامی است"
            return
        }
        if (selected.isEmpty()) {
            saveError = "حداقل یک عضو انتخاب کنید"
            return
        }
        scope.launch {
            busy = true
            saveError = null
            when (val result = chatRepo.createGroupRoom(
                t,
                selected.toList()
            )) {
                is NetworkResult.Success -> {
                    busy = false
                    onCreated(result.data.id)
                }

                is NetworkResult.Error   -> {
                    saveError = result.message
                    busy = false
                }

                is NetworkResult.Loading -> Unit
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },

        // دکمه‌ی تأیید
        confirmButton = {
            TextButton(
                onClick = { submit() },
                enabled = !busy && contacts != null && loadError == null
            ) {
                if (busy) {
                    CircularProgressIndicator(
                        color = GoldPrimary,
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "ساخت گروه",
                        color = GoldPrimary
                    )
                }
            }
        },

        // دکمه‌ی انصراف
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !busy
            ) {
                Text(
                    text = "انصراف",
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        },

        // عنوان
        title = {
            Text(
                text = "ساخت گروه گفتگو",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },

        // محتوا
        text = {
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (saveError != null) saveError = null
                    },
                    placeholder = {
                        Text(
                            "عنوان گروه (مثلاً: نونهالان)",
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    singleLine = true,
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "اعضا (بازیکنان و مربیان فعال)",
                    color = GoldPrimary,
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(Modifier.height(6.dp))

                when {
                    contacts == null && loadError == null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GoldPrimary)
                        }
                    }

                    loadError != null                     -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = loadError
                                        ?: "خطا در دریافت مخاطبین",
                                color = errorColor,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(8.dp))
                            TextButton(
                                onClick = { reloadKey++ },
                                enabled = !busy
                            ) {
                                Text(
                                    "تلاش مجدد",
                                    color = GoldPrimary
                                )
                            }
                        }
                    }

                    else                                  -> {
                        val list = contacts.orEmpty()
                        if (list.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "مخاطبی برای افزودن وجود ندارد",
                                    color = Color.White.copy(alpha = 0.5f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(
                                    items = list,
                                    key = { it.userId }) { contact ->
                                    val checked = contact.userId in selected
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !busy) {
                                                selected = if (checked) {
                                                    selected - contact.userId
                                                } else {
                                                    selected + contact.userId
                                                }
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (checked) {
                                                Icons.Default.CheckCircle
                                            } else {
                                                Icons.Default.RadioButtonUnchecked
                                            },
                                            contentDescription = null,
                                            tint = if (checked) {
                                                GoldPrimary
                                            } else {
                                                Color.White.copy(alpha = 0.4f)
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )

                                        Spacer(Modifier.width(10.dp))

                                        AvatarView(
                                            name = contact.fullName
                                                    ?: "?",
                                            avatarUrl = contact.avatarUrl,
                                            size = 32.dp
                                        )

                                        Spacer(Modifier.width(10.dp))

                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                text = contact.fullName
                                                        ?: "",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = if (contact.role == "coach") "مربی" else "بازیکن",
                                                color = Color.White.copy(alpha = 0.5f),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (saveError != null) {
                    Text(
                        text = saveError!!,
                        color = errorColor,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        },

        // آیکون (اختیاری)
        icon = null,

        // شکل دیالوگ
        shape = RoundedCornerShape(24.dp),

        // رنگ پس‌زمینه
        containerColor = dialogBackground,

        // رنگ آیکون
        iconContentColor = GoldPrimary,

        // رنگ عنوان
        titleContentColor = Color.White,

        // رنگ متن
        textContentColor = Color.White,

        // ارتفاع تُنال (سایه)
        tonalElevation = 6.dp,

        // ویژگی‌های دیالوگ
        properties = DialogProperties(
            dismissOnBackPress = !busy,
            dismissOnClickOutside = !busy,
            usePlatformDefaultWidth = true
        ),

        // مودیفایر (اختیاری)
        modifier = Modifier
    )
}