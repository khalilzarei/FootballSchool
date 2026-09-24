package com.khz.malekadmin.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.ui.theme.GoldPrimary

/**
 * چیپ شیشه‌ای سه‌بعدی برای فیلترها
 *
 * @param label متن نمایشی
 * @param selected آیا انتخاب شده است
 * @param onClick عملکرد کلیک
 * @param accentColor رنگ اصلی در حالت انتخاب‌شده
 * @param showCheckmark نمایش آیکون چک در حالت انتخاب‌شده
 */
@Composable
fun GlassChip3D(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = GoldPrimary,
    showCheckmark: Boolean = true
) {
    var pressed by remember { androidx.compose.runtime.mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        label = "chipScale"
    )

    val shape = RoundedCornerShape(16.dp)

    // ─── گرادیان پس‌زمینه بر اساس وضعیت انتخاب ───
    val backgroundBrush = if (selected) {
        Brush.verticalGradient(
            listOf(
                accentColor.copy(alpha = 0.35f),
                accentColor.copy(alpha = 0.18f),
                accentColor.copy(alpha = 0.10f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.10f),
                Color.White.copy(alpha = 0.05f),
                Color.White.copy(alpha = 0.02f)
            )
        )
    }

    // ─── گرادیان لبه بر اساس وضعیت انتخاب ───
    val borderBrush = if (selected) {
        Brush.verticalGradient(
            listOf(
                accentColor.copy(alpha = 0.90f),
                Color.White.copy(alpha = 0.40f),
                accentColor.copy(alpha = 0.50f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.30f),
                Color.White.copy(alpha = 0.15f),
                Color.White.copy(alpha = 0.08f)
            )
        )
    }

    // ─── رنگ متن ───
    val textColor = if (selected) accentColor else Color.White.copy(alpha = 0.75f)
    val labelWeight = if (selected) FontWeight.Bold else FontWeight.Medium

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    pressed = true
                    onClick()
                }
            )
            .background(backgroundBrush, shape)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // ─── آیکون چک در حالت انتخاب‌شده ───
            if (selected && showCheckmark) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = labelWeight,
                color = textColor
            )
        }
    }
}