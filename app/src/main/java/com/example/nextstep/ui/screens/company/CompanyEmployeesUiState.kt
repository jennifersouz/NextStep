package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.R
import com.example.nextstep.data.model.CompanyEmployeeInviteDisplayDto

enum class CompanyEmployeeFilter(val labelRes: Int) {
    ALL(R.string.company_employee_filter_all),
    ACTIVE(R.string.company_employee_filter_active),
    PENDING(R.string.company_employee_filter_pending),
    INACTIVE(R.string.company_employee_filter_inactive)
}

data class CompanyEmployeesUiState(
    val employees: List<CompanyEmployeeInviteDisplayDto> = emptyList(),
    val searchQuery: String = "",
    val selectedEmployeeFilter: CompanyEmployeeFilter = CompanyEmployeeFilter.ALL,
    val sortAscending: Boolean = true,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
) {
    val filteredEmployees: List<CompanyEmployeeInviteDisplayDto>
        get() {
            var filtered = employees

            filtered = when (selectedEmployeeFilter) {
                CompanyEmployeeFilter.ALL -> filtered
                CompanyEmployeeFilter.ACTIVE -> filtered.filter {
                    it.status.trim().lowercase() == "active"
                }
                CompanyEmployeeFilter.PENDING -> filtered.filter {
                    it.status.trim().lowercase() == "pending"
                }
                CompanyEmployeeFilter.INACTIVE -> filtered.filter {
                    it.status.trim().lowercase() == "inactive"
                }
            }

            if (searchQuery.isNotBlank()) {
                val query = searchQuery.trim().lowercase()
                filtered = filtered.filter { employee ->
                    employee.displayName.lowercase().contains(query) ||
                            employee.email.lowercase().contains(query) ||
                            employee.department.orEmpty().lowercase().contains(query)
                }
            }

            return if (sortAscending) {
                filtered.sortedBy { it.displayName.lowercase() }
            } else {
                filtered.sortedByDescending { it.displayName.lowercase() }
            }
        }
}
