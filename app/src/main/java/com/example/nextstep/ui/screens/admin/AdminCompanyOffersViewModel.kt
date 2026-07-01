package com.example.nextstep.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.AdminCompaniesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminCompanyOffersViewModel : ViewModel() {

    private val repository = AdminCompaniesRepository()

    private val _uiState = MutableStateFlow(AdminCompanyOffersUiState(isLoading = true))
    val uiState: StateFlow<AdminCompanyOffersUiState> = _uiState.asStateFlow()

    fun loadOffers(companyProfileId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageRes = null)

            val result = repository.getOffersByCompany(companyProfileId)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    offers = result.getOrDefault(emptyList())
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.error_loading_offers
                )
            }
        }
    }
}
