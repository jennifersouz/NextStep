package com.example.nextstep.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.AdminCompanyDto
import com.example.nextstep.data.model.AdminCompanyUpdateDto
import com.example.nextstep.data.model.CreateCompanyDto
import com.example.nextstep.data.repository.AdminCompaniesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminCompaniesViewModel : ViewModel() {

    private val repository = AdminCompaniesRepository()

    private val _uiState = MutableStateFlow(AdminCompaniesUiState(isLoading = true))
    val uiState: StateFlow<AdminCompaniesUiState> = _uiState.asStateFlow()

    init {
        loadCompanies()
    }

    fun loadCompanies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.getCompaniesWithOfferCounts()

            if (result.isSuccess) {
                val companies = result.getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    companies = companies,
                    filteredCompanies = applyFilter(companies)
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFiltersAndSearch()
    }

    fun onFilterChange(filter: AdminCompaniesFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        applyFiltersAndSearch()
    }

    fun createCompany(data: CreateCompanyDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.createCompany(data)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Empresa criada com sucesso."
                )
                loadCompanies()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun updateCompany(companyId: String, data: AdminCompanyUpdateDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = repository.updateCompany(companyId, data)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Empresa atualizada com sucesso."
                )
                loadCompanies()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun setCompanyActive(companyId: String, isActive: Boolean) {
        viewModelScope.launch {
            val result = repository.setCompanyActive(companyId, isActive)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessage = if (isActive) "Empresa ativada com sucesso." else "Empresa desativada com sucesso."
                )
                loadCompanies()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun deleteCompany(companyId: String) {
        viewModelScope.launch {
            val result = repository.deleteCompany(companyId)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessage = "Empresa removida com sucesso."
                )
                loadCompanies()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun applyFiltersAndSearch() {
        val state = _uiState.value
        val filtered = applyFilter(state.companies)

        _uiState.value = _uiState.value.copy(
            filteredCompanies = if (state.searchQuery.isBlank()) {
                filtered
            } else {
                filtered.filter { company ->
                    company.companyName?.contains(state.searchQuery, ignoreCase = true) == true ||
                            company.businessArea?.contains(state.searchQuery, ignoreCase = true) == true ||
                            company.location?.contains(state.searchQuery, ignoreCase = true) == true
                }
            }
        )
    }

    private fun applyFilter(companies: List<AdminCompanyDto>): List<AdminCompanyDto> {
        return when (_uiState.value.selectedFilter) {
            AdminCompaniesFilter.ALL -> companies
            AdminCompaniesFilter.ACTIVE -> companies.filter { it.isActive == true }
            AdminCompaniesFilter.INACTIVE -> companies.filter { it.isActive == false }
        }
    }
}