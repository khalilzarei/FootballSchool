package com.khz.footballschool.ui.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.repository.PaymentRepository
import com.khz.footballschool.domain.model.Payment
import com.khz.footballschool.ui.components.ListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaymentListViewModel(private val repo: PaymentRepository) : ViewModel() {

    private val _state = MutableStateFlow<ListState<Payment>>(ListState.Loading)
    val state: StateFlow<ListState<Payment>> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = ListState.Loading
            _state.value = when (val r = repo.getPayments()) {
                is NetworkResult.Success -> ListState.Success(r.data)
                is NetworkResult.Error   -> {
                    android.util.Log.e(
                        "PaymentVM",
                        "Error: ${r.message}"
                    )
                    ListState.Error(r.message)
                }

                else                     -> ListState.Loading
            }
        }
    }

    fun approve(id: Int) {
        viewModelScope.launch { repo.approvePayment(id); refresh() }
    }

    fun reject(id: Int) {
        viewModelScope.launch { repo.rejectPayment(id); refresh() }
    }
}