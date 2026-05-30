package com.example.nextstep.ui.screens.student

import androidx.annotation.StringRes
import com.example.nextstep.data.model.OfferDto

data class StudentOfferDetailUiState(
    val offer: OfferDto? = null,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,

    val hasApplied: Boolean = false,
    val isCheckingApplication: Boolean = false,

    val isSaved: Boolean = false,
    val isCheckingSavedOffer: Boolean = true,
    val isSavingOffer: Boolean = false,
    @StringRes val saveOfferErrorRes: Int? = null
)