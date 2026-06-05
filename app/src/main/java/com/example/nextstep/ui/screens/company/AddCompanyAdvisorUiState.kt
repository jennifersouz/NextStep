package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes

data class AddCompanyAdvisorUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val department: String = "",

    val isSaving: Boolean = false,

    @StringRes val nameErrorRes: Int? = null,
    @StringRes val emailErrorRes: Int? = null,
    @StringRes val errorMessageRes: Int? = null
)