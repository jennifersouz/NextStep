package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.StudentSubmittedApplicationDto

data class StudentSubmittedApplicationDetailUiState(
    val application: StudentSubmittedApplicationDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,

    val isConfirmingPresence: Boolean = false,
    @StringRes val confirmPresenceErrorRes: Int? = null,

    val isOpeningDocument: Boolean = false,
    @StringRes val documentErrorRes: Int? = null,
    val documentUrlToOpen: String? = null
)