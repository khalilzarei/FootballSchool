package com.khz.footballschool.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.repository.UserRepository
import com.khz.footballschool.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserDetailViewModel(private val repo: UserRepository) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _resetPasswordResult = MutableStateFlow<String?>(null)
    val resetPasswordResult: StateFlow<String?> = _resetPasswordResult

    fun load(userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            when (val r = repo.getUser(userId)) {
                is NetworkResult.Success -> _user.value = r.data
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
            _loading.value = false
        }
    }

    fun toggleStatus() {
        val u = _user.value ?: return
        viewModelScope.launch {
            val activate = u.status != "active"
            when (val r = repo.toggleStatus(u.id, activate)) {
                is NetworkResult.Success -> load(u.id)
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
        }
    }

    fun resetPassword(newPassword: String? = null) {
        val u = _user.value ?: return
        viewModelScope.launch {
            when (val r = repo.resetPassword(u.id, newPassword)) {
                is NetworkResult.Success -> {
                    _resetPasswordResult.value = r.data ?: "رمز جدید با موفقیت ساخته شد"
                    load(u.id)
                }
                is NetworkResult.Error -> _error.value = r.message
                else -> {}
            }
        }
    }

    fun clearResetPasswordResult() {
        _resetPasswordResult.value = null
    }
}