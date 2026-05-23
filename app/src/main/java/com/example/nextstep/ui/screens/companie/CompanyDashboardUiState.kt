package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyInternshipDto

enum class InternshipStatusFilter(
    val value: String
) {
    PENDING("pending"),
    COMPLETED("completed")
}

data class CompanyDashboardUiState(
    val internships: List<CompanyInternshipDto> = emptyList(),
    val selectedStatus: InternshipStatusFilter = InternshipStatusFilter.PENDING,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
) {
    val filteredInternships: List<CompanyInternshipDto>
        get() = internships.filter { internship ->
            internship.status == selectedStatus.value
        }
}