package com.khz.footballschool.ui.guardians

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.domain.model.Guardian
import com.khz.footballschool.domain.model.GuardianPlayer
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassSectionTitle
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.InfoCard
import com.khz.footballschool.ui.theme.ErrorGlow
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardianDetailScreen(
    guardianId: Int,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.guardianRepository
    val scope = rememberCoroutineScope()

    var guardian by remember { mutableStateOf<Guardian?>(null) }
    var players by remember { mutableStateOf<List<GuardianPlayer>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var detachTarget by remember { mutableStateOf<GuardianPlayer?>(null) }

    fun reload() {
        scope.launch {
            loading = true
            (repo.getGuardian(guardianId) as? NetworkResult.Success)?.let { guardian = it.data }
            (repo.getGuardianPlayers(guardianId) as? NetworkResult.Success)?.let { players = it.data }
            loading = false
        }
    }

    LaunchedEffect(guardianId) { reload() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = guardian?.displayName
                        ?: "جزئیات سرپرست",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            "ویرایش",
                            tint = GoldPrimary
                        )
                    }
                })
        }
    ) { padding ->
        if (loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else {
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                error?.let {
                    GlassCard3D{
                        Text(
                            it,
                            color = Color(0xFFFF8A80)
                        )
                    }
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    guardian?.let { g ->
                        item {
                            InfoCard(
                                title = g.displayName,
                                subtitle = "موبایل: ${g.displayMobile} | آدرس: ${g.address ?: "-"}",
                                trailing = g.displayRole
                            )
                        }
                    }

                    item {
                        GlassSectionTitle("بازیکنان تحت سرپرستی (${players.size})")
                    }

                    if (players.isEmpty()) {
                        item {
                            GlassCard3D {
                                Text(
                                    "هنوز بازیکنی به این سرپرست متصل نشده است",
                                    color = Color.White.copy(0.6f)
                                )
                            }
                        }
                    } else {
                        items(players) { gp ->
                            GuardianPlayerCard(
                                guardianPlayer = gp,
                                onDetach = { detachTarget = gp })
                        }
                    }
                }
            }
        }
    }

    detachTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { detachTarget = null },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        when (val r = repo.detachPlayer(guardianId, target.playerId)) {
                            is NetworkResult.Success -> {
                                detachTarget = null
                                reload()
                            }
                            is NetworkResult.Error -> {
                                detachTarget = null
                                error = r.message
                            }
                            else -> {}
                        }
                    }
                }) {
                    Text(
                        "بله، حذف شود",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { detachTarget = null }) { Text("انصراف") }
            },
            title = { Text("قطع رابطه") },
            text = { Text("آیا از حذف ارتباط با بازیکن ${target.playerName} اطمینان دارید؟") })
    }
}

@Composable
private fun GuardianPlayerCard(
    guardianPlayer: GuardianPlayer,
    onDetach: () -> Unit
) {
    GlassCard3D(modifier = Modifier.fillMaxWidth(),) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    guardianPlayer.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    "نسبت: ${guardianPlayer.relationLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (guardianPlayer.isPrimary) PermissionBadge("اصلی")
                    if (guardianPlayer.canViewReports) PermissionBadge("گزارش")
                    if (guardianPlayer.canPay) PermissionBadge("پرداخت")
                }
            }
            IconButton(onClick = onDetach) {
                Icon(
                    Icons.Default.Delete,
                    "حذف",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun PermissionBadge(label: String) {
    GlassCard3D {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = GoldPrimary
        )
    }
}