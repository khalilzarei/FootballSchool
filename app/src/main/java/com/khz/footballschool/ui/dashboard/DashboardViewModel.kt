package com.khz.footballschool.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            when (val result = reportRepository.getDashboardReport()) {
                is NetworkResult.Success -> {
                    _uiState.value = DashboardUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = if (
                        result.message.contains("423") ||
                        result.message.contains("رمز عبور")
                    ) {
                        DashboardUiState.RequirePasswordChange
                    } else {
                        DashboardUiState.Error(result.message)
                    }
                }
                else -> {}
            }
        }
    }
}