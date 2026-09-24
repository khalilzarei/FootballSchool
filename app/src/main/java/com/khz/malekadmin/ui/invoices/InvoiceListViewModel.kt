package com.khz.malekadmin.ui.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.repository.InvoiceRepository
import com.khz.malekadmin.domain.model.Invoice
import com.khz.malekadmin.ui.components.ListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class InvoiceStatusFilter(
    val label: String,
    val apiStatus: String?
) {
    ALL(
        "همه",
        null
    ),
    OPEN(
        "بدهکار",
        null
    ), // open + partial + overdue را شامل می‌شود - فیلتر محلی
    PAID(
        "تسویه شده",
        "paid"
    ),
    PARTIAL(
        "نیمه پرداخت",
        "partial"
    ),
    OVERDUE(
        "سررسید گذشته",
        "overdue"
    ),
    CANCELLED(
        "لغو شده",
        "cancelled"
    )
}

class InvoiceListViewModel(private val repo: InvoiceRepository) : ViewModel() {
    private val _state = MutableStateFlow<ListState<Invoice>>(ListState.Loading)
    val state: StateFlow<ListState<Invoice>> = _state

    private val _filter = MutableStateFlow(InvoiceStatusFilter.ALL)
    val filter: StateFlow<InvoiceStatusFilter> = _filter

    private var allInvoices: List<Invoice> = emptyList()

    init {
        refresh()
    }

    fun setFilter(f: InvoiceStatusFilter) {
        _filter.value = f
        applyFilter()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = ListState.Loading
            _state.value = when (val r = repo.getInvoices(perPage = 100)) {
                is NetworkResult.Success -> {
                    allInvoices = r.data.items
                    val filtered = filterInvoices(
                        allInvoices,
                        _filter.value
                    )
                    ListState.Success(filtered)
                }

                is NetworkResult.Error   -> ListState.Error(r.message)
                else                     -> ListState.Loading
            }
        }
    }

    private fun applyFilter() {
        _state.value = ListState.Success(
            filterInvoices(
                allInvoices,
                _filter.value
            )
        )
    }

    private fun filterInvoices(
        list: List<Invoice>,
        f: InvoiceStatusFilter
    ): List<Invoice> {
        return when (f) {
            InvoiceStatusFilter.ALL       -> list
            InvoiceStatusFilter.OPEN      -> list.filter { it.status == "open" || it.status == "partial" || it.status == "overdue" || it.remainingAmount > 0 }
            InvoiceStatusFilter.PAID      -> list.filter { it.status == "paid" || (it.remainingAmount == 0L && it.totalAmount > 0) }
            InvoiceStatusFilter.PARTIAL   -> list.filter { it.status == "partial" }
            InvoiceStatusFilter.OVERDUE   -> list.filter { it.status == "overdue" }
            InvoiceStatusFilter.CANCELLED -> list.filter { it.status == "cancelled" }
        }
    }

    fun cancelInvoice(id: Int) {
        viewModelScope.launch { repo.cancelInvoice(id); refresh() }
    }
}
