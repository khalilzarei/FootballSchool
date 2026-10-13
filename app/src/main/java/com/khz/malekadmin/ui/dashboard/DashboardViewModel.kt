package com.khz.malekadmin.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.khz.malekadmin.core.local.SessionManager
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val reportRepository: ReportRepository,
    private val sessionManager: SessionManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState
    private val gson = Gson()
    private var lastRefreshAt = 0L

    // بارگذاری از داخل Compose انجام می‌شود؛ حالت اولیه Loading است
    // silent = بروزرسانی پس‌زمینه (وقتی صفحه بازگشت) — بدون سوسو زدن و بدون
    // جابه‌جایی داده‌های سالم با خطای موقت شبکه
    fun loadDashboardData(silent: Boolean = false) {
        // debounce برای جلوگیری از فراخوانی دوبله init + LaunchedEffect
        if (!silent) {
            val now = System.currentTimeMillis()
            if (now - lastRefreshAt < 800) return
            lastRefreshAt = now
        }
        viewModelScope.launch {
            if (!silent) _uiState.value = DashboardUiState.Loading
            when (val result = reportRepository.getDashboardReport()) {
                is NetworkResult.Success -> {
                    _uiState.value = DashboardUiState.Success(result.data)
                    // ذخیره کش آفلاین
                    try {
                        sessionManager?.saveDashboardCache(gson.toJson(result.data))
                    } catch (_: Exception) {
                    }
                }

                is NetworkResult.Error   -> {
                    val mustChange = result.message.contains("423") || result.message.contains("رمز عبور")
                    if (mustChange) {
                        _uiState.value = DashboardUiState.RequirePasswordChange
                    } else {
                        // سعی کن کش آفلاین را نمایش بده اگر داده‌ی سالم نداریم
                        val cached = try {
                            sessionManager?.getDashboardCache()
                        } catch (_: Exception) {
                            null
                        }
                        if (!cached.isNullOrBlank() && _uiState.value !is DashboardUiState.Success) {
                            try {
                                val data = gson.fromJson(
                                    cached,
                                    com.khz.malekadmin.domain.model.DashboardReport::class.java
                                )
                                if (data != null) {
                                    _uiState.value = DashboardUiState.Success(data)
                                    return@launch
                                }
                            } catch (_: Exception) {
                            }
                        }
                        if (!silent || _uiState.value !is DashboardUiState.Success) {
                            _uiState.value = DashboardUiState.Error(result.message)
                        }
                    }
                }

                else                     -> {}
            }
        }
    }

    /** برای pull-to-refresh بدون debounce */
    fun refresh() {
        lastRefreshAt = 0L
        loadDashboardData(silent = false)
    }
}