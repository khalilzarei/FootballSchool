package com.khz.footballschool.ui.matches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.khz.footballschool.FootballSchoolApp
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.SetMatchResultRequest
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassSectionTitle
import com.khz.footballschool.ui.components.GlassTextField
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetMatchResultScreen(matchId: Int, onBack: () -> Unit, onSaved: () -> Unit) {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val repo = container.matchRepository
    val scope = rememberCoroutineScope()

    var homeScore by remember { mutableStateOf("") }
    var awayScore by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { GlassTopBar(title = "ثبت نتیجه مسابقه", onBack = onBack) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.SportsSoccer,
                null,
                tint = GoldPrimary,
                modifier = Modifier.padding(top = 20.dp)
            )
            Spacer(Modifier.height(24.dp))

            GlassCard3D(modifier = Modifier.fillMaxWidth(),) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GlassSectionTitle("امتیاز نهایی")
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GlassTextField(
                            value = homeScore,
                            onValueChange = { homeScore = it.filter { c -> c.isDigit() } },
                            label = "گل میزبان",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                        GlassTextField(
                            value = awayScore,
                            onValueChange = { awayScore = it.filter { c -> c.isDigit() } },
                            label = "گل مهمان",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            GlassButton(
                text = "ثبت نتیجه",
                onClick = {
                    val h = homeScore.toIntOrNull()
                    val a = awayScore.toIntOrNull()
                    if (h == null || a == null) {
                        error = "اعداد معتبر وارد کنید"
                        return@GlassButton
                    }
                    loading = true
                    error = null
                    scope.launch {
                        when (val r = repo.setResult(matchId, SetMatchResultRequest(h, a))) {
                            is NetworkResult.Success -> onSaved()
                            is NetworkResult.Error -> { error = r.message; loading = false }
                            else -> { loading = false }
                        }
                    }
                },
                loading = loading,
                enabled = !loading,
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