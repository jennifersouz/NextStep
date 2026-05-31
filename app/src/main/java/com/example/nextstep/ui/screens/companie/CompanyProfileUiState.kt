package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyProfileDto
import com.example.nextstep.data.model.OfferDto

data class CompanyProfileUiState(
    val company: CompanyProfileDto? = null,
    val offers: List<OfferDto> = emptyList(),
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)