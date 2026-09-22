package com.khz.footballschool.ui.matches

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MatchRefreshBus {
    private val _trigger = MutableStateFlow(0L)
    val trigger: StateFlow<Long> = _trigger.asStateFlow()
    fun refresh() {
        _trigger.value = System.currentTimeMillis()
    }
}
