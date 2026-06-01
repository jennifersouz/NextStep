package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes

data class CompanyEditOfferUiState(
    val title: String = "",
    val description: String = "",
    val area: String = "",
    val location: String = "",
    val workMode: String = "",
    val duration: String = "",
    val vacancies: String = "",
    val requirements: String = "",

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    @StringRes val titleErrorRes: Int? = null,
    @StringRes val vacanciesErrorRes: Int? = null,
    @StringRes val errorMessageRes: Int? = null
)