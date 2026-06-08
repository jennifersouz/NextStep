package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyActiveAdvisorDto

data class AssignAdvisorUiState(
    val advisors: List<CompanyActiveAdvisorDto> = emptyList(),
    val searchQuery: String = "",
    val selectedAdvisorProfileId: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    @StringRes val errorMessageRes: Int? = null
) {
    val filteredAdvisors: List<CompanyActiveAdvisorDto>
        get() = if (searchQuery.isBlank()) {
            advisors
        } else {
            advisors.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        (it.email?.contains(searchQuery, ignoreCase = true) ?: false) ||
                        (it.department?.contains(searchQuery, ignoreCase = true) ?: false)
            }
        }
}
