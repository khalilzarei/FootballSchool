package com.khz.malekadmin.ui.guardians

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.ui.components.GenericListScreen
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.InfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardianListScreen(onGuardianClick: (Int) -> Unit) { // ← پارامتر کلیک اضافه شد
    val vm: GuardianListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    GenericListScreen(
        "سرپرست‌ها",
        state,
        vm::refresh
    ) { g ->
        GlassCard3D(
            modifier = Modifier.fillMaxWidth()
                .clickable { onGuardianClick(g.id) },
        ) {
            InfoCard(
                title = g.displayName,
                subtitle = "${g.displayMobile} | ${g.address ?: "-"}",
                trailing = "${g.players.size} بازیکن"
            )
        }
        // ← اتصال کلیک

    }
}