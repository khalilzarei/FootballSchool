package com.khz.footballschool.ui.chat

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.domain.model.ChatMessage
import com.khz.footballschool.domain.model.ChatRoom
import com.khz.footballschool.ui.components.AvatarView
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    roomId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container

    val chatRepo = container.chatRepository
    val scope = rememberCoroutineScope()

    var room by remember {
        mutableStateOf<ChatRoom?>(null)
    }

    var messages by remember {
        mutableStateOf<List<ChatMessage>>(emptyList())
    }

    var messageText by remember {
        mutableStateOf("")
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var sending by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var currentUserId by remember {
        mutableStateOf<Int?>(null)
    }

    var userMobile by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * Avatar and name of the current user (for one's own messages)
     */
    var myAvatarUrl by remember {
        mutableStateOf<String?>(null)
    }

    var myName by remember {
        mutableStateOf<String?>(null)
    }

    val listState = rememberLazyListState()

    /*
     * Load the ID of the current user + avatar
     */
    LaunchedEffect(Unit) {
        currentUserId = container.sessionManager.userId.first()
            ?.toIntOrNull()

        container.authRepository.getCurrentUser()
            .let { result ->
                if (result is NetworkResult.Success) {
                    myAvatarUrl = result.data.avatarUrl
                    myName = result.data.fullName
                }
            }
    }

    /*
     * Fetch room information
     */
    suspend fun loadRoom() {
        error = null

        when (val result = chatRepo.getRoom(roomId)) {
            is NetworkResult.Success -> {
                room = result.data
            }

            is NetworkResult.Error   -> {
                error = result.message
            }

            is NetworkResult.Loading -> Unit
        }
    }

    /*
     * Fetch messages
     */
    suspend fun loadMessages() {
        when (val result = chatRepo.getMessages(
            roomId = roomId,
            limit = 50
        )) {
            is NetworkResult.Success -> {
                messages = result.data

                result.data.maxOfOrNull { it.id }
                    ?.let { lastMessageId ->
                        chatRepo.markAsRead(
                            roomId,
                            lastMessageId
                        )
                    }
            }

            is NetworkResult.Error   -> {
                if (messages.isEmpty()) {
                    error = result.message
                }
            }

            is NetworkResult.Loading -> Unit
        }
    }

    /*
     * Initial loading of the room and messages
     */
    LaunchedEffect(roomId) {
        loading = true

        loadRoom()
        loadMessages()

        loading = false
    }

    /*
     * Polling messages
     */
    LaunchedEffect(roomId) {
        while (isActive) {
            delay(3000.milliseconds)

            when (val result = chatRepo.getMessages(
                roomId = roomId,
                limit = 50
            )) {
                is NetworkResult.Success -> {
                    val merged = (messages + result.data).distinctBy { it.id }
                        .sortedBy { it.id }

                    if (merged != messages) {
                        messages = merged

                        result.data.maxOfOrNull { it.id }
                            ?.let { lastMessageId ->
                                chatRepo.markAsRead(
                                    roomId,
                                    lastMessageId
                                )
                            }
                    }
                }

                is NetworkResult.Error   -> Unit
                is NetworkResult.Loading -> Unit
            }
        }
    }

    /*
     * Header display information
     */
    val roomTitle = room?.title?.takeIf { it.isNotBlank() }
            ?: "گفتگو"

    val roomImage = room?.image?.takeIf { it.isNotBlank() }

    /*
     * For calls, only in private conversations:
     * The counterpart user is the one listed in users
     */
    val otherUser = room?.users?.firstOrNull { it.id != currentUserId }

    /*
     * Subtitle below the name in the top bar
     * (Group: number of members, Private: counterpart user's role).
     */
    val roomSubtitle = if (room?.isGroup == true) {
        "${room?.users?.size ?: 0} عضو"
    } else {
        otherUser?.role?.takeIf { it.isNotBlank() }
                ?: "گفتگوی خصوصی"
    }

    /*
     * Mobile phone number does not exist in the new ChatRoom.
     * Therefore, for now, the call is only enabled if a phone
     * number is added to the room user model later.
     */
    userMobile = null

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = roomTitle,
                onBack = onBack,
                avatarUrl = roomImage,
                subtitle = roomSubtitle,
                actions = {

                    /*
                     * Display the lock state of the conversation (only admin can
                     * change lock/unlock — in the conversation list)
                     */
                    if (room?.isLocked == true) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "قفل شده",
                            tint = Color(0xFFFF8A80),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    /*
                     * The call is currently disabled because the new ChatRoom API
                     * does not return the user's mobile phone number.
                     *
                     * When phone/mobile is added to users,
                     * this part can be enabled.
                     */
                    if (!room?.isGroup.orFalse() && !userMobile.isNullOrBlank()) {
                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(
                                        Intent.ACTION_DIAL
                                    ).apply {
                                        data = Uri.parse(
                                            "tel:$userMobile"
                                        )
                                        addFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK
                                        )
                                    }

                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                }
                            }) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "تماس",
                                tint = GoldPrimary
                            )
                        }
                    }
                })
        }) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
        ) {

            /*
             * Messages
             */
            Box(
                modifier = Modifier.weight(1f)
            ) {

                when {

                    loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = GoldPrimary
                            )
                        }
                    }

                    error != null && messages.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                Text(
                                    text = error
                                            ?: "خطا در دریافت اطلاعات",
                                    color = Color(0xFFFF8A80),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(12.dp)
                                )

                                androidx.compose.material3.TextButton(
                                    onClick = {
                                        scope.launch {
                                            loading = true
                                            loadRoom()
                                            loadMessages()
                                            loading = false
                                        }
                                    }) {
                                    Text(
                                        text = "تلاش مجدد",
                                        color = GoldPrimary
                                    )
                                }
                            }
                        }
                    }

                    messages.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "هنوز پیامی رد و بدل نشده",
                                    color = Color.White.copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "اولین پیام را ارسال کنید",
                                    color = Color.White.copy(alpha = 0.4f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = messages,
                                key = { it.id }) { message ->

                                MessageBubble(
                                    message = message,
                                    isMine = message.senderId == currentUserId,
                                    myAvatarUrl = myAvatarUrl,
                                    myName = myName
                                )
                            }
                        }
                    }
                }
            }

            /*
             * Send bar
             */
            MessageInputBar(
                text = messageText,
                onTextChange = {
                    messageText = it
                },
                sending = sending,
                onSend = {

                    if (messageText.isBlank() || sending || room == null) {
                        return@MessageInputBar
                    }

                    val text = messageText.trim()

                    scope.launch {
                        sending = true

                        try {
                            when (val result = chatRepo.sendMessage(
                                roomId,
                                text
                            )) {
                                is NetworkResult.Success -> {
                                    messages = (messages + result.data).distinctBy { it.id }

                                    messageText = ""
                                }

                                is NetworkResult.Error   -> {
                                    Toast.makeText(
                                        context,
                                        result.message,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }

                                is NetworkResult.Loading -> Unit
                            }
                        } catch (_: Exception) {
                            Toast.makeText(
                                context,
                                "ارسال پیام ناموفق بود",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } finally {
                            sending = false
                        }
                    }
                })
        }
    }

    /*
     * Auto scroll
     */
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            try {
                listState.animateScrollToItem(
                    messages.size - 1
                )
            } catch (_: Exception) {
                Unit
            }
        }
    }
}

