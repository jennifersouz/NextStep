package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.InstitutionOptionDto

data class StudentEditProfileUiState(
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val educationInstitution: String = "",

    // Institution dropdown
    val selectedInstitutionId: String = "",
    val selectedInstitutionName: String = "",
    val availableInstitutions: List<InstitutionOptionDto> = emptyList(),
    val isLoadingInstitutions: Boolean = false,

    val firstNameErrorRes: Int? = null,
    val lastNameErrorRes: Int? = null,
    val educationInstitutionErrorRes: Int? = null,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    @StringRes val errorMessageRes: Int? = null,
    @StringRes val successMessageRes: Int? = null
)