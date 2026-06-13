package com.example.nextstep.ui.screens.company

import com.example.nextstep.data.model.CompanyInternStudentDto

data class CompanyInternStudentsUiState(
    val students: List<CompanyInternStudentDto> = emptyList(),
    val filteredStudents: List<CompanyInternStudentDto> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: CompanyInternStudentsFilter = CompanyInternStudentsFilter.ALL,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

enum class CompanyInternStudentsFilter(val label: String) {
    ALL("Todos"),
    ACTIVE("Ativos"),
    INACTIVE("Inativos"),
    COMPLETED("Concluídos")
}