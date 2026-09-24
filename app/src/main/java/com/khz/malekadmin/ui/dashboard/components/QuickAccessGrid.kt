package com.khz.malekadmin.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.ui.components.StatCard3D

/**
 * دسترسی سریع — آیتم‌ها در یک ستون عمودی (هر آیتم یک سطر تمام‌عرض)
 */
@Composable
fun QuickAccessGrid(items: List<DashboardMenuItem>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach { item ->
            StatCard3D(
                title = item.title,
                value = item.value
                        ?: "",
                icon = item.icon,
                accent = item.accent,
                onClick = item.onClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}