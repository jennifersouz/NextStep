package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.StudentAvailableAdvisorDto

data class StudentAdvisorsUiState(
    val advisors: List<StudentAvailableAdvisorDto> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val sendingAdvisorId: String? = null,
    @StringRes val errorMessageRes: Int? = null
) {
    val filteredAdvisors: List<StudentAvailableAdvisorDto>
        get() {
            if (searchQuery.isBlank()) return advisors

            val query = searchQuery.trim()

            return advisors.filter { advisor ->
                advisor.name.contains(query, ignoreCase = true) ||
                        advisor.email.orEmpty().contains(query, ignoreCase = true) ||
                        advisor.department.orEmpty().contains(query, ignoreCase = true)
            }
        }
}