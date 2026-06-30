package com.example.nextstep.ui.screens.company

import com.example.nextstep.data.model.CompanyEmployeeInviteDisplayDto

data class CompanyEmployeesUiState(
    val employees: List<CompanyEmployeeInviteDisplayDto> = emptyList(),
    val searchQuery: String = "",
    val sortAscending: Boolean = true,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val filteredEmployees: List<CompanyEmployeeInviteDisplayDto>
        get() {
            val filtered = if (searchQuery.isBlank()) {
                employees
            } else {
                val query = searchQuery.trim().lowercase()
                employees.filter { employee ->
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
