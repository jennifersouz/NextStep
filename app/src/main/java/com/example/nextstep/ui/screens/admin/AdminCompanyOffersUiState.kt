package com.example.nextstep.ui.screens.admin

import androidx.annotation.StringRes
import com.example.nextstep.data.model.AdminCompanyOfferDto

data class AdminCompanyOffersUiState(
    val isLoading: Boolean = false,
    val offers: List<AdminCompanyOfferDto> = emptyList(),
    @StringRes val errorMessageRes: Int? = null
)
