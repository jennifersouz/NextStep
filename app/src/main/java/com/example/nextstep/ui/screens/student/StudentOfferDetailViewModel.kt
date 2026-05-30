package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.ApplicationsRepository
import com.example.nextstep.data.repository.OffersRepository
import com.example.nextstep.data.repository.StudentSavedOffersRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentOfferDetailViewModel : ViewModel() {

    private val offersRepository = OffersRepository()
    private val applicationsRepository = ApplicationsRepository()
    private val savedOffersRepository = StudentSavedOffersRepository()

    private val _uiState = MutableStateFlow(StudentOfferDetailUiState())
    val uiState: StateFlow<StudentOfferDetailUiState> = _uiState.asStateFlow()

    fun loadOffer(offerId: String) {
        if (offerId.isBlank()) {
            _uiState.value = StudentOfferDetailUiState(
                isLoading = false,
                errorMessageRes = R.string.offer_detail_not_found,
                isCheckingSavedOffer = false
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = StudentOfferDetailUiState(
                isLoading = true,
                errorMessageRes = null,
                isCheckingApplication = true,
                isCheckingSavedOffer = true
            )

            val offerResult = offersRepository.getOfferById(offerId)

            if (offerResult.isFailure) {
                _uiState.value = StudentOfferDetailUiState(
                    offer = null,
                    isLoading = false,
                    errorMessageRes = R.string.offer_detail_load_error,
                    isCheckingApplication = false,
                    isCheckingSavedOffer = false
                )
                return@launch
            }

            val offer = offerResult.getOrNull()

            _uiState.value = _uiState.value.copy(
                offer = offer,
                isLoading = false,
                errorMessageRes = null,
                isCheckingApplication = true,
                isCheckingSavedOffer = true
            )

            val appliedDeferred = async {
                applicationsRepository.hasCurrentStudentApplied(offerId)
            }

            val savedDeferred = async {
                savedOffersRepository.isOfferSaved(offerId)
            }

            val applicationResult = appliedDeferred.await()
            val savedResult = savedDeferred.await()

            _uiState.value = _uiState.value.copy(
                hasApplied = applicationResult.getOrDefault(false),
                isSaved = savedResult.getOrDefault(false),
                isCheckingApplication = false,
                isCheckingSavedOffer = false
            )
        }
    }

    fun toggleSavedOffer() {
        val offer = _uiState.value.offer ?: return

        if (_uiState.value.isCheckingSavedOffer || _uiState.value.isSavingOffer) {
            return
        }

        val currentlySaved = _uiState.value.isSaved

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSavingOffer = true,
                saveOfferErrorRes = null
            )

            val result = if (currentlySaved) {
                savedOffersRepository.unsaveOffer(offer.id)
            } else {
                savedOffersRepository.saveOffer(offer.id)
            }

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    isSaved = !currentlySaved,
                    isSavingOffer = false,
                    saveOfferErrorRes = null
                )
            } else {
                _uiState.value.copy(
                    isSavingOffer = false,
                    saveOfferErrorRes = R.string.saved_offer_update_error
                )
            }
        }
    }
}