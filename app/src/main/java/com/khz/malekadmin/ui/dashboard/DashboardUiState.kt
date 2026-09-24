package com.khz.malekadmin.ui.dashboard

import com.khz.malekadmin.domain.model.DashboardReport

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val data: DashboardReport) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
    object RequirePasswordChange : DashboardUiState()
}