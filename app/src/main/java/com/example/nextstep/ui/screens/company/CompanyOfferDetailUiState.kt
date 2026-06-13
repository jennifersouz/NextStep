package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.OfferDto

data class CompanyOfferDetailUiState(
    val offer: OfferDto? = null,
    val isLoading: Boolean = true,
    val isActionLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
    val successMessage: String? = null
)