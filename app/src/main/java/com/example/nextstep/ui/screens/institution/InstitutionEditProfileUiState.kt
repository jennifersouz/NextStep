package com.example.nextstep.ui.screens.institution

import androidx.annotation.StringRes

data class InstitutionEditProfileUiState(
    val name: String = "",
    val nif: String = "",
    val locality: String = "",
    val address: String = "",
    val phone: String = "",

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    @StringRes val nameErrorRes: Int? = null,
    @StringRes val nifErrorRes: Int? = null,
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val successMessageRes: Int? = null
)