package com.khz.footballschool.ui.components

sealed class ListState<out T> {
    object Loading : ListState<Nothing>()
    data class Success<T>(val items: List<T>) : ListState<T>()
    data class Error(val message: String) : ListState<Nothing>()
}