package com.example.nextstep.ui.screens.company

import android.util.Log
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

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun loadOffer(offerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null,
                successMessage = null
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
            _uiState.value = _uiState.value.copy(
                isActionLoading = true,
                errorMessageRes = null,
                successMessage = null
            )

            val result = repository.changeOfferActiveStatus(
                offerId = offerId,
                isActive = false
            )

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    offer = result.getOrNull(),
                    isActionLoading = false,
                    successMessage = "Oferta desativada com sucesso."
                )
            } else {
                _uiState.value.copy(
                    isActionLoading = false,
                    errorMessageRes = R.string.company_offer_deactivate_error
                )
            }
        }
    }

    fun activateOffer(
        offerId: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isActionLoading = true,
                errorMessageRes = null,
                successMessage = null
            )

            val result = repository.changeOfferActiveStatus(
                offerId = offerId,
                isActive = true
            )

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    offer = result.getOrNull(),
                    isActionLoading = false,
                    successMessage = "Oferta ativada com sucesso."
                )
            } else {
                _uiState.value.copy(
                    isActionLoading = false,
                    errorMessageRes = R.string.company_offer_activate_error
                )
            }
        }
    }

    fun archiveOffer(
        offerId: String,
        reason: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isActionLoading = true,
                errorMessageRes = null,
                successMessage = null
            )

            val result = repository.archiveOffer(
                offerId = offerId,
                reason = reason
            )

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    offer = result.getOrNull(),
                    isActionLoading = false,
                    successMessage = "Oferta removida com sucesso."
                )
            } else {
                _uiState.value.copy(
                    isActionLoading = false,
                    errorMessageRes = R.string.company_offer_archive_error
                )
            }
        }
    }
}