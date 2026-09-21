package com.khz.footballschool.ui.news

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.khz.footballschool.core.util.DateUtils
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.domain.model.Media
import com.khz.footballschool.domain.model.News
import com.khz.footballschool.ui.components.AuthenticatedAsyncImage
import com.khz.footballschool.ui.components.ErrorContent
import com.khz.footballschool.ui.components.GlassBackground
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassSectionTitle
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.LoadingContent
import com.khz.footballschool.ui.components.rememberAuthToken
import com.khz.footballschool.ui.theme.BlueAccent
import com.khz.footballschool.ui.theme.GoldPrimary
import com.khz.footballschool.ui.theme.PurplePrimary
import com.khz.footballschool.ui.theme.RedError
import kotlinx.coroutines.launch

@Composable
fun NewsDetailScreen(
    newsId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    onDeleted: () -> Unit
) {
    val vm: NewsDetailViewModel = appViewModel()
    val state by vm.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(newsId) { vm.load(newsId) }

    val lifecycleOwner = LocalLifecycleOwner.current
    var isFirstResume by remember { mutableStateOf(true) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (isFirstResume) isFirstResume = false else vm.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val busTrigger by NewsRefreshBus.trigger.collectAsState()
    LaunchedEffect(busTrigger) {
        if (busTrigger != 0L) vm.refresh()
    }

    GlassBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                GlassTopBar(
                    title = "جزئیات خبر",
                    onBack = onBack,
                    actions = {
                        if (state is NewsDetailState.Success) {
                            IconButton(onClick = { onEdit(newsId) }) {
                                Icon(
                                    Icons.Default.Edit,
                                    "ویرایش",
                                    tint = GoldPrimary
                                )
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    Icons.Default.Delete,
                                    "حذف",
                                    tint = RedError
                                )
                            }
                        }
                    })
            }) { padding ->
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                when (val s = state) {
                    is NewsDetailState.Loading -> LoadingContent()
                    is NewsDetailState.Error   -> ErrorContent(s.message) { vm.refresh() }
                    is NewsDetailState.Success -> NewsDetailContentModern(
                        news = s.news,
                        onPublish = { vm.publish { ok, msg -> if (!ok) errorMessage = msg } },
                        onArchive = { vm.archive { ok, msg -> if (!ok) errorMessage = msg } },
                        onEdit = { onEdit(newsId) })
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف خبر") },
            text = { Text("آیا مطمئن هستید؟ این عمل قابل بازگشت نیست و رسانه‌های متصل جدا می‌شوند.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    vm.delete { ok, msg -> if (ok) onDeleted() else errorMessage = msg }
                }) {
                    Text(
                        "حذف",
                        color = RedError
                    )
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("انصراف") } })
    }

    errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("خطا") },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { errorMessage = null }) { Text("باشه") } })
    }
}

