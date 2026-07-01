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
    val errorMessageRes: Int? = null,
    val successMessage: String? = null,
    val successMessageRes: Int? = null
)

enum class AdminCompaniesFilter(val labelRes: Int) {
    ALL(com.example.nextstep.R.string.company_filter_all),
    ACTIVE(com.example.nextstep.R.string.company_filter_active),
    INACTIVE(com.example.nextstep.R.string.company_filter_inactive),
    ARCHIVED(com.example.nextstep.R.string.company_filter_archived)
}