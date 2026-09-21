package com.khz.footballschool.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.footballschool.ui.theme.GoldPrimary

@Composable
fun GlassSectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        title,
        modifier = modifier.padding(horizontal = 16.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = GoldPrimary
    )
}