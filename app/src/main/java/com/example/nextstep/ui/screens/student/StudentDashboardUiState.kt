package com.example.nextstep.ui.screens.student

import com.example.nextstep.data.model.OfferDto

data class StudentDashboardUiState(
    val searchQuery: String = "",
    val offers: List<OfferDto> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val unreadNotificationsCount: Int = 0,

    val selectedArea: String? = null,
    val selectedWorkMode: String? = null,
    val selectedLocation: String? = null,
    val onlyWithVacancies: Boolean = false
) {
    val availableAreas: List<String>
        get() = offers
            .map { offer -> offer.area.orEmpty().trim() }
            .filter { area -> area.isNotBlank() }
            .distinct()
            .sorted()

    val availableWorkModes: List<String>
        get() = offers
            .map { offer -> offer.workMode.orEmpty().trim() }
            .filter { workMode -> workMode.isNotBlank() }
            .distinct()
            .sorted()

    val availableLocations: List<String>
        get() = offers
            .map { offer -> offer.location.orEmpty().trim() }
            .filter { location -> location.isNotBlank() }
            .distinct()
            .sorted()

    val activeFiltersCount: Int
        get() = listOfNotNull(
            selectedArea,
            selectedWorkMode,
            selectedLocation
        ).size + if (onlyWithVacancies) 1 else 0

    val filteredOffers: List<OfferDto>
        get() {
            var filtered = offers

            if (searchQuery.isNotBlank()) {
                val query = searchQuery.trim()

                filtered = filtered.filter { offer ->
                    offer.title.contains(query, ignoreCase = true) ||
                            offer.companyName.contains(query, ignoreCase = true) ||
                            offer.area.orEmpty().contains(query, ignoreCase = true) ||
                            offer.location.orEmpty().contains(query, ignoreCase = true) ||
                            offer.workMode.orEmpty().contains(query, ignoreCase = true)
                }
            }

            if (!selectedArea.isNullOrBlank()) {
                filtered = filtered.filter { offer ->
                    offer.area.orEmpty().equals(selectedArea, ignoreCase = true)
                }
            }

            if (!selectedWorkMode.isNullOrBlank()) {
                filtered = filtered.filter { offer ->
                    offer.workMode.orEmpty().equals(selectedWorkMode, ignoreCase = true)
                }
            }

            if (!selectedLocation.isNullOrBlank()) {
                filtered = filtered.filter { offer ->
                    offer.location.orEmpty().equals(selectedLocation, ignoreCase = true)
                }
            }

            if (onlyWithVacancies) {
                filtered = filtered.filter { offer ->
                    (offer.vacancies ?: 0) > 0
                }
            }

            return filtered
        }
}