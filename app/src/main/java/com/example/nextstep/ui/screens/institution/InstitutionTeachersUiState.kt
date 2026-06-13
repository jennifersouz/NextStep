package com.example.nextstep.ui.screens.institution

import com.example.nextstep.data.model.InstitutionTeacherDto
import com.example.nextstep.data.repository.InstitutionRepository

data class InstitutionTeachersUiState(
    val teachers: List<InstitutionTeacherDto> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val filter: InstitutionRepository.ArchiveFilter = InstitutionRepository.ArchiveFilter.ACTIVE
)

data class InstitutionTeacherDetailUiState(
    val teacher: InstitutionTeacherDto? = null,
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
