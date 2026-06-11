package com.example.nextstep.ui.screens.advisor

import com.example.nextstep.data.model.AdvisorAssignedStudentDto

data class AdvisorStudentsUiState(
    val isLoading: Boolean = false,
    val students: List<AdvisorAssignedStudentDto> = emptyList(),
    val selectedFilter: AdvisorStudentsFilter = AdvisorStudentsFilter.ALL,
    val searchQuery: String = "",
    val errorMessage: String? = null
)