private fun Boolean?.orFalse(): Boolean {
    return this
            ?: false
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    isMine: Boolean,
    myAvatarUrl: String? = null,
    myName: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {

        if (!isMine) {/*
             * Sender's avatar (from the message payload).
             * If it doesn't exist, AvatarView displays the first letter of the name.
             */
            AvatarView(
                name = message.senderName
                        ?: "?",
                avatarUrl = message.senderAvatar,
                size = 32.dp,
                accentColor = Color(0xFF4FC3F7)
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )
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
                    brush = bgBrush,
                    shape = shape
                )
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                )
        ) {
            Column {

                Text(
                    text = message.body
                            ?: "",
                    color = if (isMine) {
                        Color(0xFF1A0533)
                    } else {
                        Color.White
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = message.createdAt?.takeLast(8)
                        ?.take(5)
                            ?: "",
                    color = if (isMine) {
                        Color(0xFF1A0533).copy(alpha = 0.7f)
                    } else {
                        Color.White.copy(alpha = 0.5f)
                    },
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        if (isMine) {
            Spacer(
                modifier = Modifier.width(6.dp)
            )

            /* Current user's avatar (first letter of the name as a fallback) */
            AvatarView(
                name = myName
                        ?: "من",
                avatarUrl = myAvatarUrl,
                size = 32.dp,
                accentColor = GoldPrimary
            )
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
            .padding(12.dp),
    ) {
        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier.size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                GoldPrimary.copy(
                                    alpha = if (text.isNotBlank()) {
                                        0.95f
                                    } else {
                                        0.3f
                                    }
                                ),
                                GoldPrimary.copy(
                                    alpha = if (text.isNotBlank()) {
                                        0.5f
                                    } else {
                                        0.15f
                                    }
                                )
                            )
                        )
                    )
                    .then(
                        if (text.isNotBlank() && !sending) {
                        Modifier.clickable {
                            onSend()
                        }
                    } else {
                        Modifier
                    }),
                    contentAlignment = Alignment.Center) {

                    if (sending) {
                        CircularProgressIndicator(
                            color = Color(0xFF1A0533),
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "ارسال",
                            tint = if (text.isNotBlank()) {
                                Color(0xFF1A0533)
                            } else {
                                Color.White.copy(alpha = 0.4f)
                            },
                            modifier = Modifier.size(20.dp)
                                .scale(
                                    scaleX = -1f,
                                    scaleY = 1f
                                )
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Box(
                    modifier = Modifier.weight(1f)
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
                            text = "پیام خود را بنویسید...",
                            color = Color.White.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    BasicTextField(
                        value = text,
                        onValueChange = onTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White
                        ),
                        cursorBrush = SolidColor(GoldPrimary),
                        singleLine = false
                    )
                }

            }
        }
    }
}