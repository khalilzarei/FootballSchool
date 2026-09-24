package com.khz.malekadmin.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class SimpleListViewModel<T>(
    private val loader: suspend () -> NetworkResult<List<T>>
) : ViewModel() {

    private val _state = MutableStateFlow<ListState<T>>(ListState.Loading)
    val state: StateFlow<ListState<T>> = _state

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.value = ListState.Loading
            _state.value = when (val r = loader()) {
                is NetworkResult.Success -> ListState.Success(r.data)
                is NetworkResult.Error -> ListState.Error(r.message)
                else -> ListState.Loading
            }
        }
    }
}