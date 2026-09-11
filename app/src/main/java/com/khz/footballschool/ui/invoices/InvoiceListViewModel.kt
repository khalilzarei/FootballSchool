package com.khz.footballschool.ui.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.repository.InvoiceRepository
import com.khz.footballschool.domain.model.Invoice
import com.khz.footballschool.ui.components.ListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InvoiceListViewModel(private val repo: InvoiceRepository) : ViewModel() {
    private val _state = MutableStateFlow<ListState<Invoice>>(ListState.Loading)
    val state: StateFlow<ListState<Invoice>> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = ListState.Loading
            _state.value = when (val r = repo.getInvoices()) {
                is NetworkResult.Success -> ListState.Success(r.data.items) // ← items
                is NetworkResult.Error   -> ListState.Error(r.message)
                else                     -> ListState.Loading
            }
        }
    }

    fun cancelInvoice(id: Int) {
        viewModelScope.launch { repo.cancelInvoice(id); refresh() }
    }
}