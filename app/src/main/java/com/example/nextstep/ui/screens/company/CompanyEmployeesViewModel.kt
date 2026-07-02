package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyAdvisorDto
import com.example.nextstep.data.model.CompanyEmployeeInviteDisplayDto
import com.example.nextstep.data.repository.CompanyAdvisorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyEmployeesViewModel : ViewModel() {

    private val repository = CompanyAdvisorsRepository()

    private val _uiState = MutableStateFlow(CompanyEmployeesUiState())
    val uiState: StateFlow<CompanyEmployeesUiState> = _uiState.asStateFlow()

    fun loadEmployees() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageRes = null)

            val result = repository.getAdvisors()

            if (result.isSuccess) {
                val advisors = result.getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(
                    employees = advisors.map { it.toDisplayDto() },
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

    fun onFilterChange(filter: CompanyEmployeeFilter) {
        _uiState.value = _uiState.value.copy(selectedEmployeeFilter = filter)
    }

    fun toggleSort() {
        _uiState.value = _uiState.value.copy(
            sortAscending = !_uiState.value.sortAscending
        )
    }

    fun deleteEmployee(employeeId: String) {
        viewModelScope.launch {
            val result = repository.deleteAdvisor(employeeId)
            if (result.isSuccess) {
                loadEmployees()
            }
        }
    }
}

private fun CompanyAdvisorDto.toDisplayDto(): CompanyEmployeeInviteDisplayDto {
    val isEmailOnly = name == email || name.isBlank()
    return CompanyEmployeeInviteDisplayDto(
        id = id,
        profileId = profileId.ifBlank { null },
        email = email,
        firstName = if (isEmailOnly) null else name,
        lastName = null,
        phone = phone.ifBlank { null },
        department = department.ifBlank { null },
        status = status
    )
}
