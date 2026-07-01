package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyApplicationDto

data class CompanyApplicationsUiState(
    val applications: List<CompanyApplicationDto> = emptyList(),
    val selectedFilter: ApplicationStatusFilter = ApplicationStatusFilter.ALL,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)