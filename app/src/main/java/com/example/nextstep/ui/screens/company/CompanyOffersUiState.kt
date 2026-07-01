package com.example.nextstep.ui.screens.company

import androidx.annotation.StringRes
import com.example.nextstep.data.model.CompanyOfferDto

data class CompanyOffersUiState(
    val offers: List<CompanyOfferDto> = emptyList(),
    val filteredOffers: List<CompanyOfferDto> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: OfferFilter = OfferFilter.ACTIVE,
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null
)

enum class OfferFilter(val labelRes: Int) {
    ALL(com.example.nextstep.R.string.company_filter_all),
    ACTIVE(com.example.nextstep.R.string.company_filter_active),
    INACTIVE(com.example.nextstep.R.string.company_filter_inactive),
    ARCHIVED(com.example.nextstep.R.string.company_filter_archived)
}