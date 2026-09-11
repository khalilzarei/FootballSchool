package com.khz.footballschool.ui.classes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.domain.model.Enrollment
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentManagerScreen(classId: Int, onBack: () -> Unit, onEnrollPlayer: () -> Unit) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.classRepository
    val scope = rememberCoroutineScope()

    var enrollments by remember { mutableStateOf<List<Enrollment>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    fun reload() {
        scope.launch {
            loading = true
//            when (val r = repo.getClassPlayers(classId, perPage = 100)) {
//                is NetworkResult.Success -> enrollments = r.data.items
//                else -> {}
//            }
            loading = false
        }
    }

    LaunchedEffect(classId) { reload() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "بازیکنان ثبت‌نام‌شده",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onEnrollPlayer) {
                        Icon(Icons.Default.PersonAdd, "ثبت‌نام بازیکن", tint = GoldPrimary)
                    }
                }
            )
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(enrollments) { e ->
                    GlassCard3D {
                        androidx.compose.foundation.layout.Row(
                            Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.foundation.layout.Column {
                                Text(
                                    e.player.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    "تاریخ ثبت‌نام: ${e.enrolledAt ?: "-"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(0.6f)
                                )
                            }
                            Text(
                                if (e.isActive) "فعال" else "غیرفعال",
                                color = if (e.isActive) Color(0xFF81C784) else Color(0xFFFF8A80),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}