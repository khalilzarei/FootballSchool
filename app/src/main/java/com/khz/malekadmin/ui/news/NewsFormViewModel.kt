package com.khz.malekadmin.ui.news

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.dto.request.AudienceItemRequest
import com.khz.malekadmin.data.dto.request.CreateNewsRequest
import com.khz.malekadmin.data.dto.request.UpdateNewsRequest
import com.khz.malekadmin.data.repository.AgeGroupRepository
import com.khz.malekadmin.data.repository.ClassRepository
import com.khz.malekadmin.data.repository.MediaRepository
import com.khz.malekadmin.data.repository.NewsRepository
import com.khz.malekadmin.domain.model.AgeGroup
import com.khz.malekadmin.domain.model.FootballClass
import com.khz.malekadmin.domain.model.Media
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
 * جریان ذخیره:
 *  ۱) خبر ساخته می‌شود — همیشه ابتدا با وضعیت draft
 *  ۲) فایل‌های انتخاب‌شده با related_type = "news" و related_id = <id> آپلود می‌شوند
 *  ۳) در یک درخواست به‌روزرسانی، متن/وضعیت/مخاطبان و لیست نهایی رسانه‌ها اعمال می‌شود
 *
 * مخاطبان پشتیبانی شده:
 *  - global
 *  - role (player, coach, admin)
 *  - age_group (target_id = age_group.id)
 *  - class (target_id = class.id)
 */
