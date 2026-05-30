package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.StudentSubmittedApplicationDto

data class StudentSubmittedApplicationsUiState(
    val applications: List<StudentSubmittedApplicationDto> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)