package com.khz.malekadmin.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.repository.NewsRepository
import com.khz.malekadmin.domain.model.News
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NewsDetailState {
    object Loading : NewsDetailState()
    data class Success(val news: News) : NewsDetailState()
    data class Error(val message: String) : NewsDetailState()
}

class NewsDetailViewModel(
    private val repo: NewsRepository
) : ViewModel() {

    private val _state = MutableStateFlow<NewsDetailState>(NewsDetailState.Loading)
    val state: StateFlow<NewsDetailState> = _state.asStateFlow()

    private var currentId: Int? = null

    fun load(id: Int) {
        currentId = id
        _state.value = NewsDetailState.Loading
        viewModelScope.launch {
            when (val r = repo.getNews(id)) {
                is NetworkResult.Success -> _state.value = NewsDetailState.Success(r.data)
                is NetworkResult.Error   -> _state.value = NewsDetailState.Error(r.message)
                else                     -> {}
            }
        }
    }

    fun refresh() {
        currentId?.let { load(it) }
    }

    fun publish(onDone: (Boolean, String?) -> Unit) {
        val id = currentId
                ?: return
        viewModelScope.launch {
            when (val r = repo.publishNews(id)) {
                is NetworkResult.Success -> {
                    NewsRefreshBus.refresh()
                    load(id)
                    onDone(
                        true,
                        null
                    )
                }

                is NetworkResult.Error   -> onDone(
                    false,
                    r.message
                )

                else                     -> {}
            }
        }
    }

    fun archive(onDone: (Boolean, String?) -> Unit) {
        val id = currentId
                ?: return
        viewModelScope.launch {
            when (val r = repo.archiveNews(id)) {
                is NetworkResult.Success -> {
                    NewsRefreshBus.refresh()
                    load(id)
                    onDone(
                        true,
                        null
                    )
                }

                is NetworkResult.Error   -> onDone(
                    false,
                    r.message
                )

                else                     -> {}
            }
        }
    }

    fun delete(onDone: (Boolean, String?) -> Unit) {
        val id = currentId
                ?: return
        viewModelScope.launch {
            when (val r = repo.deleteNews(id)) {
                is NetworkResult.Success -> {
                    NewsRefreshBus.refresh()
                    onDone(
                        true,
                        null
                    )
                }

                is NetworkResult.Error   -> onDone(
                    false,
                    r.message
                )

                else                     -> {}
            }
        }
    }
}
