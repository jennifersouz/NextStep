package com.example.nextstep.ui.screens.student

import com.example.nextstep.data.model.OfferDto

data class StudentDashboardUiState(
    val searchQuery: String = "",
    val offers: List<OfferDto> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val filteredOffers: List<OfferDto>
        get() {
            if (searchQuery.isBlank()) return offers

            return offers.filter { offer ->
                offer.title.contains(searchQuery, ignoreCase = true) ||
                        offer.companyName.contains(searchQuery, ignoreCase = true) ||
                        offer.location.contains(searchQuery, ignoreCase = true)
            }
        }
}