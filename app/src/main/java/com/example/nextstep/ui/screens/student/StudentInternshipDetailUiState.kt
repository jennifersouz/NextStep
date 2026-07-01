package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.AdvisorTaskListItemDto
import com.example.nextstep.data.model.StudentSubmittedApplicationDto

data class StudentInternshipDetailUiState(
    val internship: StudentSubmittedApplicationDto? = null,
    val tasks: List<AdvisorTaskListItemDto> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    val showAddTaskDialog: Boolean = false,
    val taskTitle: String = "",
    val isSavingTask: Boolean = false,
    val taskError: String? = null,
    val taskErrorRes: Int? = null,
    val reportFileName: String? = null,
    val isUploadingReport: Boolean = false,
    val reportErrorMessage: String? = null,
    val reportSuccessMessage: String? = null
)
