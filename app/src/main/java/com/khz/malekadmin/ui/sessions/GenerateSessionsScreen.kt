package com.khz.malekadmin.ui.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.FootballSchoolApp
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.dto.request.GenerateSessionsRequest
import com.khz.malekadmin.domain.model.FootballClass
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassDropdown
import com.khz.malekadmin.ui.components.GlassSectionTitle
import com.khz.malekadmin.ui.components.GlassTextField
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateSessionsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val classRepo = container.classRepository
    val sessionRepo = container.sessionRepository
    val scope = rememberCoroutineScope()

    var classes by remember { mutableStateOf<List<FootballClass>>(emptyList()) }
    var selectedClassId by remember { mutableStateOf<Int?>(null) }
    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var generating by remember { mutableStateOf(false) }
    var generatedCount by remember { mutableStateOf<Int?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        when (val r = classRepo.getClasses(perPage = 100)) {
            is NetworkResult.Success -> classes = r.data.items
            is NetworkResult.Error -> error = r.message
            else -> {}
        }
        loading = false
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { GlassTopBar(title = "تولید خودکار جلسات", onBack = onBack) }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else {
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                GlassSectionTitle("تنظیمات تولید")
                Spacer(Modifier.height(8.dp))

                GlassCard3D() {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassDropdown(
                            label = "انتخاب کلاس",
                            options = classes.map { it.id to it.title },
                            selectedValue = selectedClassId
                                    ?: 0,
                            onSelect = { selectedClassId = it })
                        GlassTextField(
                            value = fromDate,
                            onValueChange = { fromDate = it },
                            label = "از تاریخ",
                            supportingText = "فرمت: 2026-09-01"
                        )
                        GlassTextField(
                            value = toDate,
                            onValueChange = { toDate = it },
                            label = "تا تاریخ",
                            supportingText = "فرمت: 2026-09-30"
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                GlassButton(
                    text = "تولید جلسات",
                    onClick = {
                        val classId = selectedClassId
                        if (classId == null) { error = "کلاس را انتخاب کنید"; return@GlassButton }
                        if (fromDate.isBlank() || toDate.isBlank()) {
                            error = "بازه زمانی را وارد کنید"; return@GlassButton
                        }
                        generating = true
                        error = null
                        scope.launch {
                            when (val r = sessionRepo.generateSessions(
                                GenerateSessionsRequest(classId, fromDate, toDate)
                            )) {
                                is NetworkResult.Success -> {
                                    generatedCount = r.data
                                    generating = false
                                }
                                is NetworkResult.Error -> {
                                    error = r.message
                                    generating = false
                                }
                                else -> {}
                            }
                        }
                    },
                    loading = generating,
                    enabled = !generating,
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

                generatedCount?.let { count ->
                    Spacer(Modifier.height(20.dp))
                    GlassCard3D() {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                if (count > 0) "$count جلسه با موفقیت تولید شد"
                                else "جلسه جدیدی تولید نشد (همه در بازه موردنظر قبلاً ایجاد شده‌اند)",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (count > 0) Color(0xFF81C784) else GoldPrimary
                            )
                            Text(
                                "از $fromDate تا $toDate — بر اساس برنامه هفتگی فعال کلاس",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}