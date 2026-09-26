package com.khz.malekadmin.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.khz.malekadmin.domain.model.ChatContact
import com.khz.malekadmin.ui.components.AvatarView
import com.khz.malekadmin.ui.components.GlassBackground
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

/**
 * انتخاب مخاطب برای شروع گفتگوی جدید (اپ ادمین).
 *
 * منبع: GET /chat/contacts — بازیکنان + مربیان فعال.
 */
@Composable
fun ChatContactsScreen(
    onBack: () -> Unit,
    onPick: (userId: Int) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val container = (context.applicationContext as MalekAdminApp).container

    val chatRepo = container.chatRepository
    val scope = rememberCoroutineScope()

    var contacts by remember {
        mutableStateOf<List<ChatContact>?>(null)
    }

    var error by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }

    fun load() {
        scope.launch {
            error = null
            when (val result = chatRepo.getContacts()) {
                is NetworkResult.Success -> contacts = result.data
                is NetworkResult.Error   -> error = result.message
                is NetworkResult.Loading -> Unit
            }
        }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        load()
    }

    val filtered = contacts?.filter {
        query.isBlank() || (it.fullName?.contains(
            query,
            ignoreCase = true
        )
                ?: false)
    }
            ?: emptyList()

    GlassBackground {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            GlassTopBar(
                title = "شروع گفتگوی جدید",
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp)
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text("جست‌وجوی بازیکن یا مربی...")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                when {
                    contacts == null && error == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = GoldPrimary)
                        }
                    }

                    error != null                     -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = error
                                            ?: "خطا در دریافت مخاطبین",
                                    color = Color(0xFFFF8A80),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.height(12.dp))
                                TextButton(onClick = { load() }) {
                                    Text(
                                        "تلاش مجدد",
                                        color = GoldPrimary
                                    )
                                }
                            }
                        }
                    }

                    filtered.isEmpty()                -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "مخاطبی پیدا نشد",
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    else                              -> {
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 4.dp),
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = filtered,
                                key = { it.userId }) { contact ->

                                ContactRow(
                                    contact = contact,
                                    onClick = { onPick(contact.userId) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactRow(
    contact: ChatContact,
    onClick: () -> Unit
) {
    val roleLabel = when (contact.role?.trim()
        ?.lowercase()) {
        "player", "athlete" -> "بازیکن"
        "admin", "administrator", "manager", "owner" -> "مدیر"
        "coach", "trainer" -> "مربی"
        "guardian", "parent" -> "سرپرست"
        "teacher" -> "مربی آموزشی"
        "accountant" -> "حسابدار"
        "staff" -> "کادر اجرایی"
        else -> contact.role.orEmpty()
    }

    val subtitle = contact.classTitle?.takeIf { it.isNotBlank() }
        ?.let { "$roleLabel — $it" }
            ?: roleLabel

    GlassCard3D(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarView(
                name = contact.fullName
                        ?: "?",
                avatarUrl = contact.avatarUrl,
                size = 48.dp,
                accentColor = GoldPrimary
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = contact.fullName
                            ?: "",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
