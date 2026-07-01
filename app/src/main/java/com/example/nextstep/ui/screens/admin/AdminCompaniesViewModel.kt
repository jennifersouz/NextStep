package com.example.nextstep.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.nextstep.R
import com.example.nextstep.data.model.AdminCompanyDto
import com.example.nextstep.data.model.AdminCompanyEditRequest
import com.example.nextstep.data.model.AdminCompanyUpdateDto
import com.example.nextstep.data.model.CreateCompanyDto
import com.example.nextstep.data.repository.AdminCompaniesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

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
                val refreshedSelected = _uiState.value.selectedCompany?.let { current ->
                    companies.find { it.id == current.id }
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    companies = companies,
                    filteredCompanies = applyFilter(companies),
                    selectedCompany = refreshedSelected ?: _uiState.value.selectedCompany
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_failed_to_load_companies
                )
            }
        }
    }

    fun selectCompany(company: AdminCompanyDto) {
        _uiState.value = _uiState.value.copy(selectedCompany = company)
    }

    fun clearSelectedCompany() {
        _uiState.value = _uiState.value.copy(selectedCompany = null)
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
                    successMessageRes = R.string.company_created_success
                )
                loadCompanies()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_could_not_create_company
                )
            }
        }
    }

    fun updateCompany(companyId: String, data: AdminCompanyUpdateDto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.updateCompany(companyId, data)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(successMessageRes = R.string.company_updated_success)
                loadCompanies()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_could_not_update_company
                )
            }
        }
    }

    /**
     * Editar empresa: atualiza companies + profiles, confirma resultado.
     * Retorna a empresa atualizada para o chamador via callback.
     */
    fun editCompany(
        companyId: String,
        companyProfileId: String,
        name: String,
        nif: String?,
        businessArea: String?,
        location: String?,
        phone: String?,
        description: String?,
        isActive: Boolean,
        onSuccess: (AdminCompanyDto) -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val request = AdminCompanyEditRequest(
                companyName = name,
                nif = nif,
                businessArea = businessArea,
                location = location,
                phone = phone,
                description = description,
                isActive = isActive,
                updatedAt = Instant.now().toString()
            )

            val result = repository.editCompany(companyId, companyProfileId, request)

            if (result.isSuccess) {
                val updated = result.getOrThrow()
                // Atualizar selectedCompany para que o detalhe reflita imediatamente
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessageRes = R.string.company_updated_success,
                    selectedCompany = updated
                )
                loadCompanies()
                onSuccess(updated)
            } else {
                Log.e("AdminCompaniesVM", "editCompany failed id=$companyId", result.exceptionOrNull())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_could_not_save_company_changes
                )
                onError()
            }
        }
    }

    fun setCompanyActive(companyId: String, isActive: Boolean) {
        viewModelScope.launch {
            val result = repository.deactivateCompany(companyId, "")

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessageRes = if (isActive) R.string.company_activated_success else R.string.company_deactivated_success
                )
                loadCompanies()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessageRes = R.string.error_could_not_change_company_status
                )
            }
        }
    }

    fun deleteCompany(companyId: String) {
        viewModelScope.launch {
            val result = repository.deactivateCompany(companyId, "")

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    successMessageRes = R.string.company_deactivated_success,
                    selectedCompany = null
                )
                loadCompanies()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessageRes = R.string.error_could_not_remove_company
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
            AdminCompaniesFilter.ACTIVE -> companies.filter { it.isActive == true && !it.isArchived }
            AdminCompaniesFilter.INACTIVE -> companies.filter { it.isActive == false && !it.isArchived }
            AdminCompaniesFilter.ARCHIVED -> companies.filter { it.isArchived }
        }
    }
}