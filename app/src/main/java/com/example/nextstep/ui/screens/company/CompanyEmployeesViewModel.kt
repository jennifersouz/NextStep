package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyEmployeesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyEmployeesViewModel : ViewModel() {

    private val repository = CompanyEmployeesRepository()

    private val _uiState = MutableStateFlow(CompanyEmployeesUiState())
    val uiState: StateFlow<CompanyEmployeesUiState> = _uiState.asStateFlow()

    fun loadEmployees() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageRes = null)

            val result = repository.getEmployees()

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    employees = result.getOrDefault(emptyList()),
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.employees_load_error
                )
            }
        }
    }

    fun onSearchChange(value: String) {
        _uiState.value = _uiState.value.copy(searchQuery = value)
    }

    fun toggleSort() {
        _uiState.value = _uiState.value.copy(
            sortAscending = !_uiState.value.sortAscending
        )
    }

    fun deleteEmployee(employeeId: String) {
        viewModelScope.launch {
            val result = repository.deleteEmployee(employeeId)
            if (result.isSuccess) {
                loadEmployees()
            }
        }
    }
}