class NewsFormViewModel(
    private val newsRepository: NewsRepository,
    private val mediaRepository: MediaRepository,
    private val ageGroupRepository: AgeGroupRepository? = null,
    private val classRepository: ClassRepository? = null
) : ViewModel() {

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
        val globalAudience: Boolean = true,
        val selectedRoles: Set<String> = emptySet(),
        val selectedAgeGroupIds: Set<Int> = emptySet(),
        val selectedClassIds: Set<Int> = emptySet(),
        val availableAgeGroups: List<AgeGroup> = emptyList(),
        val availableClasses: List<FootballClass> = emptyList(),
        val loadingAudienceOptions: Boolean = false,
        val audienceOptionsError: String? = null,
        val existingMedia: List<Media> = emptyList(),
        val removedMediaIds: Set<Int> = emptySet(),
        val pendingFiles: List<PendingFile> = emptyList(),
        val uploadedCount: Int = 0,
        val totalToUpload: Int = 0,
        val error: String? = null,
        val savedSuccessfully: Boolean = false
    ) {
        val effectiveMediaCount: Int
            get() = (existingMedia.size - removedMediaIds.size) + pendingFiles.size
        val isUploading: Boolean get() = saving && totalToUpload > 0

        val hasSpecificAudience: Boolean
            get() = selectedRoles.isNotEmpty() || selectedAgeGroupIds.isNotEmpty() || selectedClassIds.isNotEmpty()
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var editingId: Int? = null

    init {
        loadAudienceOptions()
    }

    fun loadAudienceOptions() {
        val ageRepo = ageGroupRepository
                ?: return
        val classRepo = classRepository
                ?: return

        _state.update {
            it.copy(
                loadingAudienceOptions = true,
                audienceOptionsError = null
            )
        }
        viewModelScope.launch {
            // گروه‌های سنی
            val ageGroupsResult = ageRepo.getAgeGroups(
                page = 1,
                perPage = 100,
                status = "active"
            )
            val classesResult = classRepo.getClasses(
                page = 1,
                perPage = 100,
                status = "active"
            )

            var ageGroups: List<AgeGroup> = emptyList()
            var classes: List<FootballClass> = emptyList()
            var error: String? = null

            when (ageGroupsResult) {
                is NetworkResult.Success -> ageGroups = ageGroupsResult.data
                is NetworkResult.Error   -> error = ageGroupsResult.message
                else                     -> {}
            }
            when (classesResult) {
                is NetworkResult.Success -> classes = classesResult.data.items
                is NetworkResult.Error   -> error = (error?.let { "$it | " }
                        ?: "") + classesResult.message

                else                     -> {}
            }

            _state.update {
                it.copy(
                    availableAgeGroups = ageGroups,
                    availableClasses = classes,
                    loadingAudienceOptions = false,
                    audienceOptionsError = error
                )
            }
        }
    }

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
                    val media = news.media
                    // تشخیص مخاطب از روی audiences
                    val isGlobal = news.audiences.isEmpty() || news.audiences.any { it.audienceType == "global" }
                    val roles = if (isGlobal) emptySet() else news.audiences.filter { it.audienceType == "role" }
                        .mapNotNull { it.role }
                        .toSet()
                    val ageGroupIds = if (isGlobal) emptySet() else news.audiences.filter { it.audienceType == "age_group" }
                        .mapNotNull { it.targetId }
                        .toSet()
                    val classIds = if (isGlobal) emptySet() else news.audiences.filter { it.audienceType == "class" }
                        .mapNotNull { it.targetId }
                        .toSet()

                    _state.update {
                        it.copy(
                            loadingNews = false,
                            title = news.title,
                            body = news.body,
                            status = news.status,
                            existingMedia = media,
                            removedMediaIds = emptySet(),
                            globalAudience = isGlobal,
                            selectedRoles = roles,
                            selectedAgeGroupIds = ageGroupIds,
                            selectedClassIds = classIds
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
        // اگر مخاطب خاص انتخاب شد، global خاموش شود
        val newGlobal = if (roles.isNotEmpty() || current.selectedAgeGroupIds.isNotEmpty() || current.selectedClassIds.isNotEmpty()) false else current.globalAudience
        current.copy(
            selectedRoles = roles,
            globalAudience = newGlobal,
            error = null
        )
    }

    fun onAgeGroupToggle(id: Int) = _state.update { current ->
        val set = current.selectedAgeGroupIds.toMutableSet()
        if (set.contains(id)) set.remove(id) else set.add(id)
        val newGlobal = if (set.isNotEmpty() || current.selectedRoles.isNotEmpty() || current.selectedClassIds.isNotEmpty()) false else current.globalAudience
        current.copy(
            selectedAgeGroupIds = set,
            globalAudience = newGlobal,
            error = null
        )
    }

    fun onClassToggle(id: Int) = _state.update { current ->
        val set = current.selectedClassIds.toMutableSet()
        if (set.contains(id)) set.remove(id) else set.add(id)
        val newGlobal = if (set.isNotEmpty() || current.selectedRoles.isNotEmpty() || current.selectedAgeGroupIds.isNotEmpty()) false else current.globalAudience
        current.copy(
            selectedClassIds = set,
            globalAudience = newGlobal,
            error = null
        )
    }

    fun clearAllSpecificAudiences() = _state.update {
        it.copy(
            selectedRoles = emptySet(),
            selectedAgeGroupIds = emptySet(),
            selectedClassIds = emptySet(),
            globalAudience = true
        )
    }

    fun addPendingFile(
        uri: Uri,
        part: MultipartBody.Part,
        isVideo: Boolean,
        name: String,
        sizeBytes: Long
    ) {
        _state.update { current ->
            if (current.effectiveMediaCount >= MAX_MEDIA_PER_NEWS) {
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
        if (index !in current.pendingFiles.indices) current
        else current.copy(pendingFiles = current.pendingFiles.filterIndexed { i, _ -> i != index })
    }

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
        if (!snapshot.globalAudience && !snapshot.hasSpecificAudience) {
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
            val newsId = existingNewsId
                    ?: createDraft(snapshot)
            if (newsId == null) {
                _state.update { it.copy(saving = false) }
                return@launch
            }

            val newMediaIds = uploadPendingFiles(newsId)
            if (newMediaIds == null) {
                _state.update { it.copy(saving = false) }
                return@launch
            }

            val finalMediaIds = snapshot.existingMedia.map { it.id }
                .filterNot { it in snapshot.removedMediaIds } + newMediaIds

            val request = UpdateNewsRequest(
                title = snapshot.title.trim(),
                body = snapshot.body.trim(),
                status = snapshot.status,
                publishAt = null,
                audiences = audiencesPayload(snapshot),
                mediaIds = finalMediaIds,
                media = finalMediaIds
            )

            when (val result = newsRepository.updateNews(
                newsId,
                request
            )) {
                is NetworkResult.Success -> {
                    NewsRefreshBus.refresh()
                    _state.update {
                        it.copy(
                            saving = false,
                            savedSuccessfully = true
                        )
                    }
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

    private suspend fun uploadPendingFiles(newsId: Int): List<Int>? {
        val files = _state.value.pendingFiles
        if (files.isEmpty()) return emptyList()

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
                    _state.update { it.copy(error = "آپلود «${file.name}» ناموفق بود: ${result.message}") }
                    return null
                }

                NetworkResult.Loading    -> Unit
            }
        }
        return uploadedIds
    }

    private fun audiencesPayload(snapshot: UiState): List<AudienceItemRequest> {
        if (snapshot.globalAudience) {
            return listOf(
                AudienceItemRequest(
                    audienceType = "global",
                    targetId = null,
                    role = null
                )
            )
        }
        val list = mutableListOf<AudienceItemRequest>()
        snapshot.selectedRoles.forEach { role ->
            list.add(
                AudienceItemRequest(
                    audienceType = "role",
                    targetId = null,
                    role = role
                )
            )
        }
        snapshot.selectedAgeGroupIds.forEach { id ->
            list.add(
                AudienceItemRequest(
                    audienceType = "age_group",
                    targetId = id,
                    role = null
                )
            )
        }
        snapshot.selectedClassIds.forEach { id ->
            list.add(
                AudienceItemRequest(
                    audienceType = "class",
                    targetId = id,
                    role = null
                )
            )
        }
        // اگر هیچکدام انتخاب نشده بود، برای جلوگیری از خطای سرور، global برمی‌گردانیم
        if (list.isEmpty()) {
            return listOf(
                AudienceItemRequest(
                    audienceType = "global",
                    targetId = null,
                    role = null
                )
            )
        }
        return list
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
        const val MAX_MEDIA_PER_NEWS = 10
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
