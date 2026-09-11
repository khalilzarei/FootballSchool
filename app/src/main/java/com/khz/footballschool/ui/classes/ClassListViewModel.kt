package com.khz.footballschool.ui.classes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.repository.ClassRepository
import com.khz.footballschool.domain.model.FootballClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ClassListState {
    object Loading : ClassListState()
    data class Success(val classes: List<FootballClass>) : ClassListState()
    data class Error(val message: String) : ClassListState()
}

class ClassListViewModel(
    private val repo: ClassRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ClassListState>(ClassListState.Loading)
    val state: StateFlow<ClassListState> = _state

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter

    private var allClasses: List<FootballClass> = emptyList()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = ClassListState.Loading
            when (val r = repo.getClasses(
                perPage = 100,
                status = _statusFilter.value,
                query = _query.value.takeIf { it.isNotBlank() }
            )) {
                is NetworkResult.Success -> {
                    allClasses = r.data.items
                    _state.value = ClassListState.Success(allClasses)
                }
                is NetworkResult.Error -> {
                    _state.value = ClassListState.Error(r.message)
                }
                else -> {}
            }
        }
    }

    fun search(q: String) {
        _query.value = q
        load()
    }

    fun setStatusFilter(status: String?) {
        _statusFilter.value = status
        load()
    }

    fun toggleStatus(cls: FootballClass) {
        viewModelScope.launch {
            val activate = !cls.isActive
            when (val r = repo.toggleStatus(cls.id, activate)) {
                is NetworkResult.Success -> {
                    allClasses = allClasses.map { c ->
                        if (c.id == cls.id) {
                            c.copy(isActive = activate)
                        } else c
                    }
                    _state.value = ClassListState.Success(allClasses)
                }
                is NetworkResult.Error -> {
                    _state.value = ClassListState.Error(r.message)
                }
                else -> {}
            }
        }
    }

    fun delete(cls: FootballClass) {
        viewModelScope.launch {
            when (val r = repo.deleteClass(cls.id)) {
                is NetworkResult.Success -> {
                    allClasses = allClasses.filter { it.id != cls.id }
                    _state.value = ClassListState.Success(allClasses)
                }
                is NetworkResult.Error -> {
                    _state.value = ClassListState.Error(r.message)
                }
                else -> {}
            }
        }
    }
}