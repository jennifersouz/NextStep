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

enum class OfferFilter(val label: String) {
    ALL("Todas"),
    ACTIVE("Ativas"),
    INACTIVE("Inativas"),
    ARCHIVED("Arquivadas")
}