package com.khz.footballschool.ui.discounts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GenericListScreen
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.InfoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountListScreen(onBack: () -> Unit) {
    val vm: DiscountListViewModel = appViewModel()
    val state by vm.state.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { GlassTopBar(title = "تخفیف‌ها", onBack = onBack) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            GenericListScreen(
                title = "تخفیف‌ها",
                state = state,
                onRefresh = vm::refresh
            ) { d ->
                val valueText = if (d.discountType == "percent") "${d.value.toInt()}٪" else "${d.value.toInt()} تومان"
                InfoCard(
                    title = d.title,
                    subtitle = "نوع: $valueText",
                    trailing = if (d.isActive) "فعال" else "غیرفعال"
                )
            }
        }
    }
}