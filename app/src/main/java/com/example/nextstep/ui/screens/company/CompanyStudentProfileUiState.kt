package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyStudentProfileDto

data class CompanyStudentProfileUiState(
    val profile: CompanyStudentProfileDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)