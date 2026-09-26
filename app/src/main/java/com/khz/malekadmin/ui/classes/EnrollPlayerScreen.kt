package com.khz.malekadmin.ui.classes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.MalekAdminApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.domain.model.Player
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassSearchField
import com.khz.malekadmin.ui.components.GlassSectionTitle
import com.khz.malekadmin.ui.components.GlassTextField
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollPlayerScreen(
    classId: Int,
    onBack: () -> Unit,
    onEnrolled: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as MalekAdminApp).container
    val playerRepo = container.playerRepository
    val classRepo = container.classRepository
    val scope = rememberCoroutineScope()

    var players by remember { mutableStateOf<List<Player>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var query by remember { mutableStateOf("") }
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }
    var enrolledAt by remember { mutableStateOf("") }
    var monthlyFeeOverride by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(query) {
        loading = true
        when (val r = playerRepo.getPlayers(
            query = query.takeIf { it.isNotBlank() },
            perPage = 50
        )) {
            is NetworkResult.Success -> players = r.data.items
            is NetworkResult.Error   -> error = r.message
            else                     -> {}
        }
        loading = false
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "ثبت‌نام بازیکن در کلاس",
                onBack = onBack
            )
        }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            GlassSectionTitle("۱. انتخاب بازیکن")
            Spacer(Modifier.height(8.dp))
            GlassSearchField(
                value = query,
                onValueChange = { query = it },
                label = "جستجوی بازیکن"
            )
            Spacer(Modifier.height(8.dp))

            if (loading) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoldPrimary)
                }
            } else if (players.isEmpty()) {
                GlassCard3D() {
                    Text(
                        "بازیکنی یافت نشد",
                        color = Color.White.copy(0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(players) { p ->
                        PlayerSelectableItem(
                            player = p,
                            isSelected = selectedPlayer?.id == p.id,
                            onClick = { selectedPlayer = p })
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            GlassSectionTitle("۲. جزئیات ثبت‌نام")
            Spacer(Modifier.height(8.dp))

            GlassCard3D() {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    GlassTextField(
                        value = enrolledAt,
                        onValueChange = { enrolledAt = it },
                        label = "تاریخ ثبت‌نام (اختیاری)",
                        supportingText = "فرمت: 2026-09-08"
                    )
                    GlassTextField(
                        value = monthlyFeeOverride,
                        onValueChange = { v -> monthlyFeeOverride = v.filter { it.isDigit() } },
                        label = "شهریه ماهانه اختصاصی (اختیاری، ریال)",
                        keyboardType = KeyboardType.Number
                    )
                    GlassTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = "یادداشت (اختیاری)"
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            GlassButton(
                text = "ثبت‌نام",
                onClick = {
                    val p = selectedPlayer
                    if (p == null) {
                        error = "ابتدا یک بازیکن انتخاب کنید"; return@GlassButton
                    }

                    val date = enrolledAt.trim()
                    if (date.isNotEmpty() && !Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(date)) {
                        error = "تاریخ ثبت‌نام باید با فرمت YYYY-MM-DD باشد (مثلاً 2026-09-12)"
                        return@GlassButton
                    }

                    val fee = monthlyFeeOverride.trim()
                    if (fee.isNotEmpty() && fee.toLongOrNull() == null) {
                        error = "شهریه ماهانه اختصاصی باید عدد باشد"
                        return@GlassButton
                    }

                    saving = true
                    error = null
                    scope.launch {
                        when (val r = classRepo.enrollPlayer(
                            classId = classId,
                            playerId = p.id,
                            enrolledAt = date.takeIf { it.isNotEmpty() },
                            monthlyFeeOverride = fee.takeIf { it.isNotEmpty() }
                                ?.toLong(),
                            sessionFeeOverride = null,
                            registrationFeeOverride = null,
                            notes = notes.takeIf { it.isNotBlank() })) {
                            is NetworkResult.Success -> onEnrolled()
                            is NetworkResult.Error   -> error = r.message
                            else                     -> {}
                        }
                        saving = false
                    }
                },
                loading = saving,
                enabled = !saving && selectedPlayer != null,
                modifier = Modifier.fillMaxWidth()
            )

            error?.let {
                Spacer(Modifier.height(8.dp))
                GlassCard3D() {
                    Text(
                        it,
                        color = Color(0xFFFF8A80)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerSelectableItem(
    player: Player,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    GlassCard3D(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    player.fullName,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                Text(
                    "سن: ${player.age ?: "-"} | ${player.nationalCode ?: "-"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f)
                )
            }
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = GoldPrimary
                )
            }
        }
    }
}