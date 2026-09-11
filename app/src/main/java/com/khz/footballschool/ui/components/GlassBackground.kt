package com.khz.footballschool.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khz.footballschool.ui.theme.*

@Composable
fun GlassBackground(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(BackgroundTop, BackgroundMid, BackgroundBottom)
                )
            )
    ) {
        val transition = rememberInfiniteTransition(label = "blobs")

        // هاله بنفش بالا-چپ
        val y1 by transition.animateFloat(
            initialValue = 0f,
            targetValue = 70f,
            animationSpec = infiniteRepeatable(
                tween(6000, easing = FastOutSlowInEasing),
                RepeatMode.Reverse
            ),
            label = "blob1y"
        )
        // هاله طلایی پایین-راست
        val x2 by transition.animateFloat(
            initialValue = 0f,
            targetValue = -60f,
            animationSpec = infiniteRepeatable(
                tween(7500, easing = FastOutSlowInEasing),
                RepeatMode.Reverse
            ),
            label = "blob2x"
        )
        // هاله آبی میانی
        val y3 by transition.animateFloat(
            initialValue = 0f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(
                tween(9000, easing = FastOutSlowInEasing),
                RepeatMode.Reverse
            ),
            label = "blob3y"
        )

        Box(
            Modifier
                .offset(y = y1.dp)
                .size(300.dp)
                .blur(70.dp)
                .background(
                    Brush.radialGradient(
                        listOf(PurpleLight.copy(alpha = 0.55f), Color.Transparent)
                    )
                )
        )
        Box(
            Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .offset(x = x2.dp)
                .size(260.dp)
                .blur(80.dp)
                .background(
                    Brush.radialGradient(
                        listOf(GoldPrimary.copy(alpha = 0.30f), Color.Transparent)
                    )
                )
        )
        Box(
            Modifier
                .align(androidx.compose.ui.Alignment.CenterStart)
                .offset(y = y3.dp)
                .size(220.dp)
                .blur(75.dp)
                .background(
                    Brush.radialGradient(
                        listOf(BlueLight.copy(alpha = 0.35f), Color.Transparent)
                    )
                )
        )

        content()
    }
}