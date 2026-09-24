package com.khz.malekadmin.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    // بارگذاری از داخل Compose انجام می‌شود؛ حالت اولیه Loading است
    // silent = بروزرسانی پس‌زمینه (وقتی صفحه بازگشت) — بدون سوسو زدن و بدون
    // جابه‌جایی داده‌های سالم با خطای موقت شبکه
    fun loadDashboardData(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _uiState.value = DashboardUiState.Loading
            when (val result = reportRepository.getDashboardReport()) {
                is NetworkResult.Success -> {
                    _uiState.value = DashboardUiState.Success(result.data)
                }

                is NetworkResult.Error   -> {
                    val mustChange = result.message.contains("423") || result.message.contains("رمز عبور")
                    if (mustChange) {
                        _uiState.value = DashboardUiState.RequirePasswordChange
                    } else if (!silent || _uiState.value !is DashboardUiState.Success) {
                        // فقط اگر داده قبلی سالم نباشد، خطا نمایش داده شود
                        _uiState.value = DashboardUiState.Error(result.message)
                    }
                }

                else                     -> {}
            }
        }
    }
}