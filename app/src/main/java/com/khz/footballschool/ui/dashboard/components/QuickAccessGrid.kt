package com.khz.footballschool.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.StatCard3D

@Composable
fun QuickAccessGrid(items: List<DashboardMenuItem>) {
    GlassCard3D {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.chunked(2)
                .forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { item ->
                            StatCard3D(
                                title = item.title,
                                value = item.value
                                        ?: "",
                                icon = item.icon,
                                accent = item.accent,
                                onClick = item.onClick,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(2 - rowItems.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
        }
    }
}