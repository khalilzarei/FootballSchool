package com.khz.footballschool.ui.matches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.domain.model.Match
import com.khz.footballschool.domain.model.MatchPlayer
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.ErrorGlow
import com.khz.footballschool.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchPlayersScreen(
    matchId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.matchRepository

    var match by remember { mutableStateOf<Match?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(matchId) {
        when (val r = repo.getMatches(perPage = 100)) {
            is NetworkResult.Success -> match = r.data.find { it.id == matchId }
            is NetworkResult.Error   -> error = r.message
            else                     -> {}
        }
        loading = false
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "بازیکنان مسابقه",
                onBack = onBack
            )
        }) { padding ->
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
                val players = match?.players
                        ?: emptyList()
                if (players.isEmpty()) {
                    GlassCard3D {
                        Text(
                            "بازیکنی اضافه نشده است",
                            color = Color.White.copy(0.6f)
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(players) { mp ->
                            MatchPlayerCard(mp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchPlayerCard(mp: MatchPlayer) {
    GlassCard3D(modifier = Modifier.fillMaxWidth(),) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        mp.player?.fullName
                                ?: "-",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        "شماره: ${mp.jerseyNumber ?: "-"} | ${mp.position ?: "-"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(0.6f)
                    )
                }
                Text(
                    "${mp.invitationStatus ?: "-"}",
                    color = GoldPrimary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                StatBadge(
                    "گل",
                    mp.goals
                )
                StatBadge(
                    "پاس",
                    mp.assists
                )
                StatBadge(
                    "زرد",
                    mp.yellowCards
                )
                StatBadge(
                    "قرمز",
                    mp.redCards
                )
            }
        }
    }
}

@Composable
private fun StatBadge(
    label: String,
    value: Int
) {
    GlassCard3D {
        Text(
            "$label: $value",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(0.8f)
        )
    }
}