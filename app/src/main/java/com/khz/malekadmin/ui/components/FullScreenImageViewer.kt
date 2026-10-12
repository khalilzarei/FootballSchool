package com.khz.malekadmin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 6f

/**
 * نمایش تمام‌صفحه‌ی یک تصویر (مثل عکس پروفایل) با امکان زوم و جابه‌جایی.
 *
 * رفتار:
 *  - لمس روی تصویر یا دکمه‌ی بستن → بسته شدن
 *  - دابل‌تپ → ریست زوم به حالت اولیه
 *  - pinch (دو انگشت) → زوم بین 1x و 6x
 *  - کشیدن با یک انگشت وقتی زوم شده → جابه‌جایی تصویر
 *  - دکمه‌ی «بازنشانی زوم» فقط زمانی ظاهر می‌شود که تصویر زوم شده باشد
 *
 * @param model هر مدلی که Coil بپذیرد: URL رشته‌ای یا Uri محلی
 * @param title عنوان نمایش‌داده‌شده در بالای صفحه (مثل نام بازیکن)
 * @param onDismiss فراخوانی هنگام بسته شدن
 */
@Composable
fun FullScreenImageViewer(
    model: Any?,
    title: String? = null,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(MIN_SCALE) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.94f))
                // لمسِ ساده روی هر نقطه‌ی صفحه → بستن
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onDismiss() },
                        onDoubleTap = {
                            scale = MIN_SCALE
                            offset = Offset.Zero
                        })
                },
            contentAlignment = Alignment.Center
        ) {
            // لایه‌ی زوم/جابه‌جایی
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(
                                MIN_SCALE,
                                MAX_SCALE
                            )
                            scale = newScale
                            offset = if (newScale > MIN_SCALE) {
                                offset + pan
                            } else {
                                Offset.Zero
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(model)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                )
            }

            // نوار بالا: عنوان + دکمه‌ها
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن",
                        tint = Color.White,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                    )
                }

                Text(
                    text = title.orEmpty(),
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                if (scale > MIN_SCALE + 0.01f) {
                    IconButton(onClick = {
                        scale = MIN_SCALE
                        offset = Offset.Zero
                    }) {
                        Icon(
                            imageVector = Icons.Default.ZoomOutMap,
                            contentDescription = "بازنشانی زوم",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                } else {
                    Box(Modifier.size(48.dp))
                }
            }
        }
    }
}
