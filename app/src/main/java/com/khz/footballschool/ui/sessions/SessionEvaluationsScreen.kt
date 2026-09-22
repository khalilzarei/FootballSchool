package com.khz.footballschool.ui.sessions

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
import com.khz.footballschool.domain.model.Evaluation
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionEvaluationsScreen(
    sessionId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.evaluationRepository

    var evaluations by remember { mutableStateOf<List<Evaluation>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(sessionId) {
        when (val r = repo.getSessionEvaluations(sessionId)) {
            is NetworkResult.Success -> evaluations = r.data
            is NetworkResult.Error   -> error = r.message
            else                     -> {}
        }
        loading = false
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "ارزیابی‌های جلسه",
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
                    GlassCard3D() {
                        Text(
                            it,
                            color = Color(0xFFFF8A80)
                        )
                    }
                }
                if (evaluations.isEmpty()) {
                    GlassCard3D() {
                        Text(
                            "ارزیابی‌ای ثبت نشده است",
                            color = Color.White.copy(0.6f)
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(evaluations) { e ->
                            EvaluationCard(e)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EvaluationCard(e: Evaluation) {
    GlassCard3D(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    e.player?.fullName
                            ?: "-",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                e.overallScore?.let {
                    Text(
                        "$it/10",
                        color = GoldPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ScoreChip(
                    "فنی",
                    e.technicalScore
                )
                ScoreChip(
                    "انضباط",
                    e.disciplineScore
                )
                ScoreChip(
                    "جسمانی",
                    e.physicalScore
                )
                ScoreChip(
                    "کار تیمی",
                    e.teamworkScore
                )
            }
        }
    }
}

@Composable
private fun ScoreChip(
    label: String,
    score: Int?
) {
    GlassCard3D(modifier = Modifier.padding(end = 0.dp)) {
        Text(
            "$label: ${score ?: "-"}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(0.8f)
        )
    }
}