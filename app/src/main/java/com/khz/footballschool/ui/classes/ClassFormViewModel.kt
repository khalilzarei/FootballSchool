package com.khz.footballschool.ui.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.repository.ClassRepository
import com.khz.footballschool.domain.model.FootballClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClassFormViewModel(
    private val repo: ClassRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _initialLoading = MutableStateFlow(false)
    val initialLoading: StateFlow<Boolean> = _initialLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _class = MutableStateFlow<FootballClass?>(null)
    val cls: StateFlow<FootballClass?> = _class

    fun load(id: Int) {
        _initialLoading.value = true
        viewModelScope.launch {
            when (val r = repo.getClass(id)) {
                is NetworkResult.Success -> _class.value = r.data
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _initialLoading.value = false
        }
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
        onSuccess: () -> Unit
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
                is NetworkResult.Success -> onSuccess()
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