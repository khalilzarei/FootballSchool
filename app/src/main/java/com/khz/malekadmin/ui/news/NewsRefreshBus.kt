package com.khz.malekadmin.ui.news

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * باس ساده برای اطلاع‌رسانی به لیست اخبار که باید رفرش شود.
 * هر جا خبر ایجاد، ویرایش، منتشر، بایگانی یا حذف شد، refresh() صدا زده می‌شود
 * و NewsListScreen آن را مشاهده کرده و لیست را دوباره لود می‌کند.
 */
object NewsRefreshBus {
    private val _trigger = MutableStateFlow(0L)
    val trigger: StateFlow<Long> = _trigger.asStateFlow()

    fun refresh() {
        _trigger.value = System.currentTimeMillis()
    }
}
