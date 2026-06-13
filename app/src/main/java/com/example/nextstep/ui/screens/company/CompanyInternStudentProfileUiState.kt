package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyInternStudentProfileDto

data class CompanyInternStudentProfileUiState(
    val profile: CompanyInternStudentProfileDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
    val isOpeningDocument: Boolean = false,
    @StringRes val documentErrorRes: Int? = null,
    val documentUrlToOpen: String? = null
)