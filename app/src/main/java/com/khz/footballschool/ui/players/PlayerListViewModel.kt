package com.khz.footballschool.ui.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.repository.PlayerRepository
import com.khz.footballschool.domain.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PlayerListState {
    object Loading : PlayerListState()
    data class Success(val players: List<Player>) : PlayerListState()
    data class Error(val message: String) : PlayerListState()
}

class PlayerListViewModel(
    private val repo: PlayerRepository
) : ViewModel() {

    private val _state = MutableStateFlow<PlayerListState>(PlayerListState.Loading)
    val state: StateFlow<PlayerListState> = _state

    private var allPlayers: List<Player> = emptyList()

    init {
        load()
    }

    /**
     * بارگذاری لیست بازیکنان از سرور.
     * silent=true: بدون نمایش اسپینر (برای رفرش هنگام بازگشت به صفحه)
     */
    fun load(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _state.value = PlayerListState.Loading
            when (val r = repo.getPlayers(perPage = 100)) {
                is NetworkResult.Success -> {
                    allPlayers = r.data.items
                    _state.value = PlayerListState.Success(allPlayers)
                }

                is NetworkResult.Error   -> {
                    if (!silent) _state.value = PlayerListState.Error(r.message)
                }

                else                     -> {}
            }
        }
    }

    /**
     * جستجوی محلی روی لیست بارگذاری‌شده
     * (فیلترهای وضعیت و جنسیت در خود صفحه اعمال می‌شوند)
     */
    fun search(query: String) {
        if (allPlayers.isEmpty()) return
        _state.value = PlayerListState.Success(allPlayers)
    }

    /**
     * تغییر وضعیت بازیکن (فعال/غیرفعال)
     */
    fun toggleStatus(player: Player) {
        viewModelScope.launch {
            val activate = player.status != "active"
            when (val r = repo.toggleStatus(
                player.id,
                activate
            )) {
                is NetworkResult.Success -> {
                    allPlayers = allPlayers.map { p ->
                        if (p.id == player.id) {
                            p.copy(status = if (activate) "active" else "inactive")
                        } else p
                    }
                    _state.value = PlayerListState.Success(allPlayers)
                }

                is NetworkResult.Error   -> {
                    _state.value = PlayerListState.Error(r.message)
                }

                else                     -> {}
            }
        }
    }
}