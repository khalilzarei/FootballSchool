package com.khz.malekadmin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.khz.malekadmin.ui.theme.GoldPrimary

/**
 * کارت اطلاعات شیشه‌ای - استفاده در همه لیست‌ها و صفحه‌های جزئیات
 */
@Composable
fun InfoCard(
    title: String,
    subtitle: String,
    trailing: String = "",
    content: (@Composable () -> Unit)? = null
) {
    GlassCard3D(modifier = Modifier.fillMaxWidth(),) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.60f)
                    )
                }
                if (trailing.isNotBlank()) {
                    Text(
                        trailing,
                        style = MaterialTheme.typography.labelLarge,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            content?.invoke()
        }
    }
}