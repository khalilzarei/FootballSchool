package com.khz.footballschool.ui.news

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.domain.model.News
import com.khz.footballschool.ui.components.ErrorContent
import com.khz.footballschool.ui.components.GlassBackground
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassSearchField
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.LoadingContent
import com.khz.footballschool.ui.components.MediaCover
import com.khz.footballschool.ui.components.MediaThumbnailRow
import com.khz.footballschool.ui.components.rememberAuthToken
import com.khz.footballschool.ui.theme.GoldPrimary
import com.khz.footballschool.ui.theme.RedError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit = {},
    onEdit: (Int) -> Unit = {},
    onDetail: (Int) -> Unit = {}
) {
    val vm: NewsListViewModel = appViewModel()
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var newsToDelete by remember { mutableStateOf<News?>(null) }
    val token = rememberAuthToken()

    // 1) هر بار که کاربر به لیست برمی‌گردد (ON_RESUME) لیست را رفرش کن
    // برای جلوگیری از رفرش دوبل در اولین ورود، اولین ON_RESUME را نادیده می‌گیریم
    // چون ViewModel خودش در init رفرش می‌کند
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var isFirstResume by remember { mutableStateOf(true) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (isFirstResume) {
                    isFirstResume = false
                } else {
                    vm.refresh()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 2) اگر از صفحه جزئیات یا فرم خبری تغییر کرد، باس خبر می‌دهد و لیست رفرش می‌شود
    // این برای حالتی است که کاربر هنوز در لیست است و از جای دیگر خبر تغییر کرده
    // یا برای اطمینان بیشتر بعد از بازگشت
    val busTrigger by NewsRefreshBus.trigger.collectAsState()
    LaunchedEffect(busTrigger) {
        if (busTrigger != 0L) {
            vm.refresh()
        }
    }

    LaunchedEffect(state.actionMessage) {
        state.actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.consumeActionMessage()
        }
    }

    GlassBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                GlassTopBar(
                    title = "اخبار",
                    onBack = onBack,
                    actions = {
                        IconButton(onClick = onAdd) {
                            Icon(
                                Icons.Default.Add,
                                "افزودن",
                                tint = GoldPrimary
                            )
                        }
                    })
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                when {
                    state.loading -> LoadingContent()
                    state.error != null -> ErrorContent(
                        message = state.error!!,
                        onRetry = { vm.refresh() })

                    else -> Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        GlassSearchField(
                            value = state.query,
                            onValueChange = vm::onQueryChange,
                            placeholder = "جستجو در عنوان و متن...",
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    null,
                                    tint = Color.White.copy(0.6f)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = state.statusFilter == null,
                                onClick = { vm.onStatusFilterChange(null) },
                                label = { Text("همه") })
                            FilterChip(
                                selected = state.statusFilter == NewsFormViewModel.STATUS_PUBLISHED,
                                onClick = { vm.onStatusFilterChange(NewsFormViewModel.STATUS_PUBLISHED) },
                                label = { Text("منتشر شده") })
                            FilterChip(
                                selected = state.statusFilter == NewsFormViewModel.STATUS_DRAFT,
                                onClick = { vm.onStatusFilterChange(NewsFormViewModel.STATUS_DRAFT) },
                                label = { Text("پیش‌نویس") })
                            FilterChip(
                                selected = state.statusFilter == NewsFormViewModel.STATUS_ARCHIVED,
                                onClick = { vm.onStatusFilterChange(NewsFormViewModel.STATUS_ARCHIVED) },
                                label = { Text("بایگانی") })
                        }

                        Spacer(Modifier.height(12.dp))

                        if (state.filteredItems.isEmpty()) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "خبری یافت نشد",
                                    color = Color.White.copy(0.6f)
                                )
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 80.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    state.filteredItems,
                                    key = { it.id }) { news ->
                                    NewsCard(
                                        news = news,
                                        token = token,
                                        onClick = { onDetail(news.id) },
                                        onEdit = { onEdit(news.id) },
                                        onPublish = { vm.publish(news.id) },
                                        onArchive = { vm.archive(news.id) },
                                        onDelete = { newsToDelete = news })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    newsToDelete?.let { news ->
        AlertDialog(
            onDismissRequest = { newsToDelete = null },
            title = { Text("حذف خبر") },
            text = { Text("آیا از حذف خبر «${news.title}» مطمئن هستید؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.delete(news.id)
                        newsToDelete = null
                    }) {
                    Text(
                        "حذف",
                        color = RedError
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { newsToDelete = null }) { Text("انصراف") }
            })
    }
}

@Composable
private fun NewsCard(
    news: News,
    token: String?,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onPublish: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard3D(
        modifier = Modifier.clickable(
            onClick = onClick
        ),
    ) {
        Column {
            // اگر رسانه دارد، کاور اولین رسانه را نشان بده
            if (news.media.isNotEmpty()) {
                val firstMedia = news.media.first()
                MediaCover(
                    media = firstMedia,
                    token = token,
                    modifier = Modifier.fillMaxWidth()
                        .height(170.dp)
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = news.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f),
                        maxLines = 2
                    )
                    Spacer(Modifier.width(8.dp))
                    StatusChip(status = news.status)
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = news.body.take(100) + if (news.body.length > 100) "..." else "",
                    color = Color.White.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )

                // نمایش ردیف تامبنیل‌ها اگر بیش از 1 رسانه دارد
                if (news.media.size > 1) {
                    Spacer(Modifier.height(8.dp))
                    MediaThumbnailRow(
                        mediaList = news.media,
                        token = token,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        news.mediaSummary?.let {
                            Text(
                                it,
                                color = GoldPrimary.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        news.audienceSummary?.let {
                            Text(
                                "مخاطب: $it",
                                color = Color.White.copy(0.5f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                "ویرایش",
                                tint = Color.White.copy(0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        if (news.isDraft) {
                            IconButton(
                                onClick = onPublish,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Publish,
                                    "انتشار",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        if (news.isPublished) {
                            IconButton(
                                onClick = onArchive,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Archive,
                                    "بایگانی",
                                    tint = Color.White.copy(0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                "حذف",
                                tint = RedError.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (label, color) = when (status) {
        NewsFormViewModel.STATUS_PUBLISHED -> "منتشر شده" to Color(0xFF4CAF50)
        NewsFormViewModel.STATUS_DRAFT     -> "پیش‌نویس" to Color(0xFFFFA726)
        NewsFormViewModel.STATUS_ARCHIVED  -> "بایگانی" to Color(0xFF90A4AE)
        else                               -> status to Color.White.copy(0.5f)
    }
    androidx.compose.material3.Surface(
        color = color.copy(alpha = 0.15f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp)
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
        )
    }
}
