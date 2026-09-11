package com.khz.footballschool.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khz.footballschool.core.network.NetworkResult
import com.khz.footballschool.data.dto.request.SettingItemRequest
import com.khz.footballschool.data.repository.SettingRepository
import com.khz.footballschool.domain.model.Setting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: SettingRepository) : ViewModel() {

    private val _settings = MutableStateFlow<List<Setting>>(emptyList())
    val settings: StateFlow<List<Setting>> = _settings

    private val _edits = MutableStateFlow<Map<String, String>>(emptyMap())
    val edits: StateFlow<Map<String, String>> = _edits

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    init { load() }

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            (repo.getSettings() as? NetworkResult.Success)?.let { _settings.value = it.data }
            _loading.value = false
        }
    }

    fun edit(key: String, value: String) {
        _edits.value = _edits.value + (key to value)
    }

    fun save() {
        viewModelScope.launch {
            val items = _settings.value.map { s ->
                SettingItemRequest(
                    key = s.key,
                    value = _edits.value[s.key] ?: s.value,
                    valueType = s.valueType,
                    description = s.description
                )
            }
            val r = repo.updateSettings(items)
            if (r is NetworkResult.Success) {
                _settings.value = r.data
                _edits.value = emptyMap()
                _saved.value = true
            }
        }
    }
}