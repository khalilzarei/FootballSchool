package com.khz.malekadmin.ui.news

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.khz.malekadmin.core.util.MediaPickerHelper
import com.khz.malekadmin.core.util.appViewModel
import com.khz.malekadmin.domain.model.Media
import com.khz.malekadmin.ui.components.AuthenticatedAsyncImage
import com.khz.malekadmin.ui.components.GlassBackground
import com.khz.malekadmin.ui.components.GlassButton
import com.khz.malekadmin.ui.components.GlassCard3D
import com.khz.malekadmin.ui.components.GlassSectionTitle
import com.khz.malekadmin.ui.components.GlassTextField
import com.khz.malekadmin.ui.components.GlassTopBar
import com.khz.malekadmin.ui.components.LoadingContent
import com.khz.malekadmin.ui.components.rememberAuthToken
import com.khz.malekadmin.ui.theme.BlueAccent
import com.khz.malekadmin.ui.theme.GoldPrimary
import com.khz.malekadmin.ui.theme.RedError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * فرم ساخت و ویرایش خبر — با امکان آپلود عکس و فیلم و نمایش پیش‌نمایش.
 * + مخاطبان جدید: گروه سنی و کلاس
 * @param newsId مقدار null یعنی «خبر جدید»
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsFormScreen(
    newsId: Int? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: NewsFormViewModel = appViewModel()
    val state by viewModel.state.collectAsState()
    val token = rememberAuthToken()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(newsId) { viewModel.load(newsId) }

    val saving by rememberUpdatedState(state.saving)

    DisposableEffect(Unit) {
        onDispose { if (!saving) MediaPickerHelper.clearTempFiles(context) }
    }

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) {
            viewModel.consumeSavedFlag()
            MediaPickerHelper.clearTempFiles(context)
            onSaved()
        }
    }

    fun handlePickedUri(
        uri: android.net.Uri?,
        isVideo: Boolean
    ) {
        if (uri == null) return
        scope.launch {
            val prepared = withContext(Dispatchers.IO) {
                val (name, size) = MediaPickerHelper.queryFileInfo(
                    context,
                    uri
                )
                val part = MediaPickerHelper.filePart(
                    context,
                    uri
                )
                Triple(
                    name,
                    size,
                    part
                )
            }
            val (name, size, part) = prepared
            when {
                !MediaPickerHelper.isAllowed(name) -> viewModel.onPickError("نوع فایل مجاز نیست (فقط عکس یا فیلم): $name")
                part == null                       -> viewModel.onPickError("خواندن فایل «$name» ممکن نشد")
                else                               -> viewModel.addPendingFile(
                    uri,
                    part,
                    isVideo,
                    name,
                    size
                )
            }
        }
    }

    val imagePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
        handlePickedUri(
            uri,
            isVideo = false
        )
    }
    val videoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
        handlePickedUri(
            uri,
            isVideo = true
        )
    }

    GlassBackground {
        Box(Modifier.fillMaxSize()) {
            GlassTopBar(
                title = if (state.isEditing) "ویرایش خبر" else "خبر جدید",
                onBack = onBack
            )

            when {
                state.loadingNews -> LoadingContent()
                else              -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 64.dp),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { GlassSectionTitle("اطلاعات خبر") }

                    item {
                        GlassTextField(
                            value = state.title,
                            onValueChange = viewModel::onTitleChange,
                            label = "عنوان خبر",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        GlassTextField(
                            value = state.body,
                            onValueChange = viewModel::onBodyChange,
                            label = "متن خبر",
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false
                        )
                    }

                    item { GlassSectionTitle("وضعیت انتشار") }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NewsFormViewModel.STATUSES.forEach { status ->
                                FilterChip(
                                    selected = state.status == status,
                                    onClick = { viewModel.onStatusChange(status) },
                                    enabled = !state.saving,
                                    label = { Text(statusLabel(status)) })
                            }
                        }
                    }

                    item { GlassSectionTitle("مخاطبان") }

                    item {
                        GlassCard3D(shape = RoundedCornerShape(16.dp),) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                // همه
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "ارسال برای همه",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    FilterChip(
                                        selected = state.globalAudience,
                                        onClick = { viewModel.onGlobalAudienceChange(!state.globalAudience) },
                                        enabled = !state.saving,
                                        label = { Text(if (state.globalAudience) "فعال" else "غیرفعال") })
                                }

                                if (!state.globalAudience) {
                                    Text(
                                        text = "اگر «همه» خاموش باشد، فقط مخاطبان انتخاب‌شده خبر را می‌بینند. می‌توانید نقش، گروه سنی و کلاس را ترکیب کنید.",
                                        color = Color.White.copy(alpha = 0.6f),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                // نقش‌ها
                                Text(
                                    "نقش‌ها",
                                    color = GoldPrimary,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    NewsFormViewModel.ROLES.forEach { (role, label) ->
                                        FilterChip(
                                            selected = !state.globalAudience && role in state.selectedRoles,
                                            onClick = { viewModel.onRoleToggle(role) },
                                            enabled = !state.saving && !state.globalAudience,
                                            label = { Text(label) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = GoldPrimary.copy(alpha = 0.2f),
                                                selectedLabelColor = GoldPrimary
                                            )
                                        )
                                    }
                                }

                                // گروه‌های سنی
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "گروه‌های سنی${if (state.selectedAgeGroupIds.isNotEmpty()) " (${state.selectedAgeGroupIds.size})" else ""}",
                                        color = GoldPrimary,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    if (state.loadingAudienceOptions) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = GoldPrimary
                                        )
                                    } else {
                                        TextButton(onClick = { viewModel.loadAudienceOptions() }) {
                                            Text(
                                                "بارگذاری مجدد",
                                                color = GoldPrimary,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }

                                if (state.audienceOptionsError != null) {
                                    Text(
                                        state.audienceOptionsError!!,
                                        color = RedError,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                if (state.availableAgeGroups.isEmpty() && !state.loadingAudienceOptions) {
                                    Text(
                                        "گروه سنی فعالی یافت نشد",
                                        color = Color.White.copy(0.5f),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } else {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        state.availableAgeGroups.forEach { ag ->
                                            val selected = ag.id in state.selectedAgeGroupIds
                                            FilterChip(
                                                selected = !state.globalAudience && selected,
                                                onClick = { viewModel.onAgeGroupToggle(ag.id) },
                                                enabled = !state.saving && !state.globalAudience,
                                                label = { Text(ag.title) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = BlueAccent.copy(alpha = 0.2f),
                                                    selectedLabelColor = BlueAccent
                                                )
                                            )
                                        }
                                    }
                                }

                                // کلاس‌ها
                                Text(
                                    "کلاس‌ها${if (state.selectedClassIds.isNotEmpty()) " (${state.selectedClassIds.size})" else ""}",
                                    color = GoldPrimary,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )

                                if (state.availableClasses.isEmpty() && !state.loadingAudienceOptions) {
                                    Text(
                                        "کلاس فعالی یافت نشد",
                                        color = Color.White.copy(0.5f),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } else {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        state.availableClasses.forEach { cls ->
                                            val selected = cls.id in state.selectedClassIds
                                            FilterChip(
                                                selected = !state.globalAudience && selected,
                                                onClick = { viewModel.onClassToggle(cls.id) },
                                                enabled = !state.saving && !state.globalAudience,
                                                label = {
                                                    Column {
                                                        Text(
                                                            cls.title,
                                                            style = MaterialTheme.typography.labelMedium
                                                        )
                                                        cls.ageGroupTitle?.let {
                                                            Text(
                                                                it,
                                                                style = MaterialTheme.typography.labelSmall,
                                                                color = Color.White.copy(0.6f)
                                                            )
                                                        }
                                                    }
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                                                    selectedLabelColor = Color(0xFF4CAF50)
                                                )
                                            )
                                        }
                                    }
                                }

                                if (state.hasSpecificAudience && !state.globalAudience) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "مجموع: ${state.selectedRoles.size} نقش · ${state.selectedAgeGroupIds.size} گروه سنی · ${state.selectedClassIds.size} کلاس",
                                            color = Color.White.copy(0.7f),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        TextButton(onClick = { viewModel.clearAllSpecificAudiences() }) {
                                            Text(
                                                "پاک کردن",
                                                color = RedError,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { GlassSectionTitle("عکس‌ها و فیلم‌ها") }

                    item {
                        Text(
                            text = "${state.effectiveMediaCount} از ${NewsFormViewModel.MAX_MEDIA_PER_NEWS} فایل · فرمت‌های مجاز: jpg, png, gif, webp, mp4, webm, mov",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GlassButton(
                                text = "افزودن عکس",
                                onClick = {
                                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                enabled = !state.saving && state.effectiveMediaCount < NewsFormViewModel.MAX_MEDIA_PER_NEWS,
                                modifier = Modifier.weight(1f)
                            )
                            GlassButton(
                                text = "افزودن فیلم",
                                onClick = { videoPicker.launch(arrayOf("video/*")) },
                                enabled = !state.saving && state.effectiveMediaCount < NewsFormViewModel.MAX_MEDIA_PER_NEWS,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // فایل‌های ذخیره‌شده روی سرور - با پیش‌نمایش عکس
                    items(
                        count = state.existingMedia.size,
                        key = { index -> "existing-${state.existingMedia[index].id}" }) { index ->
                        val media = state.existingMedia[index]
                        val removed = media.id in state.removedMediaIds
                        ExistingMediaRow(
                            media = media,
                            token = token,
                            dimmed = removed,
                            enabled = !state.saving,
                            undoMode = removed,
                            onAction = {
                                if (removed) viewModel.undoRemoveExistingMedia(media.id)
                                else viewModel.removeExistingMedia(media.id)
                            })
                    }

                    // فایل‌های در انتظار آپلود - با پیش‌نمایش محلی
                    items(
                        count = state.pendingFiles.size,
                        key = { index -> "pending-$index" }) { index ->
                        val file = state.pendingFiles[index]
                        PendingMediaRow(
                            file = file,
                            enabled = !state.saving,
                            onRemove = { viewModel.removePendingFile(index) })
                    }

                    if (state.isUploading) {
                        item {
                            GlassCard3D() {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = GoldPrimary
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = "در حال آپلود ${state.uploadedCount} از ${state.totalToUpload}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    state.error?.let { message ->
                        item {
                            GlassCard3D() {
                                Text(
                                    text = message,
                                    color = RedError,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(4.dp))
                        GlassButton(
                            text = if (state.isEditing) "ذخیره تغییرات" else "ساخت خبر",
                            onClick = { viewModel.save() },
                            enabled = !state.saving,
                            loading = state.saving,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExistingMediaRow(
    media: Media,
    token: String?,
    dimmed: Boolean = false,
    enabled: Boolean = true,
    undoMode: Boolean = false,
    onAction: () -> Unit
) {
    GlassCard3D() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // پیش‌نمایش تصویر
            Box(
                modifier = Modifier.size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = if (dimmed) 0.03f else 0.08f))
            ) {
                val url = media.thumbnailUrl
                        ?: media.streamUrl
                        ?: media.url
                if (!url.isNullOrBlank()) {
                    AuthenticatedAsyncImage(
                        url = url,
                        token = token,
                        contentDescription = media.displayName,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (media.isVideo) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                if (media.isVideo) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            Modifier.size(28.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
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
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = media.displayName,
                    color = if (dimmed) Color.White.copy(alpha = 0.45f) else Color.White,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = buildString {
                        append(if (media.isVideo) "فیلم" else "عکس")
                        append(" · ${media.humanSize}")
                        media.humanDuration?.let { append(" · $it") }
                        if (dimmed) append(" · حذف خواهد شد")
                    },
                    color = Color.White.copy(alpha = if (dimmed) 0.35f else 0.65f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onAction,
                enabled = enabled
            ) {
                Icon(
                    imageVector = if (undoMode) Icons.Default.Refresh else Icons.Default.Close,
                    contentDescription = if (undoMode) "بازگردانی" else "حذف",
                    tint = if (enabled) Color.White.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun PendingMediaRow(
    file: NewsFormViewModel.PendingFile,
    enabled: Boolean = true,
    onRemove: () -> Unit
) {
    GlassCard3D() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // پیش‌نمایش محلی
            Box(
                modifier = Modifier.size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                AsyncImage(
                    model = file.uri,
                    contentDescription = file.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (file.isVideo) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            Modifier.size(28.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
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
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = buildString {
                        append(if (file.isVideo) "فیلم" else "عکس")
                        if (file.sizeBytes > 0) {
                            append(" · ${formatBytes(file.sizeBytes)}")
                        }
                        append(" · در انتظار آپلود")
                    },
                    color = Color.White.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onRemove,
                enabled = enabled
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "حذف",
                    tint = Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun statusLabel(status: String): String = when (status) {
    NewsFormViewModel.STATUS_DRAFT     -> "پیش‌نویس"
    NewsFormViewModel.STATUS_PUBLISHED -> "منتشرشده"
    NewsFormViewModel.STATUS_ARCHIVED  -> "بایگانی"
    else                               -> status
}

private fun formatBytes(bytes: Long): String = when {
    bytes <= 0L           -> "-"
    bytes < 1024L         -> "$bytes بایت"
    bytes < 1024L * 1024L -> "${bytes / 1024} کیلوبایت"
    else                  -> String.format(
        "%.1f مگابایت",
        bytes / (1024.0 * 1024.0)
    )
}
