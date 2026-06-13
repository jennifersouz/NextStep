package com.example.nextstep.ui.screens.student

import com.example.nextstep.data.model.AdvisorTaskListItemDto
import com.example.nextstep.data.model.StudentSubmittedApplicationDto

data class StudentInternshipDetailUiState(
    val internship: StudentSubmittedApplicationDto? = null,
    val tasks: List<AdvisorTaskListItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
