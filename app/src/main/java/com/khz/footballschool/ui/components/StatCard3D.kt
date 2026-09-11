package com.khz.footballschool.ui.components

import android.R.attr.onClick
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khz.footballschool.ui.theme.GoldGlow
import com.khz.footballschool.ui.theme.GoldPrimary

@Composable
fun StatCard3D(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accent: Color = GoldPrimary,
    isError: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val glow = if (isError) Color(0x66A50044) else GoldGlow
    val accentColor = if (isError) Color(0xFFFF5252) else accent

    // افکت فشار برای کلیک
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (pressed) 0.96f else 1f,
        label = "statScale"
    )

    GlassCard3D(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = {
                            pressed = true
                            onClick()
                        })
                } else Modifier
            ),
    ) {
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
                    icon,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Row(
                Modifier
                    .weight(1f)
                    .background(Color.Transparent),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = accentColor
                )
            }
        }
    }
}

@Preview
@Composable
fun StatCard3DPreview() {
    StatCard3D(
        title = "title",
        value = "500",
        icon = Icons.Default.Assessment,
    )
}