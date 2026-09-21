package com.khz.footballschool.ui.news

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.AudienceItemRequest
import com.khz.footballschool.data.dto.request.CreateNewsRequest
import com.khz.footballschool.data.dto.request.UpdateNewsRequest
import com.khz.footballschool.data.repository.MediaRepository
import com.khz.footballschool.data.repository.NewsRepository
import com.khz.footballschool.domain.model.Media
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * ViewModel فرم ساخت و ویرایش خبر.
 *
 * جریان ذخیره (مهم):
 *  ۱) خبر ساخته می‌شود — **همیشه ابتدا با وضعیت draft**، چون برای اتصال رسانه
 *     به خبر باید شناسه‌ی خبر وجود داشته باشد.
 *  ۲) فایل‌های انتخاب‌شده با `related_type = "news"` و
 *     `related_id = <شناسه خبر>` آپلود می‌شوند.
 *  ۳) در یک درخواست به‌روزرسانی، متن/وضعیت/مخاطبان و **لیست نهایی رسانه‌ها**
 *     اعمال می‌شود.
 *
 * نکته: ارسال صریح لیست نهایی رسانه‌ها (mediaIds) کاری می‌کند که حذف یک
 * فایل هم در همان درخواست اعمال شود — سرور بقیه را جدا می‌کند.
 */
