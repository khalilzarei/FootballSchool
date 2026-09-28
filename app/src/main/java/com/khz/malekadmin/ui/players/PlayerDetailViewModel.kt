package com.khz.malekadmin.ui.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.malekadmin.core.network.NetworkResult
import com.khz.malekadmin.data.repository.PlayerRepository
import com.khz.malekadmin.data.repository.UserRepository
import com.khz.malekadmin.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerDetailViewModel(
    private val repo: PlayerRepository,
    private val userRepository: UserRepository
) : ViewModel() {

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

    private val _resetPasswordResult = MutableStateFlow<String?>(null)
    val resetPasswordResult: StateFlow<String?> = _resetPasswordResult

    private val _resetPasswordError = MutableStateFlow<String?>(null)
    val resetPasswordError: StateFlow<String?> = _resetPasswordError

    private var currentPlayerId: Int = 0

    fun load(playerId: Int) {
        currentPlayerId = playerId
        viewModelScope.launch {
            _loading.value = true
            launch { (repo.getPlayer(playerId) as? NetworkResult.Success)?.let { _player.value = it.data } }
            launch { (repo.getPlayerGuardians(playerId) as? NetworkResult.Success)?.let { _guardians.value = it.data } }
            launch { (repo.getPlayerBalance(playerId) as? NetworkResult.Success)?.let { _balance.value = it.data } }
            launch { (repo.getPlayerInvoices(playerId) as? NetworkResult.Success)?.let { _invoices.value = it.data } }
            _loading.value = false
        }
    }

    fun refresh() {
        if (currentPlayerId != 0) load(currentPlayerId)
    }

    fun resetPassword(userId: Int) {
        viewModelScope.launch {
            _resetPasswordError.value = null
            when (val result = userRepository.resetPassword(userId)) {
                is NetworkResult.Success -> {
                    _resetPasswordResult.value = result.data
                            ?: "رمز جدید در دسترس نیست"
                }

                is NetworkResult.Error   -> _resetPasswordError.value = result.message
                else                     -> Unit
            }
        }
    }

    fun clearResetPasswordResult() {
        _resetPasswordResult.value = null
    }

    fun clearResetPasswordError() {
        _resetPasswordError.value = null
    }
}
