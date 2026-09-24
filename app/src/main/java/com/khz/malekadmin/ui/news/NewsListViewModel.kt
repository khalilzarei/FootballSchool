package com.khz.malekadmin.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.repository.NewsRepository
import com.khz.malekadmin.domain.model.News
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewsListUiState(
    val loading: Boolean = false,
    val items: List<News> = emptyList(),
    val filteredItems: List<News> = emptyList(),
    val query: String = "",
    val statusFilter: String? = null,
    val error: String? = null,
    val actionMessage: String? = null
)

class NewsListViewModel(
    private val repo: NewsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewsListUiState(loading = true))
    val state: StateFlow<NewsListUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loading = true,
                    error = null
                )
            }
            when (val r = repo.getNews(
                page = 1,
                perPage = 100
            )) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            loading = false,
                            items = r.data,
                            filteredItems = filterItems(
                                r.data,
                                it.query,
                                it.statusFilter
                            ),
                            error = null
                        )
                    }
                }

                is NetworkResult.Error   -> _state.update {
                    it.copy(
                        loading = false,
                        error = r.message
                    )
                }

                else                     -> _state.update { it.copy(loading = false) }
            }
        }
    }

    fun onQueryChange(q: String) {
        _state.update { current ->
            current.copy(
                query = q,
                filteredItems = filterItems(
                    current.items,
                    q,
                    current.statusFilter
                )
            )
        }
    }

    fun onStatusFilterChange(status: String?) {
        _state.update { current ->
            current.copy(
                statusFilter = status,
                filteredItems = filterItems(
                    current.items,
                    current.query,
                    status
                )
            )
        }
    }

    private fun filterItems(
        items: List<News>,
        query: String,
        status: String?
    ): List<News> {
        var list = items
        if (!status.isNullOrBlank()) {
            list = list.filter { it.status == status }
        }
        if (query.isNotBlank()) {
            val q = query.trim()
            list = list.filter {
                it.title.contains(
                    q,
                    ignoreCase = true
                ) || it.body.contains(
                    q,
                    ignoreCase = true
                )
            }
        }
        return list
    }

    fun publish(id: Int) {
        viewModelScope.launch {
            when (val r = repo.publishNews(id)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(actionMessage = "خبر منتشر شد") }
                    NewsRefreshBus.refresh()
                    refresh()
                }

                is NetworkResult.Error   -> _state.update { it.copy(error = r.message) }
                else                     -> {}
            }
        }
    }

    fun archive(id: Int) {
        viewModelScope.launch {
            when (val r = repo.archiveNews(id)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(actionMessage = "خبر بایگانی شد") }
                    NewsRefreshBus.refresh()
                    refresh()
                }

                is NetworkResult.Error   -> _state.update { it.copy(error = r.message) }
                else                     -> {}
            }
        }
    }

    fun delete(id: Int) {
        viewModelScope.launch {
            when (val r = repo.deleteNews(id)) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(actionMessage = "خبر حذف شد") }
                    NewsRefreshBus.refresh()
                    refresh()
                }

                is NetworkResult.Error   -> _state.update { it.copy(error = r.message) }
                else                     -> {}
            }
        }
    }

    fun consumeActionMessage() {
        _state.update { it.copy(actionMessage = null) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
