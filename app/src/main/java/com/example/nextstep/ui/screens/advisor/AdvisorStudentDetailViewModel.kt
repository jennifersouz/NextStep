package com.example.nextstep.ui.screens.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.data.repository.AdvisorStudentDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvisorStudentDetailViewModel : ViewModel() {

    private val repository = AdvisorStudentDetailRepository()

    private val _uiState = MutableStateFlow(AdvisorStudentDetailUiState())
    val uiState: StateFlow<AdvisorStudentDetailUiState> = _uiState.asStateFlow()

    fun loadDetail(applicationId: String) {
        viewModelScope.launch {
            _uiState.value = AdvisorStudentDetailUiState(isLoading = true)

            repository.getStudentDetail(applicationId)
                .onSuccess { detail ->
                    _uiState.value = AdvisorStudentDetailUiState(
                        isLoading = false,
                        detail = detail
                    )
                }
                .onFailure { exception ->
                    _uiState.value = AdvisorStudentDetailUiState(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
        }
    }

    fun refresh(applicationId: String) {
        loadDetail(applicationId)
    }
}