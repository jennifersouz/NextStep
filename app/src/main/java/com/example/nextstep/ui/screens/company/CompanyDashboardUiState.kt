package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyInternshipDto

enum class InternshipStatusFilter {
    PENDING,
    COMPLETED
}

data class CompanyDashboardUiState(
    val internships: List<CompanyInternshipDto> = emptyList(),
    val selectedStatus: InternshipStatusFilter = InternshipStatusFilter.PENDING,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
) {
    val filteredInternships: List<CompanyInternshipDto>
        get() {
            return internships.filter { internship ->
                when (selectedStatus) {
                    InternshipStatusFilter.PENDING -> internship.status == "pending"
                    InternshipStatusFilter.COMPLETED -> internship.status == "completed"
                }
            }
        }
}