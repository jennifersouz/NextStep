package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes

data class CompanyEditProfileUiState(
    val companyName: String = "",
    val businessArea: String = "",
    val location: String = "",
    val description: String = "",
    val phone: String = "",

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    @StringRes val companyNameErrorRes: Int? = null,
    @StringRes val phoneErrorRes: Int? = null,
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val successMessageRes: Int? = null
)