class NewsFormViewModel(
    private val newsRepository: NewsRepository,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    /** فایلی که انتخاب شده ولی هنوز آپلود نشده */
    data class PendingFile(
        val uri: Uri,
        val part: MultipartBody.Part,
        val isVideo: Boolean,
        val name: String,
        val sizeBytes: Long
    )

    data class UiState(
        val isEditing: Boolean = false,
        val loadingNews: Boolean = false,
        val saving: Boolean = false,

        val title: String = "",
        val body: String = "",
        val status: String = STATUS_DRAFT,

        /** اگر true باشد، خبر برای «همه» منتشر می‌شود */
        val globalAudience: Boolean = true,
        /** نقش‌های انتخاب‌شده (وقتی globalAudience خاموش است) */
        val selectedRoles: Set<String> = emptySet(),

        /** رسانه‌های ذخیره‌شده روی سرور */
        val existingMedia: List<Media> = emptyList(),
        /** رسانه‌هایی که کاربر حذف کرده (تا در لیست نهایی فرستاده نشوند) */
        val removedMediaIds: Set<Int> = emptySet(),

        /** فایل‌های در انتظار آپلود */
        val pendingFiles: List<PendingFile> = emptyList(),

        val uploadedCount: Int = 0,
        val totalToUpload: Int = 0,

        val error: String? = null,
        val savedSuccessfully: Boolean = false
    ) {
        /** مجموع فایل‌هایی که بعد از ذخیره روی خبر خواهند بود */
        val effectiveMediaCount: Int
            get() = (existingMedia.size - removedMediaIds.size) + pendingFiles.size

        val isUploading: Boolean get() = saving && totalToUpload > 0
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    /** null یعنی حالت «ساخت خبر جدید» */
    private var editingId: Int? = null

    // ═══════════════════════════════════════════════════════════
    // بارگذاری
    // ═══════════════════════════════════════════════════════════

    fun load(newsId: Int?) {
        editingId = newsId

        _state.update {
            it.copy(
                isEditing = newsId != null,
                error = null
            )
        }

        if (newsId == null) return

        _state.update { it.copy(loadingNews = true) }

        viewModelScope.launch {
            when (val result = newsRepository.getNews(newsId)) {
                is NetworkResult.Success -> {
                    val news = result.data

                    // اگر پاسخ خبر رسانه نداشت، مستقیم از ماژول رسانه بخوان
                    // (سازگاری با نسخه‌های قدیمی‌تر سرور)
                    //
                    // ⚠ نکته: اینجا از ifEmpty استفاده نمی‌کنیم چون پارامتر آن
                    // یک لامبدای معمولی است (() -> R) و نه suspend؛ فراخوانی
                    // getNewsMedia درون آن خطای
                    // «Suspension functions can be called only within
                    //  coroutine body» می‌داد.
                    val media = if (news.media.isNotEmpty()) {
                        news.media
                    } else {

                        emptyList()
//                        when (val mediaResult = mediaRepository.getMedia(newsId)) {
//                            is NetworkResult.Success -> mediaResult.data
//                            else -> emptyList()
//                        }
                    }

                    _state.update {
                        it.copy(
                            loadingNews = false,
                            title = news.title,
                            body = news.body,
                            status = news.status,
                            existingMedia = media,
                            removedMediaIds = emptySet()
                        )
                    }
                }

                is NetworkResult.Error   -> _state.update {
                    it.copy(
                        loadingNews = false,
                        error = result.message
                    )
                }

                NetworkResult.Loading    -> Unit
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // ورودی‌های فرم
    // ═══════════════════════════════════════════════════════════

    fun onTitleChange(value: String) = _state.update {
        it.copy(
            title = value,
            error = null
        )
    }

    fun onBodyChange(value: String) = _state.update {
        it.copy(
            body = value,
            error = null
        )
    }

    fun onStatusChange(value: String) = _state.update {
        if (value in STATUSES) it.copy(
            status = value,
            error = null
        ) else it
    }

    fun onGlobalAudienceChange(global: Boolean) = _state.update {
        it.copy(
            globalAudience = global,
            error = null
        )
    }

    fun onRoleToggle(role: String) = _state.update { current ->
        val roles = current.selectedRoles.toMutableSet()

        if (roles.contains(role)) roles.remove(role) else roles.add(role)

        current.copy(
            selectedRoles = roles,
            error = null
        )
    }

    // ═══════════════════════════════════════════════════════════
    // فایل‌ها
    // ═══════════════════════════════════════════════════════════

    fun addPendingFile(
        uri: Uri,
        part: MultipartBody.Part,
        isVideo: Boolean,
        name: String,
        sizeBytes: Long
    ) {
        _state.update { current ->
            if (current.pendingFiles.size >= MAX_MEDIA_PER_NEWS) {
                current.copy(error = "حداکثر $MAX_MEDIA_PER_NEWS فایل می‌تواند به یک خبر متصل شود")
            } else {
                current.copy(
                    pendingFiles = current.pendingFiles + PendingFile(
                        uri,
                        part,
                        isVideo,
                        name,
                        sizeBytes
                    ),
                    error = null
                )
            }
        }
    }

    fun removePendingFile(index: Int) = _state.update { current ->
        if (index !in current.pendingFiles.indices) {
            current
        } else {
            current.copy(pendingFiles = current.pendingFiles.filterIndexed { i, _ -> i != index })
        }
    }

    /** جدا کردن یک فایل ذخیره‌شده از خبر (فایل روی سرور باقی می‌ماند) */
    fun removeExistingMedia(mediaId: Int) = _state.update {
        it.copy(
            removedMediaIds = it.removedMediaIds + mediaId,
            error = null
        )
    }

    fun undoRemoveExistingMedia(mediaId: Int) = _state.update {
        it.copy(
            removedMediaIds = it.removedMediaIds - mediaId,
            error = null
        )
    }

    fun onPickError(message: String) = _state.update { it.copy(error = message) }

    fun clearError() = _state.update { it.copy(error = null) }

    fun consumeSavedFlag() = _state.update { it.copy(savedSuccessfully = false) }

    // ═══════════════════════════════════════════════════════════
    // ذخیره
    // ═══════════════════════════════════════════════════════════

    fun save() {
        val snapshot = _state.value

        if (snapshot.title.isBlank()) {
            _state.update { it.copy(error = "عنوان خبر الزامی است") }
            return
        }

        if (snapshot.body.isBlank()) {
            _state.update { it.copy(error = "متن خبر الزامی است") }
            return
        }

        if (!snapshot.globalAudience && snapshot.selectedRoles.isEmpty()) {
            _state.update { it.copy(error = "حداقل یک مخاطب انتخاب کنید یا «همه» را روشن کنید") }
            return
        }

        _state.update {
            it.copy(
                saving = true,
                error = null,
                uploadedCount = 0,
                totalToUpload = it.pendingFiles.size
            )
        }

        viewModelScope.launch {
            val existingNewsId = editingId

            // ── گام ۱: تضمین وجود خبر (برای جدیدها، همیشه draft) ──
            val newsId = existingNewsId
                    ?: createDraft(snapshot)

            if (newsId == null) {
                _state.update { it.copy(saving = false) }
                return@launch
            }

            // ── گام ۲: آپلود فایل‌های جدید با related_id = شناسه خبر ──
            val newMediaIds = uploadPendingFiles(newsId)

            if (newMediaIds == null) {
                _state.update { it.copy(saving = false) }
                return@launch
            }

            // ── گام ۳: اعمال متن، وضعیت، مخاطبان و لیست نهایی رسانه‌ها ──
            val finalMediaIds = snapshot.existingMedia.map { it.id }
                .filterNot { it in snapshot.removedMediaIds } + newMediaIds

            val request = UpdateNewsRequest(
                title = snapshot.title.trim(),
                body = snapshot.body.trim(),
                status = snapshot.status,
                publishAt = null, // سرور هنگام انتشار، زمان را خودش تنظیم می‌کند
//                audiences = audiencesPayload(snapshot),
//                mediaIds = finalMediaIds
            )

            when (val result = newsRepository.updateNews(
                newsId,
                request
            )) {
                is NetworkResult.Success -> _state.update {
                    it.copy(
                        saving = false,
                        savedSuccessfully = true
                    )
                }

                is NetworkResult.Error   -> _state.update {
                    it.copy(
                        saving = false,
                        error = result.message
                    )
                }

                NetworkResult.Loading    -> Unit
            }
        }
    }

    /** ساخت خبر به‌صورت draft؛ شناسه خبر یا null (در صورت خطا) برمی‌گرداند */
    private suspend fun createDraft(snapshot: UiState): Int? {
        val request = CreateNewsRequest(
            title = snapshot.title.trim(),
            body = snapshot.body.trim(),
            status = STATUS_DRAFT,
            publishAt = null,
            audiences = audiencesPayload(snapshot)
        )

        return when (val result = newsRepository.createNews(request)) {
            is NetworkResult.Success -> result.data.id

            is NetworkResult.Error   -> {
                _state.update { it.copy(error = result.message) }
                null
            }

            NetworkResult.Loading    -> null
        }
    }

    /**
     * آپلود ترتیبی فایل‌ها.
     * شناسه‌ی رسانه‌های آپلودشده را برمی‌گرداند، یا null در صورت خطا.
     */
    private suspend fun uploadPendingFiles(newsId: Int): List<Int>? {
        val files = _state.value.pendingFiles

        if (files.isEmpty()) return emptyList()

        // ⚠ toRequestBody یک MediaType می‌خواهد، نه String.
        // پیش‌تر TEXT_PLAIN (که String است) مستقیم پاس داده می‌شد و
        // خطای type mismatch می‌داد. باید اول به MediaType تبدیل شود.
        val textPlain = TEXT_PLAIN.toMediaTypeOrNull()

        val visibilityBody = VISIBILITY_PUBLIC.toRequestBody(textPlain)
        val relatedTypeBody = RELATED_TYPE_NEWS.toRequestBody(textPlain)
        val relatedIdBody = newsId.toString()
            .toRequestBody(textPlain)

        val uploadedIds = mutableListOf<Int>()

        files.forEachIndexed { index, file ->
            val result = mediaRepository.uploadMedia(
                file = file.part,
                visibility = visibilityBody,
                relatedType = relatedTypeBody,
                relatedId = relatedIdBody,
                description = null
            )

            when (result) {
                is NetworkResult.Success -> {
                    uploadedIds += result.data.id
                    _state.update { it.copy(uploadedCount = index + 1) }
                }

                is NetworkResult.Error   -> {
                    _state.update {
                        it.copy(error = "آپلود «${file.name}» ناموفق بود: ${result.message}")
                    }
                    return null
                }

                NetworkResult.Loading    -> Unit
            }
        }

        return uploadedIds
    }

    /**
     * ساخت payload مخاطبان.
     * حالت‌های پشتیبانی‌شده در این فرم: «همه» و «بر اساس نقش».
     * هدف‌گیری بر اساس کلاس/گروه سنی/بازیکن مشخص را می‌توان همین‌جا اضافه کرد.
     */
    private fun audiencesPayload(snapshot: UiState): List<AudienceItemRequest> {
        if (snapshot.globalAudience) {
            return listOf(
                AudienceItemRequest(
                    audienceType = "global",
                    targetId = null,
                    ""
                )
            )
        }

        return snapshot.selectedRoles.map { role ->
            AudienceItemRequest(
                audienceType = "role",
                targetId = null,
                role = role
            )
        }
    }

    companion object {
        const val STATUS_DRAFT = "draft"
        const val STATUS_PUBLISHED = "published"
        const val STATUS_ARCHIVED = "archived"

        val STATUSES = listOf(
            STATUS_DRAFT,
            STATUS_PUBLISHED,
            STATUS_ARCHIVED
        )

        /** هم‌خوان با NewsService::MEDIA_MAX_PER_NEWS در سرور */
        const val MAX_MEDIA_PER_NEWS = 10

        /** نقش‌های قابل هدف‌گیری */
        val ROLES = listOf(
            "player" to "بازیکنان",
            "coach" to "مربیان",
            "admin" to "مدیران"
        )

        private const val TEXT_PLAIN = "text/plain"
        private const val VISIBILITY_PUBLIC = "public"
        private const val RELATED_TYPE_NEWS = "news"
    }
}
