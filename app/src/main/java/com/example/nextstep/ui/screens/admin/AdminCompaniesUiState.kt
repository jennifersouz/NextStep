package com.example.nextstep.ui.screens.admin

import com.example.nextstep.data.model.AdminCompanyDto

data class AdminCompaniesUiState(
    val isLoading: Boolean = false,
    val companies: List<AdminCompanyDto> = emptyList(),
    val filteredCompanies: List<AdminCompanyDto> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: AdminCompaniesFilter = AdminCompaniesFilter.ALL,
    val selectedCompany: AdminCompanyDto? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

enum class AdminCompaniesFilter(val label: String) {
    ALL("Todas"),
    ACTIVE("Ativas"),
    INACTIVE("Inativas")
}