@Composable
private fun NewsDetailContentModern(
    news: News,
    onPublish: () -> Unit,
    onArchive: () -> Unit,
    onEdit: () -> Unit
) {
    val token = rememberAuthToken()
    var selectedMedia by remember { mutableStateOf<Media?>(null) }

    val hasMedia = news.media.isNotEmpty()
    val coverMedia = news.media.firstOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // HERO
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (hasMedia) 300.dp else 180.dp)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                PurplePrimary.copy(alpha = 0.9f),
                                BlueAccent.copy(alpha = 0.8f),
                                GoldPrimary.copy(alpha = 0.6f)
                            )
                        )
                    )
            ) {
                if (hasMedia && coverMedia != null) {
                    val heroUrl = coverMedia.streamUrl
                            ?: coverMedia.url
                            ?: coverMedia.thumbnailUrl
                    AuthenticatedAsyncImage(
                        url = heroUrl,
                        token = token,
                        contentDescription = news.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.2f),
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    if (coverMedia.isVideo) {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.18f))
                                    .border(
                                        1.5.dp,
                                        Color.White.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                                    .clickable { selectedMedia = coverMedia },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Image,
                                null,
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "بدون تصویر",
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusChipModern(status = news.status)
                        if (news.media.isNotEmpty()) {
                            GlassInfoChip(
                                icon = Icons.Default.Image,
                                text = "${news.media.size} رسانه"
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = news.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        maxLines = 3
                    )
                }

                if (coverMedia?.isVideo == true) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Row(
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Videocam,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "ویدیو",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                            coverMedia.humanDuration?.let {
                                Text(
                                    "· $it",
                                    color = Color.White.copy(0.8f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // INFO CHIPS
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val dateJalali = news.publishAt?.let { DateUtils.gregorianToJalali(it.take(10)) }
                        ?: news.createdAt?.let { DateUtils.gregorianToJalali(it.take(10)) }
                        ?: "-"
                InfoChip(
                    icon = Icons.Default.CalendarMonth,
                    text = DateUtils.toPersianDigits(dateJalali),
                    modifier = Modifier.weight(1f)
                )
                news.createdByName?.let {
                    InfoChip(
                        icon = Icons.Default.Person,
                        text = it,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(
                    icon = Icons.Default.Groups,
                    text = "مخاطب: ${news.audienceSummary ?: "همه"}",
                    modifier = Modifier.weight(1f)
                )
                if (news.images.isNotEmpty() || news.videos.isNotEmpty()) {
                    InfoChip(
                        icon = if (news.videos.isNotEmpty()) Icons.Default.Videocam else Icons.Default.Image,
                        text = listOfNotNull(
                            if (news.images.isNotEmpty()) "${news.images.size} عکس" else null,
                            if (news.videos.isNotEmpty()) "${news.videos.size} فیلم" else null
                        ).joinToString(" · "),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // AUDIENCE DETAILS
        if (news.audiences.isNotEmpty() && news.audiences.none { it.audienceType == "global" }) {
            item {
                GlassCard3D(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BlueAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Groups,
                                    null,
                                    tint = BlueAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                "مخاطبان انتخاب‌شده",
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        val roles = news.audiences.filter { it.audienceType == "role" }
                        val ageGroups = news.audiences.filter { it.audienceType == "age_group" }
                        val classes = news.audiences.filter { it.audienceType == "class" }
                        val players = news.audiences.filter { it.audienceType == "player" }

                        if (roles.isNotEmpty()) {
                            AudienceGroupRow(
                                title = "نقش‌ها",
                                items = roles.map { it.displayTitle })
                        }
                        if (ageGroups.isNotEmpty()) {
                            AudienceGroupRow(
                                title = "گروه‌های سنی",
                                items = ageGroups.map { it.displayTitle })
                        }
                        if (classes.isNotEmpty()) {
                            AudienceGroupRow(
                                title = "کلاس‌ها",
                                items = classes.map { it.displayTitle })
                        }
                        if (players.isNotEmpty()) {
                            AudienceGroupRow(
                                title = "بازیکنان خاص",
                                items = players.map { it.displayTitle })
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        // BODY
        item {
            GlassSectionTitle(
                "متن خبر",
            )
            Spacer(Modifier.height(8.dp))
            GlassCard3D(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = news.body,
                    color = Color.White.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4f),
                    modifier = Modifier.padding(4.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
        }

        // GALLERY - SLIDER
        if (hasMedia) {
            item {
                GlassSectionTitle(
                    "گالری رسانه‌ها (${news.media.size})",
                )
                Spacer(Modifier.height(12.dp))
                MediaSlider(
                    mediaList = news.media,
                    token = token,
                    onMediaClick = { selectedMedia = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(20.dp))
            }
        }

        // ACTIONS
        item {
            GlassCard3D(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "عملیات",
                        color = Color.White.copy(0.8f),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (news.isDraft) {
                            GlassButton(
                                text = "انتشار",
                                onClick = onPublish,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (news.isPublished) {
                            GlassButton(
                                text = "بایگانی",
                                onClick = onArchive,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        GlassButton(
                            text = "ویرایش",
                            onClick = onEdit,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        "نکته: پس از انتشار، خبر برای مخاطبان انتخاب‌شده در اپ بازیکن نمایش داده می‌شود.",
                        color = Color.White.copy(alpha = 0.45f),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    // FULLSCREEN PREVIEW
    selectedMedia?.let { media ->
        Dialog(
            onDismissRequest = { selectedMedia = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1A0A2E).copy(alpha = 0.95f),
                                Color.Black.copy(alpha = 0.98f)
                            )
                        )
                    )
                    .clickable { selectedMedia = null },
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { selectedMedia = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        Icons.Default.Close,
                        "بستن",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.12f),
                                RoundedCornerShape(20.dp)
                            )
                    ) {
                        val url = media.streamUrl
                                ?: media.url
                        AuthenticatedAsyncImage(
                            url = url,
                            token = token,
                            contentDescription = media.displayName,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                        if (media.isVideo) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .border(
                                            2.dp,
                                            Color.White.copy(alpha = 0.3f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                media.displayName,
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 2
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (media.isVideo) BlueAccent.copy(alpha = 0.18f) else GoldPrimary.copy(alpha = 0.18f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        if (media.isVideo) "ویدیو" else "تصویر",
                                        color = if (media.isVideo) BlueAccent else GoldPrimary,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 5.dp
                                        )
                                    )
                                }
                                Text(
                                    media.humanSize,
                                    color = Color.White.copy(0.6f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                                media.humanDuration?.let {
                                    Text(
                                        "· $it",
                                        color = Color.White.copy(0.6f),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaSlider(
    mediaList: List<Media>,
    token: String?,
    onMediaClick: (Media) -> Unit,
    modifier: Modifier = Modifier
) {
    if (mediaList.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { mediaList.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Pager اصلی
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.06f))
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.1f),
                    RoundedCornerShape(20.dp)
                )
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val media = mediaList[page]
                Box(
                    Modifier
                        .fillMaxSize()
                        .clickable { onMediaClick(media) }) {
                    val url = media.thumbnailUrl
                            ?: media.streamUrl
                            ?: media.url
                    if (!url.isNullOrBlank()) {
                        AuthenticatedAsyncImage(
                            url = url,
                            token = token,
                            contentDescription = media.displayName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // گرادیان پایین
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )
                    // Play برای ویدیو
                    if (media.isVideo) {
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.55f))
                                    .border(
                                        1.5.dp,
                                        Color.White.copy(alpha = 0.25f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        media.humanDuration?.let { dur ->
                            Surface(
                                color = Color.Black.copy(alpha = 0.65f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    dur,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 4.dp
                                    )
                                )
                            }
                        }
                    }
                    // نام فایل پایین
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                media.displayName,
                                color = Color.White.copy(0.9f),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${page + 1}/${mediaList.size}",
                                color = Color.White.copy(0.6f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            // دکمه‌های قبلی/بعدی اگر بیش از 1 باشد
            if (mediaList.size > 1) {
                // Indicator dots
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 44.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(mediaList.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(
                                    if (isSelected) 20.dp else 8.dp,
                                    8.dp
                                )
                                .clip(CircleShape)
                                .background(if (isSelected) GoldPrimary else Color.White.copy(alpha = 0.4f))
                        )
                    }
                }
            }
        }

        // اطلاعات رسانه فعلی
        val currentMedia = mediaList.getOrNull(pagerState.currentPage)
        currentMedia?.let { media ->
            GlassCard3D(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (media.isVideo) BlueAccent.copy(0.15f) else GoldPrimary.copy(0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (media.isVideo) Icons.Default.Videocam else Icons.Default.Image,
                                null,
                                tint = if (media.isVideo) BlueAccent else GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                media.displayName,
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                            Text("${if (media.isVideo) "ویدیو" else "عکس"} · ${media.humanSize}${media.humanDuration?.let { " · $it" } ?: ""}",
                                color = Color.White.copy(0.5f),
                                style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Surface(
                        color = if (media.isVideo) BlueAccent.copy(0.15f) else GoldPrimary.copy(0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            if (media.isVideo) "ویدیو" else "عکس",
                            color = if (media.isVideo) BlueAccent else GoldPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            )
                        )
                    }
                }
            }
        }

        // Thumbnails strip
        if (mediaList.size > 1) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                itemsIndexed(mediaList) { index, media ->
                    val isSelected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .border(
                                width = if (isSelected) 2.dp else 0.5.dp,
                                color = if (isSelected) GoldPrimary else Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            }) {
                        val url = media.thumbnailUrl
                                ?: media.streamUrl
                                ?: media.url
                        if (!url.isNullOrBlank()) {
                            AuthenticatedAsyncImage(
                                url = url,
                                token = token,
                                contentDescription = media.displayName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        if (media.isVideo) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChipModern(status: String) {
    val (label, color, icon) = when (status) {
        NewsFormViewModel.STATUS_PUBLISHED -> Triple(
            "منتشر شده",
            Color(0xFF4CAF50),
            Icons.Default.Publish
        )

        NewsFormViewModel.STATUS_DRAFT     -> Triple(
            "پیش‌نویس",
            Color(0xFFFFA726),
            Icons.Default.Edit
        )

        NewsFormViewModel.STATUS_ARCHIVED  -> Triple(
            "بایگانی",
            Color(0xFF90A4AE),
            Icons.Default.Archive
        )

        else                               -> Triple(
            status,
            Color.White.copy(0.6f),
            Icons.Default.Image
        )
    }
    Surface(
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.border(
            0.5.dp,
            color.copy(alpha = 0.3f),
            RoundedCornerShape(10.dp)
        )
    ) {
        Row(
            Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                icon,
                null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                label,
                color = color,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun AudienceGroupRow(
    title: String,
    items: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            title,
            color = GoldPrimary.copy(alpha = 0.9f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.chunked(3)
                    .forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            rowItems.forEach { item ->
                                Surface(
                                    color = Color.White.copy(alpha = 0.06f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.border(
                                        0.5.dp,
                                        Color.White.copy(alpha = 0.08f),
                                        RoundedCornerShape(8.dp)
                                    )
                                ) {
                                    Text(
                                        item,
                                        color = Color.White.copy(0.8f),
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 5.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.07f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(
            0.5.dp,
            Color.White.copy(alpha = 0.1f),
            RoundedCornerShape(12.dp)
        )
    ) {
        Row(
            Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                null,
                tint = GoldPrimary.copy(alpha = 0.9f),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun GlassInfoChip(
    icon: ImageVector,
    text: String
) {
    Surface(
        color = Color.Black.copy(alpha = 0.45f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                null,
                tint = Color.White.copy(0.9f),
                modifier = Modifier.size(12.dp)
            )
            Text(
                text,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
