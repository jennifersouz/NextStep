package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.StudentSavedOffersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentSavedOffersViewModel : ViewModel() {

    private val repository = StudentSavedOffersRepository()

    private val _uiState = MutableStateFlow(StudentSavedOffersUiState())
    val uiState: StateFlow<StudentSavedOffersUiState> = _uiState.asStateFlow()

    init {
        loadSavedOffers()
    }

    fun loadSavedOffers() {
        viewModelScope.launch {
            val hasCurrentData = _uiState.value.offers.isNotEmpty()

            _uiState.value = _uiState.value.copy(
                isLoading = !hasCurrentData,
                errorMessageRes = null
            )

            val result = repository.getSavedOffers()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    offers = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.saved_offers_load_error
                )
            }
        }
    }
}