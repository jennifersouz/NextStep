package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyOffersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyOfferDetailViewModel : ViewModel() {

    private val repository = CompanyOffersRepository()

    private val _uiState = MutableStateFlow(CompanyOfferDetailUiState())
    val uiState: StateFlow<CompanyOfferDetailUiState> = _uiState.asStateFlow()

    fun loadOffer(offerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getCompanyOfferById(offerId)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    offer = result.getOrNull(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.company_offer_load_error
                )
            }
        }
    }

    fun deactivateOffer(
        offerId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true)

            val result = repository.deactivateOffer(offerId)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isUpdating = false)
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isUpdating = false,
                    errorMessageRes = R.string.company_offer_deactivate_error
                )
            }
        }
    }

    fun activateOffer(
        offerId: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true)

            val result = repository.activateOffer(offerId)

            if (result.isSuccess) {
                loadOffer(offerId)
            } else {
                _uiState.value = _uiState.value.copy(
                    isUpdating = false,
                    errorMessageRes = R.string.company_offer_activate_error
                )
            }
        }
    }
}