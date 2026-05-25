package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyApplicationDto

data class CompanyApplicationDetailUiState(
    val application: CompanyApplicationDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,

    val isUpdatingStatus: Boolean = false,
    @StringRes val statusErrorRes: Int? = null,

    val isOpeningDocument: Boolean = false,
    @StringRes val documentErrorRes: Int? = null,
    val documentUrlToOpen: String? = null
)