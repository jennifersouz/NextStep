package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.OffersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentOfferDetailViewModel : ViewModel() {

    private val offersRepository = OffersRepository()

    private val _uiState = MutableStateFlow(StudentOfferDetailUiState())
    val uiState: StateFlow<StudentOfferDetailUiState> = _uiState.asStateFlow()

    fun loadOffer(offerId: String) {
        if (offerId.isBlank()) {
            _uiState.value = StudentOfferDetailUiState(
                isLoading = false,
                errorMessageRes = R.string.offer_detail_not_found
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = offersRepository.getOfferById(offerId)

            _uiState.value = if (result.isSuccess) {
                StudentOfferDetailUiState(
                    offer = result.getOrNull(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                StudentOfferDetailUiState(
                    offer = null,
                    isLoading = false,
                    errorMessageRes = R.string.offer_detail_load_error
                )
            }
        }
    }
}