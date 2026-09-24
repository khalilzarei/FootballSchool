package com.khz.malekadmin.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.ui.theme.GoldGlow
import com.khz.malekadmin.ui.theme.GoldPrimary

/**
 * کارت شیشه‌ای سه‌بعدی بدون shadow مستطیلی Compose
 *
 * استراتژی:
 * - shadow نرم با Box پشتی + blur (بدون bounding box مستطیلی)
 * - گرادیان عمودی برای عمق سه‌بعدی
 * - highlight بالا برای حس نور
 * - border گرادیانی برای لبه نورانی
 */

@Composable
fun GlassCard3D(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    glowColor: Color = Color.Transparent,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {

    // ─── گرادیان اصلی سطح ───
    val surfaceBrush = remember {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.18f),   // بالا (روشن‌تر)
                Color.White.copy(alpha = 0.08f),   // میانه
                Color.White.copy(alpha = 0.04f)    // پایین (شفاف‌تر)
            )
        )
    }

    // ─── گرادیان لبه نورانی ───
    val borderBrush = remember {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.45f),   // لبه بالا
                GoldPrimary.copy(alpha = 0.30f),   // میانه طلایی
                Color.White.copy(alpha = 0.10f),   // پایین ملایم
                Color.Transparent
            )
        )
    }

    // ─── گرادیان سایه درونی (پایین) ───
    val shadowBrush = remember {
        val shadowColor = if (glowColor == Color.Transparent) {
            Color.Black
        } else {
            glowColor
        }
        Brush.verticalGradient(
            listOf(
                Color.Transparent,
                Color.Transparent,
                shadowColor.copy(alpha = 0.15f)
            )
        )
    }

    Box(
        modifier = modifier
            .clickable(onClick = { onClick?.invoke() })
            .padding(
                5.dp,
            )
            // لایه ۱: سایه درونی (حس عمق)
            .background(
                shadowBrush,
                shape
            )
            // لایه ۲: گرادیان اصلی سطح
            .background(
                surfaceBrush,
                shape
            )
            // لایه ۳: لبه نورانی
            .border(
                1.dp,
                borderBrush,
                shape
            )
            // لایه ۴: گرد کردن گوشه‌ها
            .clip(shape),

        content = {
            Box(
                modifier = modifier.padding(10.dp),

                ) {
                content()
            }
        })
}

@Preview(showBackground = false)
@Composable
private fun GlassCard3DPreviewDark() {
    val glow = GoldGlow
    val accentColor = Color(0xFFFF5252)

    // افکت فشار برای کلیک
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (pressed) 0.96f else 1f,
        label = "statScale"
    )
    GlassCard3D() {
        Row(
            modifier = Modifier.background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(46.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                accentColor.copy(0.45f),
                                accentColor.copy(0.65f),
                                accentColor.copy(0.9f),
                                accentColor.copy(0.65f),
                                accentColor.copy(0.45f),
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Assessment,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(
                Modifier
                    .weight(1f)
                    .background(Color.Transparent)
            ) {
                Text(
                    "title",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    "value",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = accentColor
                )
            }
        }
    }

}