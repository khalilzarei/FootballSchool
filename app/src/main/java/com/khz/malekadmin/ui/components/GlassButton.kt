package com.khz.malekadmin.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.malekadmin.ui.theme.GoldGlow
import com.khz.malekadmin.ui.theme.GoldPrimary
import com.khz.malekadmin.ui.theme.PurpleDark
import com.khz.malekadmin.ui.theme.PurpleGlow

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    primary: Boolean = true
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "scale")
    val rotationX by animateFloatAsState(if (pressed) 6f else 0f, label = "rotX")
    val elevation by animateDpAsState(if (pressed) 4.dp else 14.dp, label = "elev")

    val shape = RoundedCornerShape(18.dp)
    val bgBrush = if (primary) {
        Brush.linearGradient(listOf(GoldPrimary, Color(0xFFFFB300), GoldPrimary.copy(alpha = 0.85f)))
    } else {
        Brush.linearGradient(listOf(Color.White.copy(0.22f), Color.White.copy(0.06f)))
    }
    val contentColor = if (primary) PurpleDark else Color.White
    val glow = if (primary) GoldGlow else PurpleGlow

    Box(
        modifier = modifier
            .graphicsLayer3D(scale, rotationX)
            .shadow(elevation, shape, spotColor = glow, ambientColor = Color.Black.copy(0.5f))
            .background(bgBrush, shape)
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(Color.White.copy(0.75f), Color.White.copy(0.15f), Color.White.copy(0.45f))
                ),
                shape
            )
            .clip(shape)
            .then(if (enabled) Modifier else Modifier.alpha(0.45f))
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = { if (!loading) onClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    Modifier.size(20.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text,
                    color = contentColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/** افکت سه‌بعدی: مقیاس + چرخش حول محور X */
private fun Modifier.graphicsLayer3D(scale: Float, rotationX: Float): Modifier =
    this.then(
        Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.rotationX = rotationX
            cameraDistance = 12f * density
        }
    )