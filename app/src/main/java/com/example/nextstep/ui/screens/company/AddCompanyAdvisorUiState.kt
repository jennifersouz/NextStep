package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes

data class AddCompanyAdvisorUiState(
    val email: String = "",
    val isSaving: Boolean = false,
    @StringRes val emailErrorRes: Int? = null,
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val successMessageRes: Int? = null
)