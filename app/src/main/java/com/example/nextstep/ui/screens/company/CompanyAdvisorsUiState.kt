package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyAdvisorDto

data class CompanyAdvisorsUiState(
    val advisors: List<CompanyAdvisorDto> = emptyList(),
    val searchQuery: String = "",
    val sortAscending: Boolean = true,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
) {
    val filteredAdvisors: List<CompanyAdvisorDto>
        get() {
            val filtered = if (searchQuery.isBlank()) {
                advisors
            } else {
                val query = searchQuery.trim()

                advisors.filter { advisor ->
                    advisor.name.contains(query, ignoreCase = true) ||
                            advisor.email.contains(query, ignoreCase = true) ||
                            advisor.department.orEmpty().contains(query, ignoreCase = true)
                }
            }

            return if (sortAscending) {
                filtered.sortedBy { advisor ->
                    advisor.name.lowercase()
                }
            } else {
                filtered.sortedByDescending { advisor ->
                    advisor.name.lowercase()
                }
            }
        }
}