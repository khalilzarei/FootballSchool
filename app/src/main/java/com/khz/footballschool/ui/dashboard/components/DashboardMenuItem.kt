package com.khz.footballschool.ui.dashboard.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class DashboardMenuItem(
    val title: String,
    val icon: ImageVector,
    val accent: Color,
    val value: String? = null,
    val onClick: () -> Unit
)