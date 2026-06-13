package com.example.nextstep.ui.screens.company

data class CompanyEditOfferUiState(
    val offerId: String = "",
    val title: String = "",
    val description: String = "",
    val area: String = "",
    val location: String = "",
    val workMode: String = "",
    val duration: String = "",
    val vacancies: String = "",
    val requirements: String = "",
    val isActive: Boolean = true,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,

    val titleError: String? = null,
    val descriptionError: String? = null,
    val areaError: String? = null,
    val locationError: String? = null,
    val workModeError: String? = null,
    val durationError: String? = null,
    val vacanciesError: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)