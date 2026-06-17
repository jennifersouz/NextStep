package com.example.nextstep.ui.screens.student

import android.util.Log
import com.example.nextstep.data.model.OfferDto
import java.util.Locale

data class StudentDashboardUiState(
    val searchQuery: String = "",
    val studentName: String = "",
    val offers: List<OfferDto> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingName: Boolean = true,
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
            Log.d("FILTER_DEBUG", "=== Filter Debug ===")
            Log.d("FILTER_DEBUG", "Total offers before filtering: ${offers.size}")
            offers.forEach { offer ->
                Log.d("FILTER_DEBUG", "  offer.id=${offer.id} workMode='${offer.workMode}'")
            }
            Log.d("FILTER_DEBUG", "selectedWorkMode=$selectedWorkMode")
            Log.d("FILTER_DEBUG", "selectedArea=$selectedArea")
            Log.d("FILTER_DEBUG", "selectedLocation=$selectedLocation")
            Log.d("FILTER_DEBUG", "onlyWithVacancies=$onlyWithVacancies")
            Log.d("FILTER_DEBUG", "searchQuery='$searchQuery'")

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
                    offer.area.orEmpty().trim().equals(selectedArea.trim(), ignoreCase = true)
                }
            }

            if (!selectedWorkMode.isNullOrBlank()) {
                val normalizedSelected = normalizeWorkMode(selectedWorkMode)
                Log.d("FILTER_DEBUG", "Normalized selectedWorkMode=$normalizedSelected")
                filtered = filtered.filter { offer ->
                    val normalizedOffer = normalizeWorkMode(offer.workMode)
                    Log.d("FILTER_DEBUG", "  offer.id=${offer.id} workMode='${offer.workMode}' -> normalized='$normalizedOffer' match=${normalizedOffer == normalizedSelected}")
                    normalizedOffer == normalizedSelected
                }
            }

            if (!selectedLocation.isNullOrBlank()) {
                filtered = filtered.filter { offer ->
                    offer.location.orEmpty().trim().equals(selectedLocation.trim(), ignoreCase = true)
                }
            }

            if (onlyWithVacancies) {
                filtered = filtered.filter { offer ->
                    (offer.vacancies ?: 0) > 0
                }
            }

            Log.d("FILTER_DEBUG", "Filtered count: ${filtered.size}")
            return filtered
        }
}

internal fun normalizeWorkMode(value: String?): String? {
    return when (value?.trim()?.lowercase(Locale.ROOT)) {
        "remote", "remoto" -> "remoto"
        "onsite", "on-site", "presencial" -> "presencial"
        "hybrid", "hibrido", "híbrido" -> "hibrido"
        else -> value?.trim()?.lowercase(Locale.ROOT)
    }
}