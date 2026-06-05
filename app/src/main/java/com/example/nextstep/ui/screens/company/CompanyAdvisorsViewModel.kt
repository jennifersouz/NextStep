package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyAdvisorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyAdvisorsViewModel : ViewModel() {

    private val repository = CompanyAdvisorsRepository()

    private val _uiState = MutableStateFlow(CompanyAdvisorsUiState())
    val uiState: StateFlow<CompanyAdvisorsUiState> = _uiState.asStateFlow()

    init {
        loadAdvisors()
    }

    fun loadAdvisors() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getAdvisors()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    advisors = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.advisors_load_error
                )
            }
        }
    }

    fun onSearchChange(value: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = value
        )
    }

    fun toggleSort() {
        _uiState.value = _uiState.value.copy(
            sortAscending = !_uiState.value.sortAscending
        )
    }
}