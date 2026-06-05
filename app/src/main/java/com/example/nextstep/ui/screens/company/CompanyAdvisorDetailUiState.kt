package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyAdvisorDto

data class CompanyAdvisorDetailUiState(
    val advisor: CompanyAdvisorDto? = null,
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    @StringRes val errorMessageRes: Int? = null
)