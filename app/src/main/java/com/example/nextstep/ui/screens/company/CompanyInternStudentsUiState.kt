package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyInternStudentDto

data class CompanyInternStudentsUiState(
    val students: List<CompanyInternStudentDto> = emptyList(),
    val filteredStudents: List<CompanyInternStudentDto> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: CompanyInternStudentsFilter = CompanyInternStudentsFilter.ALL,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)

enum class CompanyInternStudentsFilter(val label: String) {
    ALL("Todos"),
    ACTIVE("Ativos"),
    INACTIVE("Inativos"),
    COMPLETED("Concluídos")
}