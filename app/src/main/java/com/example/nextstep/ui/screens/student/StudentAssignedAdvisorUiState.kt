package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.StudentAssignedAdvisorDto

data class StudentAssignedAdvisorUiState(
    val assignedAdvisor: StudentAssignedAdvisorDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)