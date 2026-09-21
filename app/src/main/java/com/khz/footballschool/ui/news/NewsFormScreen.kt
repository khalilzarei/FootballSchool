package com.khz.footballschool.ui.news

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khz.footballschool.core.util.MediaPickerHelper
import com.khz.footballschool.core.util.appViewModel
import com.khz.footballschool.ui.components.GlassBackground
import com.khz.footballschool.ui.components.GlassButton
import com.khz.footballschool.ui.components.GlassCard3D
import com.khz.footballschool.ui.components.GlassSectionTitle
import com.khz.footballschool.ui.components.GlassTextField
import com.khz.footballschool.ui.components.GlassTopBar
import com.khz.footballschool.ui.components.LoadingContent
import com.khz.footballschool.ui.theme.GoldPrimary
import com.khz.footballschool.ui.theme.RedError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * فرم ساخت و ویرایش خبر — با امکان آپلود عکس و فیلم.
 * @param newsId مقدار null یعنی «خبر جدید»
 */
@Composable
fun NewsFormScreen(
    newsId: Int? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: NewsFormViewModel = appViewModel()
    val state by viewModel.state.collectAsState()

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

    /**
     * پردازش Uri انتخاب‌شده. کپی فایل روی Dispatchers.IO انجام می‌شود تا
     * برای ویدیوهای چند ده مگابایتی رابط کاربری قفل نشود.
     */
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

    // عکس: Photo Picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        handlePickedUri(
            uri,
            isVideo = false
        )
    }

    // فیلم: Photo Picker روی همه‌ی نسخه‌ها ویدیو برنمی‌گرداند، پس OpenDocument
    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
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
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = state.globalAudience,
                                onClick = { viewModel.onGlobalAudienceChange(!state.globalAudience) },
                                enabled = !state.saving,
                                label = { Text("همه") })

                            NewsFormViewModel.ROLES.forEach { (role, label) ->
                                FilterChip(
                                    selected = !state.globalAudience && role in state.selectedRoles,
                                    onClick = { viewModel.onRoleToggle(role) },
                                    enabled = !state.saving && !state.globalAudience,
                                    label = { Text(label) })
                            }
                        }
                    }

                    if (!state.globalAudience) {
                        item {
                            Text(
                                text = "اگر «همه» خاموش باشد، فقط نقش‌های انتخاب‌شده خبر را می‌بینند.",
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    item { GlassSectionTitle("عکس‌ها و فیلم‌ها") }

                    item {
                        Text(
                            text = "${state.effectiveMediaCount} از " + "${NewsFormViewModel.MAX_MEDIA_PER_NEWS} فایل · " + "فرمت‌های مجاز: jpg, png, gif, webp, mp4, webm, mov",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            GlassButton(
                                text = "افزودن عکس",
                                onClick = {
                                    imagePicker.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
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

                    // فایل‌های ذخیره‌شده روی سرور
                    items(
                        count = state.existingMedia.size,
                        key = { index -> "existing-${state.existingMedia[index].id}" }) { index ->
                        val media = state.existingMedia[index]
                        val removed = media.id in state.removedMediaIds

                        MediaRow(
                            title = media.displayName,
                            subtitle = buildString {
                                append(if (media.isVideo) "فیلم" else "عکس")
                                append(" · ")
                                append(media.humanSize)
                                media.humanDuration?.let { append(" · $it") }
                                if (removed) append(" · حذف خواهد شد")
                            },
                            isVideo = media.isVideo,
                            dimmed = removed,
                            enabled = !state.saving,
                            undoMode = removed,
                            onAction = {
                                if (removed) viewModel.undoRemoveExistingMedia(media.id)
                                else viewModel.removeExistingMedia(media.id)
                            })
                    }

                    // فایل‌های در انتظار آپلود
                    items(
                        count = state.pendingFiles.size,
                        key = { index -> "pending-$index" }) { index ->
                        val file = state.pendingFiles[index]

                        MediaRow(
                            title = file.name,
                            subtitle = buildString {
                                append(if (file.isVideo) "فیلم" else "عکس")
                                if (file.sizeBytes > 0) {
                                    append(" · "); append(formatBytes(file.sizeBytes))
                                }
                                append(" · در انتظار آپلود")
                            },
                            isVideo = file.isVideo,
                            enabled = !state.saving,
                            onAction = { viewModel.removePendingFile(index) })
                    }

                    if (state.isUploading) {
                        item {
                            GlassCard3D {
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
                            GlassCard3D {
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

/**
 * یک ردیف رسانه با دکمه‌ی حذف.
 * @param dimmed اگر true باشد کم‌رنگ نمایش داده می‌شود (یعنی حذف خواهد شد)
 * @param undoMode دکمه به‌جای حذف، «بازگردانی» انجام می‌دهد
 */
@Composable
private fun MediaRow(
    title: String,
    subtitle: String,
    isVideo: Boolean,
    dimmed: Boolean = false,
    enabled: Boolean = true,
    undoMode: Boolean = false,
    onAction: () -> Unit
) {
    GlassCard3D {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isVideo) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "فیلم",
                    tint = GoldPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (dimmed) Color.White.copy(alpha = 0.45f) else Color.White,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
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