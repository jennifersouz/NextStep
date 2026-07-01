package com.example.nextstep.ui.screens.institution

import androidx.annotation.StringRes
import com.example.nextstep.data.model.InstitutionStudentDto
import com.example.nextstep.data.repository.InstitutionRepository

data class InstitutionStudentsUiState(
    val students: List<InstitutionStudentDto> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
    val filter: InstitutionRepository.ArchiveFilter = InstitutionRepository.ArchiveFilter.ACTIVE
)

data class InstitutionStudentDetailUiState(
    val student: InstitutionStudentDto? = null,
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val successMessageRes: Int? = null
)
