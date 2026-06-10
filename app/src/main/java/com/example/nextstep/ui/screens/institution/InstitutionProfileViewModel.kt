package com.example.nextstep.ui.screens.institution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextstep.R
import com.example.nextstep.data.repository.InstitutionProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InstitutionProfileViewModel : ViewModel() {

    private val repository = InstitutionProfileRepository()

    private val _uiState = MutableStateFlow(InstitutionProfileUiState())
    val uiState: StateFlow<InstitutionProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            val result = repository.getInstitutionProfile()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    profile = result.getOrNull(),
                    isLoading = false,
                    errorMessageRes = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.institution_profile_load_error
                )
            }
        }
    }
}