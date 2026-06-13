package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.model.CompanyOfferDto
import com.example.nextstep.data.repository.CompanyOffersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyOffersViewModel : ViewModel() {

    private val repository = CompanyOffersRepository()

    private val _uiState = MutableStateFlow(CompanyOffersUiState())
    val uiState: StateFlow<CompanyOffersUiState> = _uiState.asStateFlow()

    init {
        loadOffers()
    }

    fun loadOffers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = repository.getMyOffers()

            _uiState.value = if (result.isSuccess) {
                val offers = result.getOrDefault(emptyList())
                _uiState.value.copy(
                    offers = offers,
                    filteredOffers = applyFilters(offers, _uiState.value.searchQuery, _uiState.value.selectedFilter),
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    offers = emptyList(),
                    filteredOffers = emptyList(),
                    isLoading = false,
                    errorMessage = "Não foi possível carregar as ofertas."
                )
            }
        }
    }

    fun refresh() {
        loadOffers()
    }

    fun onSearchChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onFilterChange(filter: OfferFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        _uiState.value = state.copy(
            filteredOffers = applyFilters(state.offers, state.searchQuery, state.selectedFilter)
        )
    }

    private fun applyFilters(
        offers: List<CompanyOfferDto>,
        searchQuery: String,
        filter: OfferFilter
    ): List<CompanyOfferDto> {
        var result = offers

        // Filter by status
        result = when (filter) {
            OfferFilter.ACTIVE -> result.filter {
                it.archivedAt == null && it.isActive == true
            }
            OfferFilter.INACTIVE -> result.filter {
                it.archivedAt == null && (it.isActive == false || it.isActive == null)
            }
            OfferFilter.ARCHIVED -> result.filter {
                it.archivedAt != null
            }
            OfferFilter.ALL -> result
        }

        // Filter by search query
        if (searchQuery.isNotBlank()) {
            val query = searchQuery.lowercase().trim()
            result = result.filter { offer ->
                offer.title?.lowercase()?.contains(query) == true ||
                        offer.area?.lowercase()?.contains(query) == true ||
                        offer.location?.lowercase()?.contains(query) == true ||
                        offer.workMode?.lowercase()?.contains(query) == true
            }
        }

        return result
    }
}