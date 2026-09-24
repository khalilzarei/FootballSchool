package com.khz.malekadmin.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.repository.ReportRepository
import com.khz.malekadmin.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportsViewModel(private val repo: ReportRepository) : ViewModel() {

    private val _finance = MutableStateFlow<FinanceReport?>(null)
    val finance: StateFlow<FinanceReport?> = _finance

    private val _debts = MutableStateFlow<List<DebtsReport>>(emptyList())
    val debts: StateFlow<List<DebtsReport>> = _debts

    private val _attendance = MutableStateFlow<List<AttendanceReport>>(emptyList())
    val attendance: StateFlow<List<AttendanceReport>> = _attendance

    private val _classes = MutableStateFlow<List<ClassesReport>>(emptyList())
    val classes: StateFlow<List<ClassesReport>> = _classes

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    init { load() }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            launch { (repo.getFinanceReport() as? NetworkResult.Success)?.let { _finance.value = it.data } }
            launch { (repo.getDebtsReport() as? NetworkResult.Success)?.let { _debts.value = it.data } }
            launch { (repo.getAttendanceReport() as? NetworkResult.Success)?.let { _attendance.value = it.data } }
            launch { (repo.getClassesReport() as? NetworkResult.Success)?.let { _classes.value = it.data } }
            _loading.value = false
        }
    }
}