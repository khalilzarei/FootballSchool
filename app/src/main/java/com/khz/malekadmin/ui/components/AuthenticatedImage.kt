package com.khz.malekadmin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.khz.malekadmin.FootballSchoolApp
import com.khz.malekadmin.domain.model.Media
import com.khz.malekadmin.ui.theme.BlueAccent
import com.khz.malekadmin.ui.theme.GoldPrimary
import com.khz.malekadmin.ui.theme.PurplePrimary

@Composable
fun rememberAuthToken(): String? {
    val context = LocalContext.current
    val container = (context.applicationContext as FootballSchoolApp).container
    val tokenState = produceState<String?>(initialValue = null) {
        value = container.sessionManager.getTokenSync()
    }
    return tokenState.value
}

@Composable
fun AuthenticatedAsyncImage(
    url: String?,
    token: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    if (url.isNullOrBlank()) {
        Box(
            modifier = modifier.background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.BrokenImage,
                null,
                tint = Color.White.copy(0.3f)
            )
        }
        return
    }

    val request = remember(
        url,
        token
    ) {
        ImageRequest.Builder(context)
            .data(url)
            .apply {
                if (!token.isNullOrBlank()) {
                    addHeader(
                        "Authorization",
                        "Bearer $token"
                    )
                }
            }
            .crossfade(true)
            .build()
    }

    SubcomposeAsyncImage(
        model = request,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = GoldPrimary,
                    strokeWidth = 2.dp
                )
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.BrokenImage,
                    null,
                    tint = Color.White.copy(0.3f)
                )
            }
        })
}

@Composable
fun MediaCover(
    media: Media,
    token: String?,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp
) {
    Box(
        modifier = modifier.clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            )
    ) {
        when {
            media.isImage -> {
                val url = media.streamUrl
                        ?: media.url
                        ?: media.thumbnailUrl
                AuthenticatedAsyncImage(
                    url = url,
                    token = token,
                    contentDescription = media.displayName,
                    modifier = Modifier.fillMaxSize()
                )
            }

            media.isVideo -> {
                val thumb = media.thumbnailUrl
                        ?: media.streamUrl
                        ?: media.url
                if (!thumb.isNullOrBlank()) {
                    AuthenticatedAsyncImage(
                        url = thumb,
                        token = token,
                        contentDescription = media.displayName,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        PurplePrimary,
                                        BlueAccent
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = media.displayName.take(30),
                            color = Color.White.copy(0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                // Play overlay
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "پخش",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                media.humanDuration?.let { dur ->
                    Text(
                        text = dur,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(
                                horizontal = 6.dp,
                                vertical = 2.dp
                            )
                    )
                }
            }

            else          -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.06f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        media.displayName,
                        color = Color.White.copy(0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun MediaThumbnailRow(
    mediaList: List<Media>,
    token: String?,
    modifier: Modifier = Modifier
) {
    if (mediaList.isEmpty()) return
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        items(mediaList.size) { idx ->
            val m = mediaList[idx]
            Box(
                modifier = Modifier
                    .size(
                        width = 80.dp,
                        height = 80.dp
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.06f))
            ) {
                val url = when {
                    m.isImage -> m.thumbnailUrl
                            ?: m.streamUrl
                            ?: m.url

                    else      -> m.thumbnailUrl
                            ?: m.streamUrl
                            ?: m.url
                }
                if (m.isImage && !url.isNullOrBlank()) {
                    AuthenticatedAsyncImage(
                        url = url,
                        token = token,
                        contentDescription = m.displayName,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (m.isVideo) {
                    if (!url.isNullOrBlank()) {
                        AuthenticatedAsyncImage(
                            url = url,
                            token = token,
                            contentDescription = m.displayName,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            PurplePrimary,
                                            BlueAccent
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.BrokenImage,
                            null,
                            tint = Color.White.copy(0.3f)
                        )
                    }
                }
            }
        }
    }
}
