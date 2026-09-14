package com.khz.footballschool.ui.chat

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.domain.model.ChatMessage
import com.khz.footballschool.ui.components.AvatarView
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val chatRepo = container.chatRepository
    val userRepo = container.userRepository
    val scope = rememberCoroutineScope()

    // State ها
    var roomId by remember { mutableStateOf<Int?>(null) }
    var userName by remember { mutableStateOf<String?>(null) }
    var userAvatar by remember { mutableStateOf<String?>(null) }
    var userMobile by remember { mutableStateOf<String?>(null) }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var sending by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()

    // شناسه کاربر لاگین‌شده فعلی (برای تشخیص پیام‌های خود کاربر)
    var currentUserId by remember { mutableStateOf<Int?>(null) }

    // ─── بارگذاری اطلاعات کاربر و ایجاد/دریافت room ───
    LaunchedEffect(userId) {
        loading = true
        error = null

        // شناسه کاربر جاری
        currentUserId = container.sessionManager.userId.first()
            ?.toIntOrNull()

        // دریافت اطلاعات کاربر
        when (val r = userRepo.getUser(userId)) {
            is NetworkResult.Success -> {
                userName = r.data.fullName
                userAvatar = r.data.avatarUrl
                userMobile = r.data.mobile
            }

            else                     -> userName = "کاربر #$userId"
        }

        // ایجاد یا دریافت اتاق خصوصی
        when (val r = chatRepo.getOrCreatePrivateRoomWithUser(
            userId,
            roomType = ""
        )) {
            is NetworkResult.Success -> {
                roomId = r.data.id

                // دریافت پیام‌های room
                when (val msgResult = chatRepo.getMessages(
                    r.data.id,
                    limit = 50
                )) {
                    is NetworkResult.Success -> {
                        messages = msgResult.data
                        // ثبت خوانده‌شدن پیام‌ها تا آخرین پیام (شمارنده «جدید» صفر شود)
                        msgResult.data.maxOfOrNull { it.id }
                            ?.let { lastId ->
                                chatRepo.markAsRead(
                                    r.data.id,
                                    lastId
                                )
                            }
                    }

                    is NetworkResult.Error   -> error = msgResult.message
                    else                     -> {}
                }
            }

            is NetworkResult.Error   -> error = r.message
            else                     -> {}
        }

        loading = false
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = userName
                        ?: "گفتگو",
                onBack = onBack,
                actions = {
                    // دکمه تماس (اگر شماره موبایل دارد)
                    if (!userMobile.isNullOrBlank()) {
                        IconButton(onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$userMobile")
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                            }
                        }) {
                            Icon(
                                Icons.Default.Call,
                                "تماس",
                                tint = GoldPrimary
                            )
                        }
                    }
                })
        }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
        ) {
            // ─── هدر با اطلاعات کاربر ───
            GlassCard3D(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarView(
                        name = userName
                                ?: "?",
                        avatarUrl = userAvatar,
                        size = 56.dp,
                        accentColor = GoldPrimary
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            userName
                                    ?: "در حال بارگذاری...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "آنلاین",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF81C784)
                        )
                    }
                }
            }

            // ─── محتوای اصلی ───
            Box(Modifier.weight(1f)) {
                when {
                    loading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GoldPrimary)
                    }

                    error != null && messages.isEmpty() -> Box(
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                error!!,
                                color = Color(0xFFFF8A80),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(12.dp))
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        error = null
                                        loading = true

                                        // تلاش مجدد
                                        when (val r = chatRepo.getOrCreatePrivateRoomWithUser(
                                            userId,
                                            roomType = ""
                                        )) {
                                            is NetworkResult.Success -> {
                                                roomId = r.data.id
                                                when (val msgResult = chatRepo.getMessages(
                                                    r.data.id,
                                                    limit = 50
                                                )) {
                                                    is NetworkResult.Success -> {
                                                        messages = msgResult.data
                                                        msgResult.data.maxOfOrNull { it.id }
                                                            ?.let { lastId ->
                                                                chatRepo.markAsRead(
                                                                    r.data.id,
                                                                    lastId
                                                                )
                                                            }
                                                    }

                                                    is NetworkResult.Error   -> error = msgResult.message
                                                    else                     -> {}
                                                }
                                            }

                                            is NetworkResult.Error   -> error = r.message
                                            else                     -> {}
                                        }

                                        loading = false
                                    }
                                }) {
                                Text(
                                    "تلاش مجدد",
                                    color = GoldPrimary
                                )
                            }
                        }
                    }

                    messages.isEmpty() -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "هنوز پیامی رد و بدل نشده",
                                color = Color.White.copy(0.6f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "اولین پیام را ارسال کنید",
                                color = Color.White.copy(0.4f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    else -> LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { msg ->
                            MessageBubble(
                                message = msg,
                                isMine = msg.senderId == currentUserId
                            )
                        }
                    }
                }
            }

            // ─── نوار ارسال پیام ───
            roomId?.let { currentRoomId ->
                MessageInputBar(
                    text = messageText,
                    onTextChange = { messageText = it },
                    sending = sending,
                    onSend = {
                        if (messageText.isBlank() || sending) return@MessageInputBar
                        scope.launch {
                            sending = true
                            when (val r = chatRepo.sendMessage(
                                currentRoomId,
                                messageText.trim()
                            )) {
                                is NetworkResult.Success -> {
                                    messages = messages + r.data
                                    messageText = ""
                                    listState.animateScrollToItem(messages.size - 1)
                                }

                                is NetworkResult.Error   -> error = r.message
                                else                     -> {}
                            }
                            sending = false
                        }
                    })
            }
        }
    }

    // اسکرول خودکار به آخرین پیام
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    isMine: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        if (!isMine) {
            AvatarView(
                name = message.senderName
                        ?: "?",
                size = 32.dp,
                accentColor = Color(0xFF4FC3F7)
            )
            Spacer(Modifier.width(6.dp))
        }

        val shape = if (isMine) {
            RoundedCornerShape(
                18.dp,
                18.dp,
                4.dp,
                18.dp
            )
        } else {
            RoundedCornerShape(
                18.dp,
                18.dp,
                18.dp,
                4.dp
            )
        }

        val bgBrush = if (isMine) {
            Brush.linearGradient(
                listOf(
                    GoldPrimary.copy(alpha = 0.85f),
                    GoldPrimary.copy(alpha = 0.55f)
                )
            )
        } else {
            Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.15f),
                    Color.White.copy(alpha = 0.08f)
                )
            )
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    bgBrush,
                    shape
                )
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
        ) {
            Column {
                Text(
                    text = message.body.toString(),
                    color = if (isMine) Color(0xFF1A0533) else Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = message.createdAt?.takeLast(8)
                        ?.take(5)
                            ?: "",
                    color = if (isMine) Color(0xFF1A0533).copy(0.7f) else Color.White.copy(0.5f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        if (isMine) {
            Spacer(Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                GoldPrimary,
                                GoldPrimary.copy(0.4f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "م",
                    color = Color(0xFF1A0533),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MessageInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    sending: Boolean,
    onSend: () -> Unit
) {
    GlassCard3D(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        Color.White.copy(alpha = 0.08f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    )
            ) {
                if (text.isEmpty()) {
                    Text(
                        "پیام خود را بنویسید...",
                        color = Color.White.copy(0.4f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    cursorBrush = SolidColor(GoldPrimary),
                    singleLine = false
                )
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                GoldPrimary.copy(alpha = if (text.isNotBlank()) 0.95f else 0.3f),
                                GoldPrimary.copy(alpha = if (text.isNotBlank()) 0.5f else 0.15f)
                            )
                        )
                    )
                    .then(
                        if (text.isNotBlank() && !sending) {
                        Modifier.clickable { onSend() }
                    } else Modifier),
                contentAlignment = Alignment.Center) {
                if (sending) {
                    CircularProgressIndicator(
                        color = Color(0xFF1A0533),
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "ارسال",
                        tint = if (text.isNotBlank()) Color(0xFF1A0533) else Color.White.copy(0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}