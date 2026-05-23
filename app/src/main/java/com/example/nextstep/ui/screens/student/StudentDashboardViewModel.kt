package com.example.nextstep.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.OffersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentDashboardViewModel : ViewModel() {

    private val offersRepository = OffersRepository()

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadOffers()
    }

    fun onSearchChange(value: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = value
        )
    }

    fun loadOffers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = offersRepository.getActiveOffers()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    offers = result.getOrDefault(emptyList()),
                    isLoading = false,
                    errorMessage = null
                )
            } else {
                _uiState.value.copy(
                    offers = emptyList(),
                    isLoading = false,
                    errorMessage = "Não foi possível carregar as ofertas. Tente novamente."
                )
            }
        }
    }
}