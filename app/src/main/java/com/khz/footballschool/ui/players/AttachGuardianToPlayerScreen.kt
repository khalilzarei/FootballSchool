package com.khz.footballschool.ui.players

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.unit.dp
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.AttachGuardianToPlayerRequest
import com.khz.footballschool.domain.model.Guardian
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassDropdown
import com.khz.footballschool.ui.components.GlassSectionTitle
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.ErrorGlow
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

private val RELATIONS = listOf(
    "father" to "پدر",
    "mother" to "مادر",
    "grandfather" to "پدربزرگ",
    "grandmother" to "مادربزرگ",
    "uncle" to "عمو/دایی",
    "aunt" to "عمه/خاله",
    "other" to "سایر"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachGuardianToPlayerScreen(
    playerId: Int,
    onBack: () -> Unit,
    onAttached: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val guardianRepo = container.guardianRepository
    val playerRepo = container.playerRepository
    val scope = rememberCoroutineScope()

    var guardians by remember { mutableStateOf<List<Guardian>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var selectedGuardian by remember { mutableStateOf<Guardian?>(null) }
    var selectedRelation by remember { mutableStateOf("father") }
    var isPrimary by remember { mutableStateOf(false) }
    var canViewReports by remember { mutableStateOf(true) }
    var canPay by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        when (val r = guardianRepo.getGuardians(perPage = 100)) {
            is NetworkResult.Success -> guardians = r.data
            is NetworkResult.Error   -> error = r.message
            else                     -> {}
        }
        loading = false
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            GlassTopBar(
                title = "افزودن سرپرست به بازیکن",
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
                GlassSectionTitle("۱. انتخاب سرپرست")
                Spacer(Modifier.height(8.dp))

                if (guardians.isEmpty()) {
                    GlassCard3D {
                        Text(
                            "سرپرستی ثبت نشده است",
                            color = Color.White.copy(0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 220.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(guardians) { g ->
                            GuardianSelectableItem(
                                guardian = g,
                                isSelected = selectedGuardian?.id == g.id,
                                onClick = { selectedGuardian = g })
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                GlassSectionTitle("۲. مشخصات رابطه")
                Spacer(Modifier.height(8.dp))

                GlassCard3D {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        GlassDropdown(
                            label = "نسبت",
                            options = RELATIONS,
                            selectedValue = selectedRelation,
                            onSelect = { selectedRelation = it })
                        CheckboxRow(
                            "سرپرست اصلی",
                            isPrimary
                        ) { isPrimary = it }
                        CheckboxRow(
                            "دسترسی به گزارش‌ها",
                            canViewReports
                        ) { canViewReports = it }
                        CheckboxRow(
                            "اجازه پرداخت",
                            canPay
                        ) { canPay = it }
                    }
                }

                Spacer(Modifier.height(20.dp))

                GlassButton(
                    text = "ثبت رابطه",
                    onClick = {
                        val g = selectedGuardian
                        if (g == null) {
                            error = "ابتدا یک سرپرست انتخاب کنید"; return@GlassButton
                        }
                        saving = true
                        error = null
                        scope.launch {
                            val r = playerRepo.attachGuardian(
                                playerId,
                                AttachGuardianToPlayerRequest(
                                    guardianId = g.id,
                                    relation = selectedRelation,
                                    isPrimary = isPrimary,
                                    canViewReports = canViewReports,
                                    canPay = canPay
                                )
                            )
                            saving = false
                            when (r) {
                                is NetworkResult.Success -> onAttached()
                                is NetworkResult.Error   -> error = r.message
                                else                     -> {}
                            }
                        }
                    },
                    loading = saving,
                    enabled = !saving && selectedGuardian != null,
                    modifier = Modifier.fillMaxWidth()
                )

                error?.let {
                    Spacer(Modifier.height(8.dp))
                    GlassCard3D{
                        Text(
                            it,
                            color = Color(0xFFFF8A80)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuardianSelectableItem(
    guardian: Guardian,
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
                    guardian.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                Text(
                    guardian.displayMobile,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.6f)
                )
            }
            if (isSelected) Icon(
                Icons.Default.CheckCircle,
                null,
                tint = GoldPrimary
            )
        }
    }
}

@Composable
private fun CheckboxRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = GoldPrimary,
                checkmarkColor = Color(0xFF1A0533),
                uncheckedColor = Color.White.copy(0.4f)
            )
        )
        Text(
            label,
            color = Color.White.copy(0.85f)
        )
    }
}