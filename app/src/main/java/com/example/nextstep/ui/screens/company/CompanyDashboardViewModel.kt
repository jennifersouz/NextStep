package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyDashboardViewModel : ViewModel() {

    private val companyRepository = CompanyRepository()

    private val _uiState = MutableStateFlow(CompanyDashboardUiState())
    val uiState: StateFlow<CompanyDashboardUiState> = _uiState.asStateFlow()

    init {
        loadInternships()
    }

    fun onStatusSelected(status: InternshipStatusFilter) {
        _uiState.value = _uiState.value.copy(
            selectedStatus = status
        )
    }

    fun loadInternships() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = companyRepository.getCompanyInternships()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    internships = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    internships = emptyList(),
                    isLoading = false,
                    errorMessageRes = R.string.company_internships_load_error
                )
            }
        }
    }
}