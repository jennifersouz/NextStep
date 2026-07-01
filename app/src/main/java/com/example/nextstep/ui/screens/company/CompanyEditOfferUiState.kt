package com.example.nextstep.ui.screens.company

data class CompanyEditOfferUiState(
    val offerId: String = "",
    val title: String = "",
    val description: String = "",
    val selectedArea: OfferArea? = null,
    val location: String = "",
    val workMode: String = "",
    val duration: String = "",
    val vacancies: String = "",
    val requirements: String = "",
    val isActive: Boolean = true,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    val titleError: Int? = null,
    val descriptionError: Int? = null,
    val areaError: Int? = null,
    val locationError: Int? = null,
    val workModeError: Int? = null,
    val durationError: Int? = null,
    val vacanciesError: Int? = null,
    val errorMessage: String? = null,
    val errorMessageRes: Int? = null,
    val successMessage: String? = null,
    val successMessageRes: Int? = null
)
