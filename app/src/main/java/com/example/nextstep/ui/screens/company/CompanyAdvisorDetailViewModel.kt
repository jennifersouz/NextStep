package com.example.nextstep.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.CompanyAdvisorsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompanyAdvisorDetailViewModel : ViewModel() {

    private val repository = CompanyAdvisorsRepository()

    private val _uiState = MutableStateFlow(CompanyAdvisorDetailUiState())
    val uiState: StateFlow<CompanyAdvisorDetailUiState> = _uiState.asStateFlow()

    fun loadAdvisor(advisorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getAdvisorById(advisorId)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    advisor = result.getOrNull(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.advisors_load_error
                )
            }
        }
    }

    fun deleteAdvisor(
        advisorId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeleting = true,
                errorMessageRes = null
            )

            val result = repository.deleteAdvisor(advisorId)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false
                )

                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessageRes = R.string.advisor_delete_error
                )
            }
        }
    }
}