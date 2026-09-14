package com.khz.footballschool.ui.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.repository.AgeGroupRepository
import com.khz.footballschool.data.repository.ClassRepository
import com.khz.footballschool.data.repository.CoachRepository
import com.khz.footballschool.domain.model.AgeGroup
import com.khz.footballschool.domain.model.ClassSchedule
import com.khz.footballschool.domain.model.Coach
import com.khz.footballschool.domain.model.FootballClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClassFormViewModel(
    private val repo: ClassRepository,
    private val ageGroupRepo: AgeGroupRepository,
    private val coachRepo: CoachRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _initialLoading = MutableStateFlow(false)
    val initialLoading: StateFlow<Boolean> = _initialLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _class = MutableStateFlow<FootballClass?>(null)
    val cls: StateFlow<FootballClass?> = _class

    // ─── داده‌های مرجع برای دراپ‌داون‌ها ───
    private val _ageGroups = MutableStateFlow<List<AgeGroup>>(emptyList())
    val ageGroups: StateFlow<List<AgeGroup>> = _ageGroups

    private val _coaches = MutableStateFlow<List<Coach>>(emptyList())
    val coaches: StateFlow<List<Coach>> = _coaches

    // برنامه هفتگی فعلی کلاس (در ویرایش) — برای نمایش بخش برنامه فقط اگر خالی باشد
    private val _schedules = MutableStateFlow<List<ClassSchedule>>(emptyList())
    val schedules: StateFlow<List<ClassSchedule>> = _schedules

    private val _refsLoading = MutableStateFlow(false)
    val refsLoading: StateFlow<Boolean> = _refsLoading

    private val _refsError = MutableStateFlow<String?>(null)
    val refsError: StateFlow<String?> = _refsError

    init {
        loadReferences()
    }

    /**
     * بارگذاری گروه‌های سنی و مربیان برای فرم
     */
    fun loadReferences() {
        _refsLoading.value = true
        _refsError.value = null
        viewModelScope.launch {
            var hasError = false

            when (val r = ageGroupRepo.getAgeGroups(perPage = 100)) {
                is NetworkResult.Success -> _ageGroups.value = r.data
                is NetworkResult.Error -> {
                    _refsError.value = "خطا در دریافت گروه‌های سنی: ${r.message}"
                    hasError = true
                }
                else -> {}
            }

            when (val r = coachRepo.getCoaches(perPage = 100)) {
                is NetworkResult.Success -> _coaches.value = r.data
                is NetworkResult.Error -> {
                    _refsError.value = "خطا در دریافت مربیان: ${r.message}"
                    hasError = true
                }
                else -> {}
            }

            if (!hasError) _refsError.value = null
            _refsLoading.value = false
        }
    }

    fun load(id: Int) {
        _initialLoading.value = true
        viewModelScope.launch {
            when (val r = repo.getClass(id)) {
                is NetworkResult.Success -> _class.value = r.data
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }

            // برنامه هفتگی فعلی (برای حالت ویرایش)
            when (val r = repo.getSchedules(id)) {
                is NetworkResult.Success -> _schedules.value = r.data
                else -> {}
            }

            _initialLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun create(
        title: String,
        ageGroupId: Int?,
        coachId: Int?,
        assistantCoachId: Int?,
        capacity: Int?,
        location: String?,
        description: String?,
        pricingType: String?,
        monthlyFee: Long?,
        sessionFee: Long?,
        registrationFee: Long?,
        startDate: String?,
        endDate: String?,
        onSuccess: (classId: Int) -> Unit
    ) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            when (val r = repo.createClass(
                title = title,
                ageGroupId = ageGroupId,
                coachId = coachId,
                assistantCoachId = assistantCoachId,
                capacity = capacity,
                location = location,
                description = description,
                pricingType = pricingType,
                monthlyFee = monthlyFee,
                sessionFee = sessionFee,
                registrationFee = registrationFee,
                startDate = startDate,
                endDate = endDate
            )) {
                is NetworkResult.Success -> onSuccess(r.data.id)
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _loading.value = false
        }
    }

    fun update(
        id: Int,
        title: String,
        ageGroupId: Int?,
        coachId: Int?,
        assistantCoachId: Int?,
        capacity: Int?,
        location: String?,
        description: String?,
        pricingType: String?,
        monthlyFee: Long?,
        sessionFee: Long?,
        registrationFee: Long?,
        startDate: String?,
        endDate: String?,
        status: String,
        onSuccess: () -> Unit
    ) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            when (val r = repo.updateClass(
                id = id,
                title = title,
                ageGroupId = ageGroupId,
                coachId = coachId,
                assistantCoachId = assistantCoachId,
                capacity = capacity,
                location = location,
                description = description,
                pricingType = pricingType,
                monthlyFee = monthlyFee,
                sessionFee = sessionFee,
                registrationFee = registrationFee,
                startDate = startDate,
                endDate = endDate,
                status = status
            )) {
                is NetworkResult.Success -> onSuccess()
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _loading.value = false
        }
    }
}
