package com.example.nextstep.ui.screens.institution

import androidx.annotation.StringRes
import com.example.nextstep.data.model.InstitutionTeacherDto
import com.example.nextstep.data.repository.InstitutionRepository

data class InstitutionTeachersUiState(
    val teachers: List<InstitutionTeacherDto> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
    val filter: InstitutionRepository.ArchiveFilter = InstitutionRepository.ArchiveFilter.ACTIVE
)

data class InstitutionTeacherDetailUiState(
    val teacher: InstitutionTeacherDto? = null,
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val successMessageRes: Int? = null
)
