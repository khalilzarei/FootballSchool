package com.khz.footballschool.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.repository.UserRepository
import com.khz.footballschool.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class UserListViewModel(private val repository: UserRepository) : ViewModel() {

    private val _state = MutableStateFlow<UserListState>(UserListState.Loading)
    val state: StateFlow<UserListState> = _state

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _roleFilter = MutableStateFlow<String?>(null)
    val roleFilter: StateFlow<String?> = _roleFilter


    private var allUsers: List<User> = emptyList()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UserListState.Loading
            when (val r = repository.getUsers(
                perPage = 100,
                role = _roleFilter.value,
                query = _query.value.takeIf { it.isNotBlank() }
            )) {
                is NetworkResult.Success -> {
                    allUsers = r.data.items
                    _state.value = UserListState.Success(allUsers)
                }
                is NetworkResult.Error -> {
                    _state.value = UserListState.Error(r.message)
                }
                else -> {}
            }
        }
    }

    fun search(q: String) {
        _query.value = q
        load()
    }

    fun setRoleFilter(role: String?) {
        _roleFilter.value = role
        load()
    }

    fun toggleStatus(user: User) {
        viewModelScope.launch {
            val activate = user.status != "active"
            when (val r = repository.toggleStatus(user.id, activate)) {
                is NetworkResult.Success -> {
                    // به‌روزرسانی لیست محلی
                    allUsers = allUsers.map { u ->
                        if (u.id == user.id) {
                            u.copy(status = if (activate) "active" else "inactive")
                        } else u
                    }
                    _state.value = UserListState.Success(allUsers)
                }
                is NetworkResult.Error -> {
                    _state.value = UserListState.Error(r.message)
                }
                else -> {}
            }
        }
    }
}

sealed class UserListState {
    object Loading : UserListState()
    data class Success(val users: List<User>) : UserListState()
    data class Error(val message: String) : UserListState()
}