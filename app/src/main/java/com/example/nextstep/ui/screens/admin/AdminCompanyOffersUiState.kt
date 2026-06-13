package com.example.nextstep.ui.screens.admin

import com.example.nextstep.data.model.AdminCompanyOfferDto

data class AdminCompanyOffersUiState(
    val isLoading: Boolean = false,
    val offers: List<AdminCompanyOfferDto> = emptyList(),
    val errorMessage: String? = null
)
