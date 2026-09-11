package com.khz.footballschool.ui.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.repository.PlayerRepository
import com.khz.footballschool.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerDetailViewModel(private val repo: PlayerRepository) : ViewModel() {

    private val _player = MutableStateFlow<Player?>(null)
    val player: StateFlow<Player?> = _player

    private val _guardians = MutableStateFlow<List<GuardianPlayer>>(emptyList())
    val guardians: StateFlow<List<GuardianPlayer>> = _guardians

    private val _balance = MutableStateFlow<PlayerBalance?>(null)
    val balance: StateFlow<PlayerBalance?> = _balance

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun load(playerId: Int) {
        viewModelScope.launch {
            _loading.value = true
            launch { (repo.getPlayer(playerId) as? NetworkResult.Success)?.let { _player.value = it.data } }
            launch { (repo.getPlayerGuardians(playerId) as? NetworkResult.Success)?.let { _guardians.value = it.data } }
            launch { (repo.getPlayerBalance(playerId) as? NetworkResult.Success)?.let { _balance.value = it.data } }
            launch { (repo.getPlayerInvoices(playerId) as? NetworkResult.Success)?.let { _invoices.value = it.data } }
            _loading.value = false
        }
    }
}