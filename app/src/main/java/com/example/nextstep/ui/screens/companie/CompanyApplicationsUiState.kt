package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyApplicationDto

data class CompanyApplicationsUiState(
    val applications: List<CompanyApplicationDto> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
) {
    val unseenApplications: List<CompanyApplicationDto>
        get() = applications.filter { application ->
            !application.viewedByCompany
        }

    val seenApplications: List<CompanyApplicationDto>
        get() = applications.filter { application ->
            application.viewedByCompany
        }
}