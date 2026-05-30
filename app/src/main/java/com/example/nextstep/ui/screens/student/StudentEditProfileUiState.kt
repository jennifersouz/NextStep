package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes

data class StudentEditProfileUiState(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val educationInstitution: String = "",

    val firstNameErrorRes: Int? = null,
    val lastNameErrorRes: Int? = null,
    val educationInstitutionErrorRes: Int? = null,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    @StringRes val errorMessageRes: Int? = null,
    @StringRes val successMessageRes: Int? = null
)